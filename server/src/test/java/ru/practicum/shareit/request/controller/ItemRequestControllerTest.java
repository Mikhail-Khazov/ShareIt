package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoSave;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestServiceImpl itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    ItemRequestDto itemRequestDto;
    Long id;

    @BeforeEach
    void initialize() {
        id = 1L;
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("I need it")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .items(null)
                .build();
    }

    @Test
    void create_whenConditionsCorrect_thenReturnJsonRequestDto() throws Exception {
        when(itemRequestService.create(any(ItemRequestDtoSave.class), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(ItemRequestDtoSave.class)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1))
                .create(any(ItemRequestDtoSave.class), anyLong());
    }

    @Test
    void getAllForUser_whenUserHasRequest_thenReturnJsonListOfRequestDto() throws Exception {
        when(itemRequestService.getAllForUser(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(ItemRequestDtoSave.class)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1))
                .getAllForUser(anyLong());
    }

    @Test
    void getAll_whenRequestExist_thenReturnJsonListOfRequestDto() throws Exception {
        when(itemRequestService.getAll(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .param("from", "2")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(ItemRequestDtoSave.class)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1))
                .getAll(anyLong(), any(PageRequest.class));
    }

    @Test
    void get_whenRequestExist_thenReturnJsonRequestDto() throws Exception {
        when(itemRequestService.get(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", id)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(ItemRequestDtoSave.class)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1))
                .get(anyLong(), anyLong());
    }

    @Test
    void get_whenRequestNotFound_thenThrowItemRequestNotFoundException() throws Exception {
        when(itemRequestService.get(anyLong(), anyLong()))
                .thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", id)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(ItemRequestDtoSave.class)))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .get(anyLong(), anyLong());
    }
}