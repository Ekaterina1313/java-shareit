package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemDaoImpl implements ItemDao {
    private Map<Long, Item> mapOfItems = new HashMap<>();
    private static Long itemId = 1L;

    @Override
    public Item createItem(Item item) {
        item.setId(itemId);
        itemId++;
        mapOfItems.put(item.getId(), item);
        return item;
    }

    @Override
    public Map<Long, Item> getAllItems() {
        return mapOfItems;
    }

    @Override
    public Item getItemById(long id) {
        return mapOfItems.get(id);
    }

    @Override
    public Item updateItem(Item item) {
        Item item1 = mapOfItems.get(item.getId());
        if (item.getName() != null) {
            item1.setName(item.getName());
        }
        if (item.getDescription() != null) {
            item1.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            item1.setAvailable(item.getAvailable());
        }
        mapOfItems.put(item1.getId(), item1);
        return item1;
    }

    @Override
    public void deleteItem(long id) {
        mapOfItems.remove(id);
    }

    @Override
    public boolean isContainItem(long id) {
        return mapOfItems.containsKey(id);
    }
}