package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Поступил запрос на получение списка пользователей.");
        return userClient.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throw new PersonalValidationException("Имя не должно быть пустым.");
        } else if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new PersonalValidationException("Адрес электронной почты не должен быть пустым.");
        } else if (!userDto.getEmail().contains("@")) {
            throw new PersonalValidationException("Некорректный формат e-mail. " +
                    "Адрес электронной почты должен содержать символ '@'.");
        }
        log.info("Поступил запрос на создание пользователя {} c id = {}.", userDto.getName(), userDto.getId());
        return userClient.create(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Поступил запрос на получение пользователя с id = {}.", id);
        return userClient.getById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().contains("@")) {
                throw new PersonalValidationException("Некорректный формат e-mail. " +
                        "Адрес электронной почты должен содержать символ '@'.");
            }
        }
        if (userDto.getName() != null) {
            if (userDto.getName().equals("")) {
                throw new PersonalValidationException("Поле не должно быть пустым.");
            }
        }
        log.info("Поступил запрос на обновление информации о пользователе {} c id = {}.",
                userDto.getName(), userDto.getId());
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Поступил запрос на удаление пользователя с id = {}.", id);
        userClient.delete(id);
    }
}