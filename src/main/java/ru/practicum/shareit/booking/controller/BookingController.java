package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSave;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.exceptions.WrongStateException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;
    private static final Sort DESC = Sort.by(Sort.Direction.DESC, "start");
    private static final Sort ASC = Sort.by(Sort.Direction.ASC, "start");

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
                                            @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                            @RequestHeader(X_SHARER_USER_ID) Long bookerId,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return service.getAllForBooker(getBookingState(state), bookerId, PageRequest.of(from / size, size, getSort(sort)));
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getAllForOwner(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                           @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return service.getAllForOwner(getBookingState(state), ownerId, PageRequest.of(from / size, size, getSort(sort)));
    }

    private Sort getSort(String sort) {
        return sort.equalsIgnoreCase("DESC") ? DESC : ASC;
    }

    private BookingState getBookingState(String state) {
        return BookingState.from(state).orElseThrow(() -> new WrongStateException("Unknown state: " + state));
    }

}

