package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoLite;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
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
