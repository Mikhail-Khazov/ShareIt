package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDtoSave itemRequestDto, Long userId) {
        User requestor = userService.getUserModel(userId);
        ItemRequest request = repository.save(mapper.dtoToRequest(itemRequestDto, requestor));
        return mapper.requestToDto(request, List.of());
    }

    @Override
    public List<ItemRequestDto> getAllForUser(Long userId) {
        if (!userService.isExist(userId)) throw new UserNotFoundException();
        List<ItemRequest> requests = repository.getAllByUserId(userId);
        List<Item> items = itemRepository.findAllByRequestIds(requests.stream().map(ItemRequest::getId).collect(Collectors.toList()));
        return requests.stream()
                .map(request -> mapper.requestToDto(request, items.stream().map(itemMapper::toItemDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, PageRequest pageRequest) {
        List<ItemRequest> requests = repository.findByRequestor_IdNot(userId, pageRequest).toList();
        if (requests.isEmpty()) return List.of();
        Map<Long, List<ItemDto>> items = itemRepository.findAllByRequestIds(requests.stream().map(ItemRequest::getId).collect(Collectors.toList()))
                .stream().map(itemMapper::toItemDto).collect(groupingBy(ItemDto::getRequestId));
        return requests.stream()
                .map(request -> mapper.requestToDto(request, items.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto get(Long requestId, Long userId) {
        if (!userService.isExist(userId)) throw new UserNotFoundException();
        List<Item> items = itemRepository.findByRequestId(requestId);
        return mapper.requestToDto(repository.findById(requestId).orElseThrow(ItemRequestNotFoundException::new),
                items.stream().map(itemMapper::toItemDto).collect(Collectors.toList()));
    }

    @Override
    public ItemRequest getModel(Long requestId) {
        return repository.findById(requestId).orElseThrow(ItemRequestNotFoundException::new);
    }
}
