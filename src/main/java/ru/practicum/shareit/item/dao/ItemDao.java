package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface ItemDao {
    Item createItem(Item item);

    Map<Long, Item> getAllItems();

    Item getItemById(long id);

    Item updateItem(Item item);

    void deleteItem(long id);

    boolean isContainItem(long id);
}