package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.IdGenerator;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final IdGenerator idGenerator;

    public UserDto create(UserDto userDto) {
        emailCheck(userDto, -1);
        userDto.setId(idGenerator.generate());
        repository.create(mapper.toUser(userDto));
        return userDto;
    }

    public UserDto update(UserDto userDto, long userId) {
        if (userDto.getEmail() != null) emailCheck(userDto, userId);
        return mapper.toUserDto(repository.update(mapper.toUser(userDto), userId));
    }

    private void emailCheck(UserDto userDto, long userId) {
        if (repository.isDuplicateEmail(userDto, userId))
            throw new DuplicateEmailException("Пользователь с таким Email уже существует");
    }

    public UserDto get(long userId) {
        return mapper.toUserDto(repository.get(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id " + userId + " не найден")
        ));
    }

    public void delete(long userId) {
        repository.delete(userId);
    }

    public List<UserDto> getAll() {
        return repository.getAll().stream().map(mapper::toUserDto).collect(Collectors.toList());
    }

    public boolean isExist(long userId) {
        return repository.isExist(userId);
    }

    public User getUserModel(long userId) {
        return repository.get(userId).orElseThrow(UserNotFoundException::new);
    }
}
