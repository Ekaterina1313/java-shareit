package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    List<ItemDtoToGet> getAll(Long userId, int from, int size);

    ItemDtoToGet getById(Long id, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    void delete(Long id, Long userId);

    List<ItemDto> search(String searchText, int from, int size, Long userId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId);

    Item validItem(Long itemId);
}