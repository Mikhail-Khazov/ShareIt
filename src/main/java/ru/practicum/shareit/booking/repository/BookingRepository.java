package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 ")
    List<Booking> getAllForBooker(Long bookerId, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?2 and b.status = ?1 ")
    List<Booking> getAllForBookerWhereStateWaitingOrRejected(BookingStatus state, Long bookerId, Sort sort);

    @Query("select b from Booking b where b.id = ?1 and b.end < ?2 ")
    List<Booking> getAllForBookerWhereStatePast(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 ")
    List<Booking> getAllForBookerWhereStateFuture(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 ")
    List<Booking> getAllForBookerWhereStateCurrent(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 ")
    List<Booking> getAllForOwner(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'APPROVED' ")
    List<Booking> getAllForOwnerApproved(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 ")
    List<Booking> getAllForOwnerWhereStateFuture(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 ")
    List<Booking> getAllForOwnerWhereStateCurrent(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 ")
    List<Booking> getAllForOwnerWhereStatePast(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?2 and b.status = ?1 ")
    List<Booking> getAllForOwnerWhereStateWaitingOrRejected(BookingStatus state, Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 ")
    List<Booking> getBookingsByItem(Long itemId, Long userId, Sort sort);
}
