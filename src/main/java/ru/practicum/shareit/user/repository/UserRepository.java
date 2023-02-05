package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> get(long id);

    User create(User user);

    User update(User user, long userId);

    boolean isDuplicateEmail(UserDto userDto, long userId);

    void delete(long userId);

    List<User> getAll();

    boolean isExist(long userId);
}
