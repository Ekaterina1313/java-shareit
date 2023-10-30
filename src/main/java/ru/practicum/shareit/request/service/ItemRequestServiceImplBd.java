package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImplBd implements ItemRequestService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImplBd(@Qualifier("userServiceImplBd") UserService userService,
                                    ItemRepository itemRepository, ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDtoFull create(ItemRequestDto itemRequestDto, Long userId) {
        User userById = userService.validUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userById);
        return ItemRequestMapper.toItemRequestDtoFull(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDtoFull> getAllByOwner(Long userId) {
        userService.validUser(userId);
        List<ItemRequest> listOfRequests = itemRequestRepository.findByRequestorId(userId);
        List<Long> requestIds = listOfRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequestIds(requestIds);
        List<ItemDto> responses = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        List<ItemRequestDtoFull> result = new ArrayList<>();
        for (ItemRequest element : listOfRequests) {
            List<ItemDto> requestResponses = responses.stream()
                    .filter(response -> response.getRequestId().equals(element.getId()))
                    .collect(Collectors.toList());
            ItemRequestDtoFull requestDto = ItemRequestMapper.toItemRequestDtoFull(element, requestResponses);
            result.add(requestDto);
        }
        result.sort(Comparator.comparing(ItemRequestDtoFull::getCreated).reversed());
        return result;
    }


    @Override
    public List<ItemRequestDtoFull> getAllByOthers(Long userId, int from, int size) {
        userService.validUser(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = itemRequestRepository.findAll(pageable);

        List<ItemRequest> itemRequests = page.getContent().stream()
                .filter(itemRequest -> !userId.equals(itemRequest.getRequestor().getId()))
                .collect(Collectors.toList());
        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequestIds(requestIds);
        List<ItemDto> responses = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        List<ItemRequestDtoFull> result = new ArrayList<>();
        for (ItemRequest element : itemRequests) {
            List<ItemDto> requestResponses = responses.stream()
                    .filter(response -> response.getRequestId().equals(element.getId()))
                    .collect(Collectors.toList());
            ItemRequestDtoFull requestDto = ItemRequestMapper.toItemRequestDtoFull(element, requestResponses);
            result.add(requestDto);
        }
        return result;
    }

    @Override
    public ItemRequestDtoFull getById(Long id, Long userId) {
        userService.validUser(userId);
        ItemRequest itemRequest = validItemRequest(id);
        List<Item> responses = itemRepository.findByRequestId(id);
        List<ItemDto> result = responses.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDtoFull(itemRequest, result);
    }

    @Override
    public void delete(Long id, Long userId) {
        userService.validUser(userId);
        ItemRequest itemRequest2 = validItemRequest(id);
        if (!Objects.equals(itemRequest2.getRequestor().getId(), userId)) {
            throw new PersonalValidationException("Пользователь с id = " + userId + " не может удалить чужой запрос.");
        }
        itemRequestRepository.deleteById(id);
    }

    @Override
    public ItemRequest validItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден запрос с id: " + requestId));
    }
}