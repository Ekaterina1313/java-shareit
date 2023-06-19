package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAll(Long userId);

    Optional<ItemRequestDto> getById(Long id);

    ItemRequestDto update(Long id, Long userId, ItemRequestDto itemRequestDto);

    void delete(Long id, Long userId);

    boolean isContainItemRequest(Long id);

    boolean isContainUser(Long id);
}