package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long userId);

    UserDto get(long userId);

    void delete(long userId);

    List<UserDto> getAll();

    boolean isExist(long userId);

    User getUserModel(long userId);
}
