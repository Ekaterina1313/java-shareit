package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос от пользователя с id = {} на добавление вещи c id = {}.", userId, itemDto.getId());
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос на получение списка вещей пользователя с id = {}.", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос на получение вещи с с id = {}.", id);
        return itemService.getItemById(id, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на обновление информации о вещи с id = {}.", itemId);
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос на удаление вещи с id = {}.", id);
        itemService.deleteItem(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItem(@RequestParam("text") String searchText,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос на поиск списка вещей ключевым словам: {}.", searchText);
        return itemService.searchForItem(searchText, userId);
    }
}