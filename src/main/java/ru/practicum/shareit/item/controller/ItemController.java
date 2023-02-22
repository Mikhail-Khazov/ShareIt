package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoSave;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComplete;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) long userId, @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDtoComplete> getUserStuff(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return itemService.getUserStuff(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{itemId}")
    public ItemDtoComplete get(@PathVariable long itemId, @RequestHeader(X_SHARER_USER_ID) long userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(X_SHARER_USER_ID) long userId,
                                @RequestParam String text,
                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        if (text.isBlank()) return Collections.emptyList();
        return itemService.search(userId, text, PageRequest.of(from / size, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Validated(Create.class) CommentDtoSave commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}