package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new BadRequestException("Поле с описанием не должно быть пустым.");
        }
        return itemRequestService.create(itemRequestDto, userId);
    }

    @PostMapping
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        return itemRequestService.getById(id, userId);
    }

    @PatchMapping("/{id}")
    public ItemRequestDto update(@RequestBody Long id, @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        isValidUser(userId);
        if (itemRequestDto.getDescription() == null) {
            if (itemRequestDto.getDescription().isBlank()) {
                throw new BadRequestException("Поле с описанием не должно быть пустым.");
            }
        }
        return itemRequestService.update(id, userId, itemRequestDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValidUser(userId);
        itemRequestService.delete(id, userId);
    }

    private boolean isValidUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        return true;
    }
}