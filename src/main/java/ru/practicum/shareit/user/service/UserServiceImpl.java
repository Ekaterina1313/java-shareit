package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throw new ValidationException("Имя не должно быть пустым.");
        } else if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Адрес электронной почты не должен быть пустым.");
        } else if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный формат e-mail. Адрес электронной почты должен содержать символ '@'.");
        } else if (userDao.isContainEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Адрес электронной почты уже используется другим пользователем.");
        }
        User user = UserMapper.fromUserDto(userDto);
        User createdUser = userDao.createUser(user);
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers().values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        if (!userDao.isContainUser(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден.");
        }
        return UserMapper.toUserDto(userDao.getUserById(id));
    }
    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        if (!userDao.isContainUser(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = \" + id + \" не найден.");
        }
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().contains("@")) {
                throw new ValidationException("Некорректный формат e-mail. Адрес электронной почты должен содержать символ '@'.");
            }
            if (userDao.isContainEmail(userDto.getEmail())) {
                if (!userDto.getEmail().equals(userDao.getUserById(id).getEmail())) {
                    throw new EmailAlreadyExistsException("Адрес электронной почты уже используется другим пользователем.");
                }
            }
        }
        if (userDto.getName() != null) {
            if (userDto.getName().equals("")) {
                throw new ValidationException("Поле не должно быть пустым.");
            }
        }
        User updatedUser = userDao.updateUser(id, UserMapper.fromUserDto(userDto));
       return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long  id) {
        if (!userDao.isContainUser(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден.");
        }
       userDao.deleteUser(id);
    }

}
