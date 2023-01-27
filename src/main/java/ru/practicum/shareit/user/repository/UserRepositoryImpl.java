package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> storage;

    @Override
    public User create(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, long userId) {
        User updatingUser = storage.get(userId);
        if (user.getName() != null && !user.getName().isEmpty()) {
            updatingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            updatingUser.setEmail(user.getEmail());
        }
        storage.put(userId, updatingUser);
        return updatingUser;
    }

    @Override
    public User get(long id) {
        return storage.get(id);

    }

    @Override
    public boolean isDuplicateEmail(UserDto userDto) {
        List<User> duplicate = storage.values().stream().filter(u -> userDto.getEmail().equals(u.getEmail())).collect(Collectors.toList());
        return !duplicate.isEmpty();
    }

    @Override
    public void delete(long userId) {
        storage.remove(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean isExist(long userId) {
        return storage.containsKey(userId);
    }
}
