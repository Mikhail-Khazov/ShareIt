package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item item, long itemId, long userId);

    List<Item> getUserStuff(long userId);

    Optional<Item> get(long itemId);

    List<Item> search(String text);
}
