package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoSave;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComplete;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDtoComplete> getUserStuff(long userId, PageRequest pageRequest);

    ItemDtoComplete get(long itemId, long userId);

    Item getItem(long itemId);

    List<ItemDto> search(long userId, String text, PageRequest pageRequest);

    CommentDto createComment(long userId, Long itemId, CommentDtoSave commentDto);
}
