package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    List<ItemDto> getAll(Long userId);

    ItemDto getById(Long id, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    void delete(Long id, Long userId);

    List<ItemDto> search(String searchText, Long userId);
}