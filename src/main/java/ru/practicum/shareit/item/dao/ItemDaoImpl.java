package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private Map<Long, Item> mapOfItems = new HashMap<>();
    private static Long itemId = 1L;

    @Override
    public Item create(Item item) {
        item.setId(itemId);
        itemId++;
        mapOfItems.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        return mapOfItems.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(mapOfItems.get(id));
    }

    @Override
    public Item update(Item item) {
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
    public List<Item> search(String searchText, Long userId) {
        String toLowerCaseText = searchText.toLowerCase();
        List<Item> items = new ArrayList<>();
        mapOfItems.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(toLowerCaseText) ||
                                item.getDescription().toLowerCase().contains(toLowerCaseText)))
                .forEach(items::add);
        return items;
    }

    @Override
    public void delete(Long id) {
        mapOfItems.remove(id);
    }

    @Override
    public boolean isContainItem(Long id) {
        return mapOfItems.containsKey(id);
    }
}