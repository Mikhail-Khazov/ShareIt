package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc ")
    List<Booking> getAllForBooker(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?2 and b.status = ?1  order by b.start desc ")
    List<Booking> getAllForBookerWhereStateWaitingOrRejected(BookingStatus state, Long bookerId);

    @Query("select b from Booking b where b.id = ?1 and b.end < ?2 order by b.start desc ")
    List<Booking> getAllForBookerWhereStatePast(Long bookerId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start desc ")
    List<Booking> getAllForBookerWhereStateFuture(Long bookerId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc ")
    List<Booking> getAllForBookerWhereStateCurrent(Long bookerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc ")
    List<Booking> getAllForOwner(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc ")
    List<Booking> getAllForOwnerWhereStateFuture(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc ")
    List<Booking> getAllForOwnerWhereStateCurrent(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc ")
    List<Booking> getAllForOwnerWhereStatePast(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?2 and b.status = ?1 order by b.start desc ")
    List<Booking> getAllForOwnerWhereStateWaitingOrRejected(BookingStatus state, Long ownerId);

    @Query("select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 order by b.start ")
    List<Booking> getBookingsByItem(Long itemId, Long userId);
}
