package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoSave {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
