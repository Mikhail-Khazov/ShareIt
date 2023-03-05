package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoSave;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(Create.class) ItemRequestDtoSave itemRequestDto, @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return client.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return client.getAllForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@PathVariable Long requestId, @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return client.get(requestId, userId);
    }
}
