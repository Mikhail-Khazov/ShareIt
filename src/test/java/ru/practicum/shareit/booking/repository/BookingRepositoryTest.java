package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.booking.enums.BookingStatus.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    User booker;
    User owner;
    Item item;
    Booking bookingCurrent;
    PageRequest page = PageRequest.of(0, 10);
    Booking bookingPastRejected;
    Booking bookingFutureWaiting;

    @BeforeEach
    void initialize() {
        booker = userRepository.save(new User(1L, "John", "doe@ya.ru"));
        owner = userRepository.save(new User(2L, "Kevin", "kev@ya.ru"));
        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .owner(owner)
                .request(null)
                .build());
        bookingCurrent = bookingRepository.save(Booking.builder()
                .id(1L)
                .start(now())
                .end(now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(APPROVED)
                .build());
        bookingPastRejected = bookingRepository.save(Booking.builder()
                .id(2L)
                .start(now().minusDays(10))
                .end(now().minusDays(8))
                .item(item)
                .booker(booker)
                .status(REJECTED)
                .build());
        bookingFutureWaiting = bookingRepository.save(Booking.builder()
                .id(3L)
                .start(now().plusDays(10))
                .end(now().plusDays(12))
                .item(item)
                .booker(booker)
                .status(WAITING)
                .build());
    }

    private void compare(Booking standard, List<Booking> response) {
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(standard.getId(), response.get(0).getId());
        assertEquals(standard.getStatus(), response.get(0).getStatus());
        assertEquals(standard.getItem().getName(), response.get(0).getItem().getName());
        assertEquals(standard.getBooker().getEmail(), response.get(0).getBooker().getEmail());
    }

    @Test
    void getAllForBooker() {
        List<Booking> response = bookingRepository.getAllForBooker(booker.getId(), page).toList();

        assertNotNull(response);
        assertEquals(3, response.size());
    }

    @Test
    void getAllForBookerWhereStateWaitingOrRejected() {
        List<Booking> response = bookingRepository
                .getAllForBookerWhereStateWaitingOrRejected(WAITING, booker.getId(), page).toList();

        compare(bookingFutureWaiting, response);
    }

    @Test
    void getAllForBookerWhereStatePast() {
        List<Booking> response = bookingRepository.getAllForBookerWhereStatePast(booker.getId(), now(), page).toList();

        compare(bookingPastRejected, response);
    }

    @Test
    void getAllForBookerWhereStateFuture() {
        List<Booking> response = bookingRepository.getAllForBookerWhereStateFuture(booker.getId(), now(), page).toList();

        compare(bookingFutureWaiting, response);
    }

    @Test
    void getAllForBookerWhereStateCurrent() {
        List<Booking> response = bookingRepository.getAllForBookerWhereStateCurrent(booker.getId(), now(), page).toList();

        compare(bookingCurrent, response);
    }

    @Test
    void getAllForOwner() {
        List<Booking> response = bookingRepository.getAllForOwner(owner.getId(), page).toList();

        assertNotNull(response);
        assertEquals(3, response.size());
    }

    @Test
    void getAllForOwnerApproved() {
        List<Booking> response = bookingRepository.getAllForOwnerApproved(owner.getId(), Sort.unsorted());

        compare(bookingCurrent, response);
    }

    @Test
    void getAllForOwnerWhereStateFuture() {
        List<Booking> response = bookingRepository.getAllForOwnerWhereStateFuture(owner.getId(), now(), page).toList();

        compare(bookingFutureWaiting, response);
    }

    @Test
    void getAllForOwnerWhereStateCurrent() {
        List<Booking> response = bookingRepository.getAllForOwnerWhereStateCurrent(owner.getId(), now(), page).toList();

        compare(bookingCurrent, response);
    }

    @Test
    void getAllForOwnerWhereStatePast() {
        List<Booking> response = bookingRepository.getAllForOwnerWhereStatePast(owner.getId(), now(), page).toList();

        compare(bookingPastRejected, response);
    }

    @Test
    void getAllForOwnerWhereStateWaitingOrRejected() {
        List<Booking> response = bookingRepository
                .getAllForOwnerWhereStateWaitingOrRejected(WAITING, owner.getId(), page).toList();

        compare(bookingFutureWaiting, response);
    }

    @Test
    void getBookingsByItemApproved() {
        List<Booking> response = bookingRepository.getBookingsByItemApproved(item.getId(), owner.getId(), Sort.unsorted());

        compare(bookingCurrent, response);
    }

    @Test
    void getAllForBookerWhereStatePastAll() {
        List<Booking> response = bookingRepository.getAllForBookerWhereStatePastAll(booker.getId(), now(), Sort.unsorted());

        compare(bookingPastRejected, response);
    }
}