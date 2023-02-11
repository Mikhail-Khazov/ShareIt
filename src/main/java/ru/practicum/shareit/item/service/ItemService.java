package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComplete;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDtoComplete> getUserStuff(long userId);

    ItemDtoComplete get(long itemId, long userId);

    Item getItem(long itemId);

    List<ItemDto> search(long userId, String text);

    CommentDto createComment(long userId, Long itemId, CommentDto commentDto);
}
