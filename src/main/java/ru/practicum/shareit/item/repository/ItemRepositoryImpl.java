package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private HashMap<Long, Item> repository;

    @Override
    public Item create(Item item) {
        repository.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId) {
        Item updatingItem = repository.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            updatingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatingItem.setAvailable(item.getAvailable());
        }
        repository.put(itemId, updatingItem);
        return updatingItem;
    }

    @Override
    public List<Item> getUserStuff(long userId) {
        return repository.values().stream().filter(t -> t.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> get(long itemId) {
        return Optional.of(repository.get(itemId));
    }

    @Override
    public List<Item> search(String text) {
        return repository.values().stream().filter(t -> t.getName().toLowerCase().contains(text.toLowerCase()) ||
                t.getDescription().toLowerCase().contains(text.toLowerCase()) && t.getAvailable()).collect(Collectors.toList());
    }
}
