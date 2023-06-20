package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAll(Long userId);

    ItemRequestDto getById(Long id, Long userId);

    ItemRequestDto update(Long id, Long userId, ItemRequestDto itemRequestDto);

    void delete(Long id, Long userId);
}