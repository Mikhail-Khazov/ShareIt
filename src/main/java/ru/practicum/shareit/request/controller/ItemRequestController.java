package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoSave;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestBody @Validated(Create.class) ItemRequestDtoSave itemRequestDto, @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return service.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllForUser(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return service.getAllForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {
        return service.getAll(userId, PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created")));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@PathVariable Long requestId, @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return service.get(requestId, userId);
    }
}
