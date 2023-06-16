package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dao.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

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
        }
        return userDao.createUser(userDto);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public UserDto getUserById(long id) {
        if (!userDao.isContain(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден.");
        }
        return userDao.getUserById(id);
    }
    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        if (!userDao.isContain(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = \" + id + \" не найден.");
        }
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().contains("@")) {
                throw new ValidationException("Некорректный формат e-mail. Адрес электронной почты должен содержать символ '@'.");
            }
        }
        if (userDto.getName() != null) {
            if (userDto.getName().equals("")) {
                throw new ValidationException("Поле не должно быть пустым.");
            }
        }
       return userDao.updateUser(id, userDto);
    }

    @Override
    public void deleteUser(long  id) {
        if (!userDao.isContain(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден.");
        }
       userDao.deleteUser(id);
    }

}
