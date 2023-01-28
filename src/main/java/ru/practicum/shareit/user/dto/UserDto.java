package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class})
    @NotEmpty(groups = {Create.class})
    private String email;
}
