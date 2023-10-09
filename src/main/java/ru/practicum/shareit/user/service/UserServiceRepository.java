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
public class UserServiceRepository implements UserService{
    private final UserRepository userRepository;

    @Autowired
    public UserServiceRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        /*if (isContainEmail(userDto.getEmail())) {
            throw new RuntimeException("Указанный e-mail уже занят.");
        }*/
        User createdUser = userRepository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User userById = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден."));
        return UserMapper.toUserDto(userById);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User userById = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден."));
        if (isContainEmail(userDto.getEmail())) {
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
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден."));
        userRepository.deleteById(id);
    }

    private boolean isContainEmail(String email) {
        return userRepository.isContainEmail(email);
    }
}
