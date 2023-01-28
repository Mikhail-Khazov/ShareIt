package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> storage;

    @Override
    public User create(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, long userId) {
        User updatingUser = storage.get(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            updatingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updatingUser.setEmail(user.getEmail());
        }
        return updatingUser;
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.of(storage.get(id));
    }

    @Override
    public boolean isDuplicateEmail(UserDto userDto, long userId) {
        List<User> duplicate = storage.values().stream()
                .filter(u -> userDto.getEmail().equals(u.getEmail()) && userId != u.getId())
                .collect(Collectors.toList());
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
