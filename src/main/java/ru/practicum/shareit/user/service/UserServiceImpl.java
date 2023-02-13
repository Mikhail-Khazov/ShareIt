package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserDto create(UserDto userDto) {
        return mapper.toUserDto(repository.save(mapper.toUser(userDto)));
    }

    @Transactional
    public UserDto update(UserDto userDto, long userId) {
        User user = getUserModel(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return mapper.toUserDto(user);
    }

    public UserDto get(long userId) {
        return mapper.toUserDto(repository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id " + userId + " не найден")
        ));
    }

    @Transactional
    public void delete(long userId) {
        repository.deleteById(userId);
    }

    public List<UserDto> getAll() {
        return repository.findAll().stream().map(mapper::toUserDto).collect(Collectors.toList());
    }

    public boolean isExist(long userId) {
        return repository.existsById(userId);
    }

    public User getUserModel(long userId) {
        return repository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
