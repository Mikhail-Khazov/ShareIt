package ru.practicum.shareit.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    UserService userService;
    UserDto expectedUserDto;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void initialization() {
        expectedUserDto = new UserDto(1, "John", "doe@ya.ru");
    }

    @Test
    void create_whenUserCreated_thenReturnedUserDto() throws Exception {
        when(userService.create(any(UserDto.class)))
                .thenReturn(expectedUserDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedUserDto)));

        verify(userService, times(1))
                .create(any(UserDto.class));
    }

    @Test
    void update_whenUserUpdated_thenReturnedUserDto() throws Exception {
        when(userService.update(any(UserDto.class), anyLong()))
                .thenReturn(expectedUserDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedUserDto)));

        verify(userService, times(1))
                .update(any(UserDto.class), anyLong());
    }

    @Test
    void getAll_whenListNotEmpty_thenReturnListWithUsersDto() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(expectedUserDto));

        mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(expectedUserDto))));

        verify(userService, times(1))
                .getAll();
    }

    @Test
    void get_whenUserExists_thenReturnUserDto() throws Exception {
        when(userService.get(anyLong()))
                .thenReturn(expectedUserDto);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedUserDto)));

        verify(userService, times(1))
                .get(anyLong());
    }

    @Test
    void delete_whenDeleteComplete_thenReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
}