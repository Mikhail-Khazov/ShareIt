package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    UserRepository userRepository;
    UserService userService;
    UserMapper userMapper;
    UserDto userDto;
    User user;

    @BeforeEach
    void initialization() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
        userDto = new UserDto(1, "John", "doe@ya.ru");
        user = userMapper.toUser(userDto);
    }

    @Test
    void create_whenUserSaved_thenReturnedUserDto() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto response = userService.create(userDto);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void update_whenUserNameNullAndNewEmail_thenUpdateEmail() {
        userDto.setName(null);
        userDto.setEmail("m@mail.ru");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto response = userService.update(userDto, 1L);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void update_whenUserNameNewAndEmailNull_thenUpdateName() {
        userDto.setName("Greg");
        userDto.setEmail(null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto response = userService.update(userDto, 1L);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void get_whenUserFound_thenReturnUserDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto response = userService.get(1L);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void get_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.get(2L));
    }

    @Test
    void delete() {
        userService.delete(user.getId());

        verify(userRepository, times(1))
                .deleteById(user.getId());
    }

    @Test
    void getAll_whenUsersFound_thenReturnListOfUsers() {
        User user1 = new User(2L, "Greg", "greg@mail.be");
        when(userRepository.findAll())
                .thenReturn(List.of(user, user1));

        List<UserDto> response = userService.getAll();

        assertEquals(2, response.size());
        assertEquals(user.getName(), response.get(0).getName());
        assertEquals(user.getEmail(), response.get(0).getEmail());
        assertEquals(user1.getName(), response.get(1).getName());
        assertEquals(user1.getEmail(), response.get(1).getEmail());
    }

    @Test
    void isExist_whenUserExist_thenReturnTrue() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        boolean bol = userService.isExist(user.getId());

        assertTrue(bol);
    }

    @Test
    void isExist_whenUserNotFound_thenReturnFalse() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        boolean bol = userService.isExist(-1);

        assertFalse(bol);
    }

    @Test
    void getUserModel_whenUserExist_thenReturnUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User user1 = userService.getUserModel(user.getId());

        assertEquals(user.getName(), user1.getName());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void getUserModel_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserModel(-1));
    }
}