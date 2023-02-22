package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.StateNotFoundException;
import ru.practicum.shareit.exceptions.WrongStateException;
import ru.practicum.shareit.item.dto.ItemDtoLite;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingServiceImpl bookingService;

    @Autowired
    BookingController bookingController;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    BookingDto bookingDto;
    BookingDtoSave bookingDtoSave;
    Long id;

    @BeforeEach
    void initialize() {
        id = 1L;
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(new ItemDtoLite(1L, "board game", "something strange"))
                .booker(new UserDto(1L, "Paul", "p12@gmail.com"))
                .status("APPROVED")
                .build();
        bookingDtoSave = new BookingDtoSave(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusDays(1), 1L);
        bookingController = new BookingController(bookingService);
    }

    @Test
    void create_whenBookingCreatedSuccessfully_thenReturnBookingDto() throws Exception {
        when(bookingService.create(any(BookingDtoSave.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingDto)));

        verify(bookingService, times(1))
                .create(any(BookingDtoSave.class), anyLong());
    }

    @Test
    void create_whenItemNotAvailable_thenThrowItemNotAvailableException() throws Exception {
        when(bookingService.create(any(BookingDtoSave.class), anyLong()))
                .thenThrow(ItemNotAvailableException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1))
                .create(any(BookingDtoSave.class), anyLong());
    }

//    @Test
//    void create_whenDtoNotValid_thenThrowMethodArgumentNotValidException() throws Exception {
//        bookingDtoSave.setEnd(LocalDateTime.now().minusDays(5));
//        when(bookingService.create(any(BookingDtoSave.class), anyLong()))
//                .thenThrow(MethodArgumentNotValidException.class);
//
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", id)
//                        .contentType("application/json")
//                        .content(mapper.writeValueAsString(bookingDtoSave)))
//                .andExpect(status().isBadRequest());
//
//        verify(bookingService, times(1))
//                .create(any(BookingDtoSave.class), anyLong());
////        assertThrows(MethodArgumentNotValidException.class, () -> bookingController.create(bookingDtoSave, anyLong()));
//    }

    @Test
    void approve_whenApproveSuccessfully_thenReturnBookingDto() throws Exception {
        when(bookingService.approve(anyBoolean(), anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", id)
                        .header("X-Sharer-User-Id", id)
                        .param("approved", "true")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingDto)));

        verify(bookingService, times(1))
                .approve(anyBoolean(), anyLong(), anyLong());
    }

    @Test
    void approve_whenBookingNotFound_thenThrowBookingNotFoundException() throws Exception {
        when(bookingService.approve(anyBoolean(), anyLong(), anyLong()))
                .thenThrow(BookingNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", id)
                        .header("X-Sharer-User-Id", id)
                        .param("approved", "true")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1))
                .approve(anyBoolean(), anyLong(), anyLong());
    }

    @Test
    void approve_whenWrongState_thenThrowWrongStateException() throws Exception {
        when(bookingService.approve(anyBoolean(), anyLong(), anyLong()))
                .thenThrow(WrongStateException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", id)
                        .header("X-Sharer-User-Id", id)
                        .param("approved", "true")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1))
                .approve(anyBoolean(), anyLong(), anyLong());
    }

    @Test
    void get_whenBookingExist_thenReturnBookingDto() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", id)
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingDto)));

        verify(bookingService, times(1))
                .get(anyLong(), anyLong());
    }

    @Test
    void getAllForBooker_whenBookerHaveBooking_thenReturnListBookingDto() throws Exception {
        when(bookingService.getAllForBooker(any(BookingState.class), anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1))
                .getAllForBooker(any(BookingState.class), anyLong(), any(PageRequest.class));
    }

    @Test
    void getAllForOwner_whenOwnerHaveBooking_thenReturnBookingDto() throws Exception {
        when(bookingService.getAllForOwner(any(BookingState.class), anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1))
                .getAllForOwner(any(BookingState.class), anyLong(), any(PageRequest.class));
    }

    @Test
    void getAllForOwner_whenStateNotFound_thenThrowStateNotFoundException() throws Exception {
        when(bookingService.getAllForOwner(any(BookingState.class), anyLong(), any(PageRequest.class)))
                .thenThrow(StateNotFoundException.class);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1))
                .getAllForOwner(any(BookingState.class), anyLong(), any(PageRequest.class));
    }

    @Test
    void getAllForOwner_whenInternalServerError_thenThrowInternalServerError() throws Exception {
        when(bookingService.getAllForOwner(any(BookingState.class), anyLong(), any(PageRequest.class)))
                .thenThrow(HttpServerErrorException.InternalServerError.class);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDtoSave)))
                .andExpect(status().isInternalServerError());

        verify(bookingService, times(1))
                .getAllForOwner(any(BookingState.class), anyLong(), any(PageRequest.class));
    }
}