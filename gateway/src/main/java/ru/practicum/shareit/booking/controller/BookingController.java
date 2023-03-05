package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.dto.enums.BookingState;
import ru.practicum.shareit.booking.dto.enums.Sort;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.exceptions.WrongStateException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated({Create.class}) BookingDtoSave bookingDtoSave,
                                         @RequestHeader(X_SHARER_USER_ID) Long bookerId) {
        return client.create(bookerId, bookingDtoSave);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> approve(@RequestParam(name = "approved") Boolean isApproved,
                                          @PathVariable(name = "bookingId") Long id,
                                          @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return client.approve(isApproved, id, ownerId);
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> get(@PathVariable(name = "bookingId") Long id,
                                      @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return client.get(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForBooker(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                                  @RequestHeader(X_SHARER_USER_ID) Long bookerId,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return client.getAllForBooker(bookerId, getBookingState(state), from, size, getSort(sort));
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getAllForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                                 @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return client.getAllForOwner(ownerId, getBookingState(state), from, size, getSort(sort));
    }

    private String getSort(String sort) {
        return Sort.from(sort);
    }

    private BookingState getBookingState(String state) {
        return BookingState.from(state).orElseThrow(() -> new WrongStateException("Unknown state: " + state));
    }

}

