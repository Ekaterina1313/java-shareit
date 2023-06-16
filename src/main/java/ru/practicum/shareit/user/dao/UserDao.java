package ru.practicum.shareit.user.dao;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    User updateUser(long id);

    void deleteUser(long  id);
}
