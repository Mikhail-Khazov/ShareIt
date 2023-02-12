package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoSave bookingDto, Long bookerId);

    BookingDto approve(Boolean isApproved, Long id, Long ownerId);

    BookingDto get(Long id, Long userId);

    List<BookingDto> getAllForOwner(BookingState state, Long ownerId, Sort sort);

    List<BookingDto> getAllForBooker(BookingState state, Long bookerId, Sort sort);

}