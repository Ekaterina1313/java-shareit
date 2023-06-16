package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public Item createItem(Item item) {
        return itemDao.createItem(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemDao.getAllItems();
    }

    @Override
    public Item getItemById(long id) {
        return itemDao.getItemById(id);
    }

    @Override
    public Item updateItem(long itemId) {
        return itemDao.updateItem(itemId);
    }

    @Override
    public void deleteItem(long  id) {
        itemDao.deleteItem(id);
    }
}
