package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoSave;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDtoSave itemRequestDto, Long userId);

    List<ItemRequestDto> getAllForUser(Long userId);

    List<ItemRequestDto> getAll(Long userId, PageRequest pageRequest);

    ItemRequestDto get(Long requestId, Long userId);

    ItemRequest getModel(Long requestId);
}
