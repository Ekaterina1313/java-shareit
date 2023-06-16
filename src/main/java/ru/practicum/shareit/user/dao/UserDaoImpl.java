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
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        user.setId(id);
        id++;
        mapOfAllUsers.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = mapOfAllUsers.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        return userDtoList;
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(mapOfAllUsers.get(id));
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        mapOfAllUsers.put(id, user);
        return UserMapper.toUserDto(user);
    }

    @Override
   public void deleteUser(long  id) {
        mapOfAllUsers.remove(id);
    }

    @Override
    public boolean isContain(long id) {
        return mapOfAllUsers.containsKey(id);
    }
}
