package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    @NotBlank(groups = {Create.class, Update.class})
    @Size(max = 1024)
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
