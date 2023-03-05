package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentValidationException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoSave;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComplete;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestService itemRequestService;
    private static final Sort ASC = Sort.by(Sort.Direction.ASC, "start");

    @Transactional
    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (!userService.isExist(userId))
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        ItemRequest request = null;
        if (null != itemDto.getRequestId())
            request = itemRequestService.getModel(itemDto.getRequestId());
        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto, userService.getUserModel(userId), request)));
    }

    @Transactional
    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        if (userId == item.getOwner().getId()) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) item.setName(itemDto.getName());
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
                item.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
            return itemMapper.toItemDto(item);
        } else throw new ForbiddenException("Редактировать вещи может только их владелец");
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoComplete get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Предмет с id " + itemId + " не найден.")
        );
        List<BookingDto> bookings = bookingRepository.getBookingsByItemApproved(itemId, userId, ASC)
                .stream().map(bookingMapper::toDto).collect(toList());
        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream().map(commentMapper::toCommentDto).collect(toList());
        return itemMapper.toItemDtoComplete(item, getLastBooking(bookings), getNextBooking(bookings), comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoComplete> getUserStuff(long userId, PageRequest pageRequest) {
        if (!userService.isExist(userId))
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        List<Item> items = itemRepository.getUserStuff(userId, pageRequest).toList();
        if (items.isEmpty()) {
            throw new ItemNotFoundException("У пользователя с id " + userId + " нет предметов для аренды");
        }
        Map<Long, List<BookingDto>> bookings = bookingRepository.getAllForOwnerApproved(userId, ASC).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.groupingBy(b -> b.getItem().getId(), toList()));

        Map<Long, List<CommentDto>> comments = commentRepository
                .findByUserId(userId).stream().map(commentMapper::toCommentDto).collect(Collectors.groupingBy(CommentDto::getItemId));

        return items.stream().map(item -> itemMapper.toItemDtoComplete(
                item,
                getLastBooking(bookings.getOrDefault(item.getId(), List.of())),
                getNextBooking(bookings.getOrDefault(item.getId(), List.of())),
                comments.getOrDefault(item.getId(), List.of())
        )).collect(toList());
    }

    private BookingDto getLastBooking(List<BookingDto> bookings) {
        return bookings.stream()
                .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                .reduce((first, second) -> second).orElse(null);
    }

    private BookingDto getNextBooking(List<BookingDto> bookings) {
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .findFirst().orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Предмет с id " + itemId + " не найден.")
        );
    }

    @Override
    public List<ItemDto> search(long userId, String text, PageRequest pageRequest) {
        List<Item> items = itemRepository.search(text, pageRequest).toList();
        if (items.isEmpty()) throw new ItemNotFoundException("По запросу " + text + " ничего не найдено");
        else return items.stream().map(itemMapper::toItemDto).collect(toList());
    }

    @Override
    public CommentDto createComment(long userId, Long itemId, CommentDtoSave commentDto) {
        User user = userService.getUserModel(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        List<Booking> bookings = bookingRepository.getAllForBookerWhereStatePastAll(userId, LocalDateTime.now(), ASC).stream()
                .filter(b -> b.getItem().getId() == itemId).collect(toList());
        if (bookings.isEmpty() || bookings.stream().map(b -> b.getItem().getId()).noneMatch(i -> i.equals(itemId)))
            throw new CommentValidationException("Невозможно добавить комментарий");
        return commentMapper.toCommentDto(commentRepository.save(commentMapper.toComment(user, item, commentDto)));
    }
}
