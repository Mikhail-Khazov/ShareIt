package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (!userService.isExist(userId))
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        return itemMapper.toItemDto(itemRepository.create(itemMapper.toItem(itemDto, userService.getUserModel(userId))));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (userId == itemRepository.get(itemId).orElseThrow(ItemNotFoundException::new).getOwner().getId()) {
            return itemMapper.toItemDto(
                    itemRepository.update(itemMapper.toItem(itemDto, userService.getUserModel(userId)), itemId, userId));
        } else throw new ForbiddenException("Редактировать вещи может только их владелец");
    }

    @Override
    public List<ItemDto> getUserStuff(long userId) {
        List<Item> stuff = itemRepository.getUserStuff(userId);
        if (stuff.isEmpty())
            throw new ItemNotFoundException("У пользователя с id " + userId + " нет предметов для аренды");
        return stuff.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        return itemMapper.toItemDto(itemRepository.get(itemId).orElseThrow(
                () -> new ItemNotFoundException("Предмет с id " + itemId + " не найден.")
        ));
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        List<Item> items = itemRepository.search(text);
        if (items.isEmpty()) throw new ItemNotFoundException("По запросу " + text + " ничего не найдено");
        else return items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
