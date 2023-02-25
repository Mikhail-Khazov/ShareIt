package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    ItemRequest itemRequest;
    User requestor;

    @BeforeEach
    void initialize() {
        requestor = userRepository.save(new User(1L, "John", "doe@ya.ru"));
        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .id(1L)
                .description("Bla bla")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void getAllByUserId() {
        List<ItemRequest> response = itemRequestRepository.getAllByUserId(requestor.getId());

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(itemRequest.getId(), response.get(0).getId());
        assertEquals(itemRequest.getDescription(), response.get(0).getDescription());
        assertEquals(itemRequest.getRequestor().getEmail(), response.get(0).getRequestor().getEmail());
    }
}