package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new BadRequestException("Имя вещи не должно быть пустым.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new BadRequestException("Описание вещи не должно быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Поле 'Available' не должно быть пустым. ");
        }
        log.info("Поступил запрос от пользователя с id = {} на добавление вещи c id = {}.", userId, itemDto.getId());
        return itemService.create(itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        log.info("Поступил запрос на получение списка вещей пользователя с id = {}.", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public Optional<ItemDto> getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!itemService.isContainItem(id)) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        log.info("Поступил запрос на получение вещи с с id = {}.", id);
        return itemService.getById(id, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!itemService.isContainItem(itemId)) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new BadRequestException("Имя вещи не должно быть пустым.");
            }
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new BadRequestException("Описание вещи не должно быть пустым.");
            }
        }
        log.info("Поступил запрос на обновление информации о вещи с id = {}.", itemId);
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!itemService.isContainItem(id)) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        log.info("Поступил запрос на удаление вещи с id = {}.", id);
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchText,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (searchText == null || searchText.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Поступил запрос на поиск списка вещей ключевым словам: {}.", searchText);
        return itemService.search(searchText, userId);
    }
}