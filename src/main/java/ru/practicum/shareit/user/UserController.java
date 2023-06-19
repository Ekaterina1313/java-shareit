package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {

        log.info("Поступил запрос на получение списка пользователей.");
        return userService.getAll();
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throw new BadRequestException("Имя не должно быть пустым.");
        } else if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new BadRequestException("Адрес электронной почты не должен быть пустым.");
        } else if (!userDto.getEmail().contains("@")) {
            throw new BadRequestException("Некорректный формат e-mail. Адрес электронной почты должен содержать символ '@'.");
        } else if (userService.isContainEmail(userDto.getEmail())) {
            throw new RuntimeException("Адрес электронной почты уже используется другим пользователем.");
        }
        log.info("Поступил запрос на создание пользователя {} c id = {}.", userDto.getName(), userDto.getId());
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getById(@PathVariable long id) {
        if (!userService.isContainUser(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден.");
        }
        log.info("Поступил запрос на получение пользователя с id = {}.", id);
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        if (!userService.isContainUser(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = \" + id + \" не найден.");
        }
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().contains("@")) {
                throw new BadRequestException("Некорректный формат e-mail. Адрес электронной почты должен содержать символ '@'.");
            }
            if (userService.isContainEmail(userDto.getEmail())) {
                if (!userDto.getEmail().equals(userService.getById(id).get().getEmail())) {
                    throw new RuntimeException("Адрес электронной почты уже используется другим пользователем.");
                }
            }
        }
        if (userDto.getName() != null) {
            if (userDto.getName().equals("")) {
                throw new BadRequestException("Поле не должно быть пустым.");
            }
        }
        log.info("Поступил запрос на обновление информации о пользователе {} c id = {}.", userDto.getName(), userDto.getId());
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Поступил запрос на удаление пользователя с id = {}.", id);
        userService.delete(id);
    }
}