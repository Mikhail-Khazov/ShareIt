package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        BookingDto dto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .build();
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        return dto;
    }

    public Booking toBookingModel(BookingDtoSave dto, Item item, User user) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }
}
