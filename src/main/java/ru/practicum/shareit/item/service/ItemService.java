package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    List<ItemDto> getAllItems(Long userId);

    ItemDto getItemById(Long id, Long userId);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    void deleteItem(Long  id, Long userId);

    List<ItemDto> searchForItem(String searchText, Long userId);
}
