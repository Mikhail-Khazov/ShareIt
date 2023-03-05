package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.constraintAnnotation.StartBeforeEnd;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@StartBeforeEnd(groups = {Create.class, Update.class})
public class BookingDtoSave {
    @NotNull(groups = {Create.class}, message = "Необходимо указать дату старта бронирования")
    @FutureOrPresent(groups = {Create.class}, message = "Некорректная дата старта")
    private LocalDateTime start;

    @NotNull(groups = {Create.class}, message = "Необходимо указать дату завершения бронирования")
    @Future(groups = {Create.class}, message = "Некорректная дата завершения")
    private LocalDateTime end;

    @NotNull(groups = {Create.class}, message = "Необходимо указать itemId")
    private Long itemId;
}
