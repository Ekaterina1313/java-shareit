package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> mapOfAllUsers = new HashMap<>();
    @Override
    public User createUser(User user) {
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(mapOfAllUsers.values());
    }

    @Override
    public User getUserById(long id) {
        return mapOfAllUsers.get(id);
    }

    @Override
    public User updateUser(long id) {
        return mapOfAllUsers.get(id);
    }

    @Override
   public void deleteUser(long  id) {

    }
}
