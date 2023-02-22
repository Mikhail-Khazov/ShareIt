package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 ")
    Page<Booking> getAllForBooker(Long bookerId, PageRequest pageRequest);

    @Query("select b from Booking b where b.booker.id = ?2 and b.status = ?1 ")
    Page<Booking> getAllForBookerWhereStateWaitingOrRejected(BookingStatus state, Long bookerId, PageRequest pageRequest);

    @Query("select b from Booking b where b.id = ?1 and b.end < ?2 ")
    Page<Booking> getAllForBookerWhereStatePast(Long bookerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 ")
    Page<Booking> getAllForBookerWhereStateFuture(Long bookerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 ")
    Page<Booking> getAllForBookerWhereStateCurrent(Long bookerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.owner.id = ?1 ")
    Page<Booking> getAllForOwner(Long ownerId, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.owner.id = ?1 and upper(b.status) like ('APPROVED') ")
    List<Booking> getAllForOwnerApproved(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 ")
    Page<Booking> getAllForOwnerWhereStateFuture(Long ownerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 ")
    Page<Booking> getAllForOwnerWhereStateCurrent(Long ownerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 ")
    Page<Booking> getAllForOwnerWhereStatePast(Long ownerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.owner.id = ?2 and b.status = ?1 ")
    Page<Booking> getAllForOwnerWhereStateWaitingOrRejected(BookingStatus state, Long ownerId, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and upper(b.status) like ('APPROVED') ")
    List<Booking> getBookingsByItemApproved(Long itemId, Long userId, Sort sort);

    @Query("select b from Booking b where b.id = ?1 and b.end < ?2 ")
    List<Booking> getAllForBookerWhereStatePastAll(long userId, LocalDateTime now, Sort asc);
}
