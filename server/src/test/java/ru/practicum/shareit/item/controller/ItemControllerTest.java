package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.CommentValidationException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoSave;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComplete;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    ItemDto itemDto;
    CommentDto commentDto;
    Long id;
    ItemDtoComplete itemDtoComplete;

    @BeforeEach
    void initialize() {
        commentDto = CommentDto.builder()
                .id(1L)
                .text("I need it!")
                .itemId(1L)
                .authorName("Boris")
                .created(LocalDateTime.now())
                .build();
        id = 1L;
        itemDto = ItemDto.builder()
                .id(1L)
                .name("fountain pen")
                .description("unique design and perfect ergonomics")
                .available(true)
                .requestId(null)
                .build();
        itemDtoComplete = ItemDtoComplete.builder()
                .id(1L)
                .name("jacket")
                .description("leather jacket")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of(commentDto))
                .build();
    }

    @Test
    void create_whenItemCreated_thenReturnItem() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemDto)));

        verify(itemService, times(1))
                .create(anyLong(), any(ItemDto.class));
    }

    @Test
    void create_whenUserNotExist_thenReturnUserNotFoundException() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1))
                .create(anyLong(), any(ItemDto.class));
    }

    @Test
    void update_whenItemUpdated_thenReturnItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemDto)));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void update_whenItemNotBelongUser_thenReturnForbiddenException() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(ForbiddenException.class);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isForbidden());

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void update_whenItemNotExist_thenReturnItemNotFoundException() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void getUserStuff_whenUserExistAndHaveItems_thenReturnListItems() throws Exception {
        when(itemService.getUserStuff(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemDtoComplete));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", id)
                        .param("from", "2")
                        .param("size", "2")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDtoComplete)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(itemDtoComplete))));

        verify(itemService, times(1))
                .getUserStuff(anyLong(), any(PageRequest.class));
    }

    @Test
    void get_whenUserExistAndItemExist_thenReturnItem() throws Exception {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemDtoComplete);

        mockMvc.perform(get("/items/{itemId}", id)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDtoComplete)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemDtoComplete)));

        verify(itemService, times(1))
                .get(anyLong(), anyLong());
    }

    @Test
    void search_whenItemIsExist_thenReturnListOfItems() throws Exception {
        when(itemService.search(anyLong(), anyString(), any(PageRequest.class)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", id)
                        .param("text", "search test")
                        .param("from", "2")
                        .param("size", "2")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(itemDto))));

        verify(itemService, times(1))
                .search(anyLong(), anyString(), any(PageRequest.class));
    }

    @Test
    void createComment_whenCommentCreated_thenReturnCommentDto() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDtoSave.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", id)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(CommentDtoSave.class)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commentDto)));

        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDtoSave.class));
    }

    @Test
    void createComment_whenCommentNotValid_thenReturnCommentValidationException() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDtoSave.class)))
                .thenThrow(CommentValidationException.class);

        mockMvc.perform(post("/items/{itemId}/comment", id)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(CommentDtoSave.class)))
                .andExpect(status().isBadRequest());

        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDtoSave.class));
    }

}