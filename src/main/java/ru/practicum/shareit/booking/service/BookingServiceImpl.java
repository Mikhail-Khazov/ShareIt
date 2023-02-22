package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final BookingRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final BookingMapper mapper;

    @Transactional
    @Override
    public BookingDto create(BookingDtoSave bookingDtoSave, Long bookerId) {
        User booker = userMapper.toUser(userService.get(bookerId));
        Item item = itemService.getItem(bookingDtoSave.getItemId());
        if (item.getOwner().getId() == booker.getId())
            throw new ItemNotFoundException("Невозможно зарезервировать свой предмет");
        if (!item.getAvailable()) throw new ItemNotAvailableException("Недоступно для бронирования");
        Booking booking = mapper.toBookingModel(bookingDtoSave, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toDto(repository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approve(Boolean isApproved, Long id, Long ownerId) {
        Booking booking = repository.findById(id).orElseThrow(BookingNotFoundException::new);
        if (!booking.getStatus().equals(BookingStatus.WAITING)) throw new WrongStateException("Статус уже установлен");
        if (ownerId != booking.getItem().getOwner().getId())
            throw new UserNotFoundException();
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.toDto(booking);
    }

    @Override
    public BookingDto get(Long id, Long userId) {
        Booking booking = repository.findById(id).orElseThrow(BookingNotFoundException::new);
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId())
            throw new UserNotFoundException();
        return mapper.toDto(booking);
    }

    private void userIsExistCheck(Long userId) {
        if (!userService.isExist(userId)) throw new UserNotFoundException();
    }

    @Override
    public List<BookingDto> getAllForBooker(BookingState state, Long bookerId, PageRequest pageRequest) {
        userIsExistCheck(bookerId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = repository.getAllForBooker(bookerId, pageRequest).toList();
                break;
            case FUTURE:
                bookings = repository.getAllForBookerWhereStateFuture(bookerId, LocalDateTime.now(), pageRequest).toList();
                break;
            case CURRENT:
                bookings = repository.getAllForBookerWhereStateCurrent(bookerId, LocalDateTime.now(), pageRequest).toList();
                break;
            case PAST:
                bookings = repository.getAllForBookerWhereStatePast(bookerId, LocalDateTime.now(), pageRequest).toList();
                break;
            default:
                bookings = repository.getAllForBookerWhereStateWaitingOrRejected(BookingStatus.from(state.toString()), bookerId, pageRequest).toList();
                break;
        }
        return bookings.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllForOwner(BookingState state, Long ownerId, PageRequest pageRequest) {
        userIsExistCheck(ownerId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = repository.getAllForOwner(ownerId, pageRequest).toList();
                break;
            case FUTURE:
                bookings = repository.getAllForOwnerWhereStateFuture(ownerId, LocalDateTime.now(), pageRequest).toList();
                break;
            case CURRENT:
                bookings = repository.getAllForOwnerWhereStateCurrent(ownerId, LocalDateTime.now(), pageRequest).toList();
                break;
            case PAST:
                bookings = repository.getAllForOwnerWhereStatePast(ownerId, LocalDateTime.now(), pageRequest).toList();
                break;
            default:
                bookings = repository.getAllForOwnerWhereStateWaitingOrRejected(BookingStatus.from(state.toString()), ownerId, pageRequest).toList();
                break;
        }
        return bookings.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
