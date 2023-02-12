package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.constraintAnnotation.StartBeforeEnd;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.item.dto.ItemDtoLite;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@StartBeforeEnd(groups = {Create.class})             //Проверка даты старта и окончания аренды
public class BookingDto {
    private long id;

    @NotNull(groups = {Create.class}, message = "Необходимо указать дату старта бронирования")
    @FutureOrPresent(groups = {Create.class}, message = "Некорректная дата старта")
    private LocalDateTime start;

    @NotNull(groups = {Create.class}, message = "Необходимо указать дату завершения бронирования")
    @Future(groups = {Create.class}, message = "Некорректная дата завершения")
    private LocalDateTime end;

    @NotNull(groups = {Create.class}, message = "Необходимо указать itemId")
    private ItemDtoLite item;

    private UserDto booker;

    private String status;

    public void setItem(Item item) {
        this.item = new ItemDtoLite(item.getId(), item.getName(), item.getDescription());
    }

    public void setBooker(User booker) {
        this.booker = new UserDto(booker.getId(), booker.getName(), booker.getEmail());
    }
}
