package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
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
    public UserDto create(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        User createdUser = userDao.create(user);
        log.info("Пользователь {} с id = {} успешно добавлен.", userDto.getName(), userDto.getId());
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public List<UserDto> getAll() {
        return userDao.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        return Optional.of(UserMapper.toUserDto(userDao.getById(id).get()));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User updatedUser = userDao.update(id, UserMapper.fromUserDto(userDto));
        log.info("Информация о пользователе {} с id = {} успешно обновлена.", userDto.getName(), userDto.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        if (!userDao.isContainUser(id)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + id + " не найден.");
        }
        userDao.delete(id);
    }

    @Override
    public boolean isContainUser(Long id) {
        return userDao.isContainUser(id);
    }

    @Override
    public boolean isContainEmail(String email) {
        return userDao.isContainEmail(email);
    }
}