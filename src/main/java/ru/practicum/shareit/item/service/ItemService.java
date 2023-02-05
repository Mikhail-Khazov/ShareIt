package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> getUserStuff(long userId);

    ItemDto get(long userId, long itemId);

    List<ItemDto> search(long userId, String text);
}
