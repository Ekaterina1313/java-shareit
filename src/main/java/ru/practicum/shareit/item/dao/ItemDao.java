package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item create(Item item);

    List<Item> getAll(Long userId);

    Optional<Item> getById(Long id);

    Item update(Item item);

    void delete(Long id);

    List<Item> search(String searchText, Long userId);
}