package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserDao {
    User createUser(User user);

    Map<Long, User> getAllUsers();

    User getUserById(long id);

    User updateUser(long id, User user);

    void deleteUser(long id);

    boolean isContainUser(long id);

    boolean isContainEmail(String email);
}