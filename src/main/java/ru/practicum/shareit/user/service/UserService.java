package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);

    User validUser(Long userId);

    // void setUserRepository(UserRepository userRepository);
}