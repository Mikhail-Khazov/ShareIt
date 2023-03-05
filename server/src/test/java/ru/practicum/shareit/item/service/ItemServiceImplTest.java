package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentValidationException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoSave;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComplete;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    ItemServiceImpl itemService;
    ItemRepository itemRepository;
    UserServiceImpl userService;
    BookingRepository bookingRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    ItemRequestService itemRequestService;
    UserRepository userRepository;
    ItemRequestRepository requestRepository;
    ItemDto itemDto;
    Item item;
    Long id;
    ItemRequest itemRequest;
    User user;
    Booking booking;
    Comment comment;

    PageImpl<Item> page;

    @BeforeEach
    void initialize() {
        user = new User(1L, "John", "doe@ya.ru");
        id = 1L;
        requestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemMapper = new ItemMapper();
        bookingMapper = new BookingMapper();
        commentMapper = new CommentMapper();
        userService = new UserServiceImpl(userRepository, new UserMapper());
        itemRequestService = new ItemRequestServiceImpl(requestRepository, itemRepository,
                new ItemRequestMapper(), itemMapper, userService);
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper, bookingMapper,
                commentRepository, commentMapper, itemRequestService);
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("")
                .requestor(new User(2L, "Kevin", "kev@ya.ru"))
                .created(LocalDateTime.now().plusHours(1))
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .requestId(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("ha ha")
                .item(item)
                .author(user)
                .created(LocalDateTime.now().minusMinutes(2))
                .build();
        page = new PageImpl<>(List.of(item));
    }

    @Test
    void create_whenUserExistAndDtoNotNull_thenReturnItemDto() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemDto response = itemService.create(id, itemDto);

        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
        assertEquals(item.getRequest().getId(), response.getRequestId());
    }

    @Test
    void update_whenItemExistAndDtoReplaceAllFields_thenReturnUpdatedDto() {
        itemDto.setName("guitar");
        itemDto.setDescription("Greg Smallman");
        itemDto.setAvailable(false);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto response = itemService.update(id, id, itemDto);

        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
    }

    @Test
    void update_whenItemUpdateNotOwner_thenReturnForbiddenException() {
        itemDto.setName("guitar");
        itemDto.setDescription("Greg Smallman");
        itemDto.setAvailable(false);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(ForbiddenException.class, () -> itemService.update(5L, id, itemDto));
    }

    @Test
    void get_whenItemContainsBookingAndComment_thenReturnItemDtoComplete() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemDtoComplete response = itemService.get(id, id);

        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
    }

    @Test
    void get_whenItemNotFound_thenReturnItemNotFoundException() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.get(id, id));
    }

    @Test
    void getUserStuff_whenUserHaveItem_thenReturnListOfItemCompleteDto() {
        when(itemRepository.getUserStuff(anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        List<ItemDtoComplete> response = itemService.getUserStuff(id, PageRequest.of(0, 10));

        assertEquals(item.getName(), response.get(0).getName());
        assertEquals(item.getDescription(), response.get(0).getDescription());
        assertEquals(item.getAvailable(), response.get(0).getAvailable());
    }

    @Test
    void getUserStuff_whenUserHaveItemAndItemHaveBookings_thenReturnListOfItemCompleteDto() {
        Booking booking0 = Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(3))
                .item(item)
                .booker(user)
                .status(BookingStatus.CANCELED)
                .build();

        when(itemRepository.getUserStuff(anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.getAllForOwnerApproved(anyLong(), any(Sort.class)))
                .thenReturn(List.of(booking, booking0));

        List<ItemDtoComplete> response = itemService.getUserStuff(id, PageRequest.of(0, 10));

        assertEquals(item.getName(), response.get(0).getName());
        assertEquals(item.getDescription(), response.get(0).getDescription());
        assertEquals(item.getAvailable(), response.get(0).getAvailable());
    }

    @Test
    void getItem_whenItemExist_thenReturnItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Item response = itemService.getItem(id);

        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
    }

    @Test
    void search_whenItemExist_thenReturnListOfItem() {
        when(itemRepository.search(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        List<ItemDto> response = itemService.search(id, "text", PageRequest.of(0, 10));

        assertEquals(item.getName(), response.get(0).getName());
        assertEquals(item.getDescription(), response.get(0).getDescription());
        assertEquals(item.getAvailable(), response.get(0).getAvailable());
    }

    @Test
    void createComment_whenConditionsComplete_thenReturnCommentDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(bookingRepository.getAllForBookerWhereStatePastAll(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        CommentDto response = itemService.createComment(id, id, new CommentDtoSave("comment text"));

        assertEquals(comment.getText(), response.getText());
    }

    @Test
    void createComment_whenUserIsOwner_thenReturnCommentValidationException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(bookingRepository.getAllForBookerWhereStatePastAll(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of());

        assertThrows(CommentValidationException.class, () -> itemService.createComment(id, id, any(CommentDtoSave.class)));
    }
}