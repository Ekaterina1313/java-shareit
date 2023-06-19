package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    Optional<UserDto> getById(Long id);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);

    boolean isContainEmail(String email);

    boolean isContainUser(Long id);
}