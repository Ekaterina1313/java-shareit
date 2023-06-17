package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> mapOfAllUsers = new HashMap<>();
    private static Long id = 1L;
    @Override
    public User createUser(User user) {
        user.setId(id);
        id++;
        mapOfAllUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        return mapOfAllUsers;
    }

    @Override
    public User getUserById(long id) {
        return mapOfAllUsers.get(id);
    }

    @Override
    public User updateUser(long id, User user) {
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
   public void deleteUser(long  id) {
        mapOfAllUsers.remove(id);
    }

    @Override
    public boolean isContainUser(long id) {
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
