package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDtoSave;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) long userId, @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return client.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return client.update(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserStuff(@RequestHeader(X_SHARER_USER_ID) long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return client.getUserStuff(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable long itemId, @RequestHeader(X_SHARER_USER_ID) long userId) {
        return client.get(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(X_SHARER_USER_ID) long userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        if (text.isBlank()) return ResponseEntity.of(Optional.of(Collections.emptyList()));
        return client.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Validated(Create.class) CommentDtoSave commentDto) {
        return client.createComment(userId, itemId, commentDto);
    }
}