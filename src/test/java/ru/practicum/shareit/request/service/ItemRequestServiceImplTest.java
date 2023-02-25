package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoSave;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {
    ItemRequestService itemRequestService;
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    ItemRequestMapper itemRequestMapper;
    ItemMapper itemMapper;
    UserService userService;
    ItemRequest itemRequest;
    User requestor;
    Item item;

    @BeforeEach
    void initialize() {
        itemRequestMapper = new ItemRequestMapper();
        itemMapper = new ItemMapper();
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository,
                itemRequestMapper, itemMapper, userService);
        requestor = new User(1L, "John", "doe@ya.ru");
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Bla bla")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .owner(requestor)
                .request(itemRequest)
                .build();
    }

    private void compare(ItemRequestDto response) {
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getRequestor().getId(), response.getRequestorId());
        assertEquals(itemRequest.getCreated(), response.getCreated());
    }

    @Test
    void create_whenConditionsCorrect_thenReturnRequestDto() {
        when(userService.getUserModel(anyLong()))
                .thenReturn(requestor);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto response = itemRequestService.create(new ItemRequestDtoSave("description"), requestor.getId());

        compare(response);
    }

    @Test
    void getAllForUser_whenUserExistAndHasItem_thenReturnListOfItemRequestDto() {
        when(itemRequestRepository.getAllByUserId(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(userService.isExist(anyLong()))
                .thenReturn(true);

        List<ItemRequestDto> response = itemRequestService.getAllForUser(requestor.getId());

        compare(response.get(0));
    }

    @Test
    void getAll_whenUserHasItemAndRequest_thenReturnListOfRequestDto() {
        User user = new User(2L, "Kevin", "kev@ya.ru");
        PageImpl<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        when(itemRequestRepository.findByRequestor_IdNot(anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        when(itemRepository.findAllByRequestIds(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> response = itemRequestService.getAll(user.getId(), PageRequest.of(0, 10));

        compare(response.get(0));
    }

    @Test
    void get_whenUserAndItemExists_thenReturnRequestDto() {
        when(userService.isExist(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDto response = itemRequestService.get(1L, requestor.getId());

        compare(response);
    }

    @Test
    void get_whenRequestNotExists_thenReturnItemRequestNotFoundException() {
        when(userService.isExist(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.get(1L, requestor.getId()));
    }

    @Test
    void getModel_whenRequestExist_thenReturnRequestModel() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemRequest response = itemRequestService.getModel(itemRequest.getId());

        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getRequestor().getId(), response.getRequestor().getId());
        assertEquals(itemRequest.getCreated(), response.getCreated());
    }

    @Test
    void getModel_whenNotExist_thenReturnItemRequestNotFoundException() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getModel(itemRequest.getId()));
    }
}