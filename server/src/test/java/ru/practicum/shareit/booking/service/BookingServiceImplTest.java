package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.WrongStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    BookingRepository bookingRepository;
    ItemService itemService;
    UserService userService;
    UserMapper userMapper;
    BookingMapper bookingMapper;
    BookingService bookingService;
    Booking booking;
    BookingDtoSave bookingDtoSave;
    User owner;
    Item item;
    Long id;
    UserDto userDto;
    UserRepository userRepository;
    User booker;
    PageImpl<Booking> page;


    @BeforeEach
    void initialize() {
        id = 2L;
        userMapper = new UserMapper();
        bookingMapper = new BookingMapper();
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemService = mock(ItemServiceImpl.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        bookingService = new BookingServiceImpl(itemService, bookingRepository, userService, userMapper, bookingMapper);
        owner = new User(1L, "John", "doe@ya.ru");
        booker = new User(2L, "Kevin", "kev@ya.ru");
        item = Item.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .owner(owner)
                .request(null)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingDtoSave = BookingDtoSave.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();
        userDto = userMapper.toUserDto(owner);
        page = new PageImpl<>(List.of(booking));
    }


    private void compare(BookingDto response) {
        assertEquals(booking.getItem().getId(), response.getItem().getId());
        assertEquals(booking.getBooker().getEmail(), response.getBooker().getEmail());
        assertEquals(booking.getStatus().toString(), response.getStatus());
    }

    @Test
    void create_whenConditionsCorrect_thenReturnBookingDto() {
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        BookingDto response = bookingService.create(bookingDtoSave, id);

        compare(response);
    }

    @Test
    void create_whenItemNotAvailable_thenItemNotAvailableException() {
        item.setAvailable(false);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(bookingDtoSave, id));
    }

    @Test
    void approve_whenBookingStatusWaiting_thenReturnBookingDtoWithNewStatus() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto response = bookingService.approve(true, booker.getId(), owner.getId());

        compare(response);
    }

    @Test
    void approve_whenBookingNotFound_thenReturnBookingNotFoundException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.approve(true, booker.getId(), owner.getId()));
    }

    @Test
    void approve_whenBookingHaveStateOtherThanWaiting_thenReturnWrongStateException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(WrongStateException.class, () -> bookingService.approve(true, booker.getId(), owner.getId()));
    }

    @Test
    void get_whenDateCorrect_thenReturnBookingDto() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto response = bookingService.get(id, booker.getId());

        compare(response);
    }

    @Test
    void get_whenBookingNotFound_thenReturnBookingNotFoundException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.get(id, booker.getId()));
    }

    @Test
    void getAllForBooker_whenStateAll_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForBooker(anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForBooker(BookingState.ALL, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForBooker_whenStateFuture_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForBookerWhereStateFuture(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForBooker(BookingState.FUTURE, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForBooker_whenStateCurrent_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForBookerWhereStateCurrent(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForBooker(BookingState.CURRENT, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForBooker_whenStatePast_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForBookerWhereStatePast(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForBooker(BookingState.PAST, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForBooker_whenStateWaitingOrRejected_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForBookerWhereStateWaitingOrRejected(any(BookingStatus.class), anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForBooker(BookingState.REJECTED, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForOwner_whenStateWaitingOrRejected_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForOwnerWhereStateWaitingOrRejected(any(BookingStatus.class), anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForOwner(BookingState.REJECTED, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForOwner_whenStateAll_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForOwner(BookingState.ALL, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForOwner_whenStateFuture_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForOwnerWhereStateFuture(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForOwner(BookingState.FUTURE, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForOwner_whenStateCurrent_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForOwnerWhereStateCurrent(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForOwner(BookingState.CURRENT, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void getAllForOwner_whenStatePast_thenReturnListOfBookingDto() {
        when(bookingRepository.getAllForOwnerWhereStatePast(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page);
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<BookingDto> response = bookingService.getAllForOwner(BookingState.PAST, booker.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

}