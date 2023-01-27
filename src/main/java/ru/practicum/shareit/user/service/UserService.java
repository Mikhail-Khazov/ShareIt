package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.IdGenerator;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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
        if (repository.isDuplicateEmail(userDto))
            throw new DuplicateEmailException("Пользователь с таким Email уже существует");
        userDto.setId(idGenerator.generate());
        repository.create(mapper.toUser(userDto));
        return userDto;
    }

    public UserDto update(UserDto userDto, long userId) {
        if (userDto.getEmail() != null) {
            if (repository.isDuplicateEmail(userDto))
                throw new DuplicateEmailException("Пользователь с таким Email уже существует");
        }
        return mapper.toUserDto(repository.update(mapper.toUser(userDto), userId));
    }

    public UserDto get(long id) {
        return mapper.toUserDto(repository.get(id));
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
}
