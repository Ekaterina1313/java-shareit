package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
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
        return itemClient.create(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        isValid(userId);
        isValidPagination(from, size);
        log.info("Поступил запрос на получение списка вещей пользователя с id = {}.", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        log.info("Поступил запрос на получение вещи с с id = {}.", id);
        return itemClient.getById(id, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
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
        return itemClient.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        log.info("Поступил запрос на удаление вещи с id = {}.", id);
        itemClient.delete(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String searchText,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        isValidPagination(from, size);
        if (searchText == null || searchText.isBlank()) {
            return new ResponseEntity(new ArrayList<>(), HttpStatus.OK);
        }
        log.info("Поступил запрос на поиск списка вещей ключевым словам: {}.", searchText);
        return itemClient.search(searchText, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        if ((commentDto.getText().isBlank()) || (commentDto.getText().isEmpty())) {
            throw new PersonalValidationException("Заполните поле 'text'.");
        }
        return itemClient.createComment(commentDto, itemId, userId);
    }

    private boolean isValid(Long userId) {
        if (userId == null) {
            throw new PersonalValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        return true;
    }

    private boolean isValidPagination(int from, int size) {
        if (from < 0) {
            throw new PersonalValidationException("Параметр 'from' не должен принимать отрицательное значение.");
        }
        if (size <= 0) {
            throw new PersonalValidationException("Параметр 'size' не должен принимать пустое или отрицательное значение.");
        }
        return true;
    }
}