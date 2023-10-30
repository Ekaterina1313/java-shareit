package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(@Qualifier("itemRequestServiceImplBd") ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoFull create(@RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new PersonalValidationException("Поле с описанием не должно быть пустым.");
        }
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoFull> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        return itemRequestService.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoFull> getAllByOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @RequestParam(name = "size", defaultValue = "10") int size) {
        isValidUser(userId);
        if (from < 0) {
            throw new PersonalValidationException("Значение параметра запроса не должно быть меньше 0");
        }
        if (size <= 0) {
            throw new PersonalValidationException("Значение параметра запроса не должно быть меньше либо равно 0");
        }
        return itemRequestService.getAllByOthers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoFull getById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        return itemRequestService.getById(requestId, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        itemRequestService.delete(id, userId);
    }

    private boolean isValidUser(Long userId) {
        if (userId == null) {
            throw new PersonalValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        return true;
    }
}