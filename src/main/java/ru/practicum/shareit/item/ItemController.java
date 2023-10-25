package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("itemServiceImplBd") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        if ((itemDto.getName() == null) || (itemDto.getName().isBlank())) {
            throw new PersonalValidationException("Имя вещи не должно быть пустым.");
        }
        if ((itemDto.getDescription() == null) || (itemDto.getDescription().isBlank())) {
            throw new PersonalValidationException("Описание вещи не должно быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new PersonalValidationException("Поле 'Available' не должно быть пустым. ");
        }
        log.info("Поступил запрос от пользователя с id = {} на добавление вещи c id = {}.", userId, itemDto.getId());
        return itemService.create(itemDto, userId);
    }

    @GetMapping
    public List<ItemDtoToGet> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        isValid(userId);
        isValidPagination(from, size);
        log.info("Поступил запрос на получение списка вещей пользователя с id = {}.", userId);
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDtoToGet getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        log.info("Поступил запрос на получение вещи с с id = {}.", id);
        return itemService.getById(id, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        isValid(userId);
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new PersonalValidationException("Имя вещи не должно быть пустым.");
            }
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new PersonalValidationException("Описание вещи не должно быть пустым.");
            }
        }
        log.info("Поступил запрос на обновление информации о вещи с id = {}.", itemId);
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        log.info("Поступил запрос на удаление вещи с id = {}.", id);
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchText,
                                @RequestParam(name = "from", defaultValue = "0") int from,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        isValidPagination(from, size);
        if (searchText == null || searchText.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Поступил запрос на поиск списка вещей ключевым словам: {}.", searchText);
        return itemService.search(searchText, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        if ((commentDto.getText().isBlank()) || (commentDto.getText().isEmpty())) {
            throw new PersonalValidationException("Заполните поле 'text'.");
        }
        return itemService.createComment(commentDto, itemId, userId);
    }

    private boolean isValid(Long userId) {
        if (userId == null) {
            throw new PersonalValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        return true;
    }
    private boolean isValidPagination(int from, int size) {
        if (from < 0 ) {
            throw new PersonalValidationException("Параметр 'from' не должен принимать отрицательное значение.");
        }
        if (size <= 0) {
            throw new PersonalValidationException("Параметр 'size' не должен принимать пустое или отрицательное значение.");
        }
        return true;
    }
}