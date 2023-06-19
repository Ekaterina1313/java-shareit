package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> mapOfAllUsers = new HashMap<>();
    private static Long id = 1L;

    @Override
    public User create(User user) {
        user.setId(id);
        id++;
        mapOfAllUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(mapOfAllUsers.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(mapOfAllUsers.get(id));
    }

    @Override
    public User update(Long id, User user) {
        User user2 = mapOfAllUsers.get(id);
        if (user.getName() != null) {
            user2.setName(user.getName());
        }
        if (user.getEmail() != null) {
            user2.setEmail(user.getEmail());
        }
        mapOfAllUsers.put(id, user2);
        return user2;
    }

    @Override
    public void delete(Long id) {
        mapOfAllUsers.remove(id);
    }

    @Override
    public boolean isContainUser(Long id) {
        return mapOfAllUsers.containsKey(id);
    }

    @Override
    public boolean isContainEmail(String email) {
        for (User element : mapOfAllUsers.values()) {
            if (element.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}