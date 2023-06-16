package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ItemDaoImpl implements ItemDao {
    private Map<Long, Item> mapOfItems = new HashMap<>();
    @Override
    public Item createItem(Item item) {
        mapOfItems.put(item.getId(), item);
        return item;
    }

    @Override
   public List<Item> getAllItems() {

        return new ArrayList<>(mapOfItems.values());
    }

    @Override
    public Item getItemById(long id) {
        return mapOfItems.get(id);
    }

    @Override
    public Item updateItem(long itemId) {
        return mapOfItems.get(itemId);
    }

    @Override
    public void deleteItem(long  id) {

    }
}
