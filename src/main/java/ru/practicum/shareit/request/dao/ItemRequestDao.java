package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestDao {
    ItemRequest create(ItemRequest itemRequest);

    List<ItemRequest> getAll(Long userId);

    Optional<ItemRequest> getById(Long id);

    ItemRequest update(ItemRequest itemRequest);

    void delete(Long id);
}