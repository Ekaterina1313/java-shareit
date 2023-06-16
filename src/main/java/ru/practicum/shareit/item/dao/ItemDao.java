package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemDao {
    Item createItem(Item item);

    List<Item> getAllItems();

    Item getItemById(long id);

    Item updateItem(long itemId);

    void deleteItem(long  id);
}
