package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    List<Item> getAllItems();

    Item getItemById(long id);

    Item updateItem(long itemId);

    void deleteItem(long  id);
}
