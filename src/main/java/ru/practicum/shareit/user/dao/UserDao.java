package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    List<User> getAll();

    Optional<User> getById(Long id);

    User update(Long id, User user);

    void delete(Long id);

    boolean isContainEmail(String email);
}