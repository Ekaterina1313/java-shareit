package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    List<ItemDtoToGet> getAll(Long userId);

    ItemDtoToGet getById(Long id, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    void delete(Long id, Long userId);

    List<ItemDto> search(String searchText, Long userId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId);

}