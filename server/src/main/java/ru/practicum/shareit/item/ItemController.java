package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("itemServiceImplDb") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос от пользователя с id = {} на добавление вещи c id = {}.", userId, itemDto.getId());
        return itemService.create(itemDto, userId);
    }

    @GetMapping
    public List<ItemDtoToGet> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                     @RequestParam(name = "size", defaultValue = "10") int size) {

        log.info("Поступил запрос на получение списка вещей пользователя с id = {}.", userId);
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDtoToGet getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Поступил запрос на получение вещи с с id = {}.", id);
        return itemService.getById(id, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {

        log.info("Поступил запрос на обновление информации о вещи с id = {}.", itemId);
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Поступил запрос на удаление вещи с id = {}.", id);
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchText,
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Поступил запрос на поиск списка вещей ключевым словам: {}.", searchText);
        return itemService.search(searchText, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemService.createComment(commentDto, itemId, userId);
    }
}