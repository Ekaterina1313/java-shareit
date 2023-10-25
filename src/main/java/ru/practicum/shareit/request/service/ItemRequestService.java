package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoFull create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoFull> getAllByOwner(Long userId);

    List<ItemRequestDtoFull> getAllByOthers(Long userId, int from, int size);

    ItemRequestDtoFull getById(Long id, Long userId);

    //ItemRequestDto update(Long id, Long userId, ItemRequestDto itemRequestDto);

    void delete(Long id, Long userId);

    ItemRequest validItemRequest(Long requestId);
}