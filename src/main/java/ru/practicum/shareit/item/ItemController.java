package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(item);
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(id);
    }

    @PatchMapping("/itemId")
    public Item updateItem(@RequestBody long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.updateItem(itemId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long  id) {
        itemService.deleteItem(id);
    }
}
