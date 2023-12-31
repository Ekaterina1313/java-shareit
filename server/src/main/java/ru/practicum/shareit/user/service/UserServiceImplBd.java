package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImplBd implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImplBd(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User createdUser = userRepository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User userById = validUser(id);
        return UserMapper.toUserDto(userById);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User userById = validUser(id);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            if (!userDto.getEmail().equals(userById.getEmail())) {
                throw new RuntimeException("Адрес электронной почты уже используется другим пользователем.");
            }
        }
        if (userDto.getName() != null) {
            userById.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userById.setEmail(userDto.getEmail());
        }
        User updatedUser = userRepository.save(userById);
        log.info("Информация о пользователе {} с id = {} успешно обновлена.", userDto.getName(), userDto.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        validUser(id);
        userRepository.deleteById(id);
    }

    @Override
    public User validUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
    }
}