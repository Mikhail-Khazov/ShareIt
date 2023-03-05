package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    Item item;
    ItemRequest itemRequest;
    User requestor;
    User owner;

    @BeforeEach
    void initialize() {
        requestor = userRepository.save(new User(1L, "John", "doe@ya.ru"));
        owner = userRepository.save(new User(2L, "Kevin", "kev@ya.ru"));
        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .id(1L)
                .description("Bla bla")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());
        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());
    }

    @Test
    void getUserStuff_whenUserHaveItem_thenReturnPageOfItem() {
        List<Item> response = itemRepository.getUserStuff(owner.getId(), PageRequest.of(0, 10)).toList();

        compare(response);
    }

    @Test
    void search_whenItemExistAndDescriptionContainsText_thenReturnPageOfItem() {
        List<Item> response = itemRepository.search("Blue", PageRequest.of(0, 10)).toList();

        compare(response);
    }

    @Test
    void findAllByRequestIds_whenListOfRequestsContainsOneElement_thenReturnListOfItem() {
        final List<Item> response = itemRepository.findAllByRequestIds(List.of(itemRequest.getId()));

        compare(response);
    }

    @Test
    void findByRequestId_whenRequestExist_thenReturnListOfItem() {
        final List<Item> response = itemRepository.findByRequestId(itemRequest.getId());

        compare(response);
    }

    private void compare(List<Item> response) {
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(item.getId(), response.get(0).getId());
        assertEquals(item.getName(), response.get(0).getName());
        assertEquals(item.getDescription(), response.get(0).getDescription());
    }
}