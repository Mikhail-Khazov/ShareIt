package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.exceptions.WrongStateException;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestBody @Validated({Create.class}) BookingDtoSave bookingDtoSave,
                             @RequestHeader(X_SHARER_USER_ID) Long bookerId) {
        return service.create(bookingDtoSave, bookerId);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto approve(@RequestParam(name = "approved") Boolean isApproved,
                              @PathVariable(name = "bookingId") Long id,
                              @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return service.approve(isApproved, id, ownerId);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto get(@PathVariable(name = "bookingId") Long id,
                          @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return service.get(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAllForBooker(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(X_SHARER_USER_ID) Long bookerId) {
        BookingState status = BookingState.from(state).orElseThrow(() -> new WrongStateException("Unknown state: " + state));
        return service.getAllForBooker(status, bookerId);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getAllForOwner(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        BookingState status = BookingState.from(state).orElseThrow(() -> new WrongStateException("Unknown state: " + state));
        return service.getAllForOwner(status, ownerId);
    }
}

