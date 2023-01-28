package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items;
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    @Override
    public Item create(Item item) {
        items.put(item.getId(), item);
        final List<Item> items = userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        Item updatingItem = items.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            updatingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatingItem.setAvailable(item.getAvailable());
        }
        return updatingItem;
    }

    @Override
    public List<Item> getUserStuff(long userId) {
        return userItemIndex.get(userId);
    }

    @Override
    public Optional<Item> get(long itemId) {
        return Optional.of(items.get(itemId));
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream().filter(t -> t.getName().toLowerCase().contains(text.toLowerCase()) ||
                t.getDescription().toLowerCase().contains(text.toLowerCase()) && t.getAvailable()).collect(Collectors.toList());
    }
}
