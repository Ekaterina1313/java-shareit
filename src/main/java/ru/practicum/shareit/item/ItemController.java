package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(id, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long  id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItem(@RequestParam ("text") String searchText,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchForItem(searchText, userId);
    }
}
