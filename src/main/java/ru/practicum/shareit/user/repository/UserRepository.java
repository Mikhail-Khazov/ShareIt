package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User get(long id);

    User create(User user);

    User update(User user, long userId);

    boolean isDuplicateEmail(UserDto userDto);

    void delete(long userId);

    List<User> getAll();

    boolean isExist(long userId);
}
