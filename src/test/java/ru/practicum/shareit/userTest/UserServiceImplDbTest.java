package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImplBd;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceImplDbTest {
    private UserServiceImplBd userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImplBd(userRepository);
    }

    @Test
    public void testCreate() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("user@mail.com");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });
        UserDto createdUser = userService.create(userDto);
        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals("user@mail.com", createdUser.getEmail());
    }

    @Test
    public void testGetAll() {
        User user1 = new User(1L, "User 1", "user1@mail.com");
        User user2 = new User(2L, "User 2", "user2@mail.com");
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getAll();

        assertEquals(2, userDtos.size());

        UserDto userDto1 = userDtos.get(0);
        assertEquals(1L, userDto1.getId());
        assertEquals("User 1", userDto1.getName());
        assertEquals("user1@mail.com", userDto1.getEmail());

        UserDto userDto2 = userDtos.get(1);
        assertEquals(2L, userDto2.getId());
        assertEquals("User 2", userDto2.getName());
        assertEquals("user2@mail.com", userDto2.getEmail());
    }

    @Test
    public void testGetById() {
        User user = new User(1L, "Test User", "user@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(1L);

        assertEquals(1L, userDto.getId());
        assertEquals("Test User", userDto.getName());
        assertEquals("user@mail.com", userDto.getEmail());
    }


    @Test
    public void testUpdateUserWithValidData() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updated@mail.com");

        User user = new User(userId, "Test User", "user@mail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto updatedUser = userService.update(userId, userDto);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updated@mail.com", updatedUser.getEmail());
    }

    @Test
    public void testUpdateUserWithInvalidEmail() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("existing@mail.com");

        User user = new User(userId, "Test User", "user@mail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@mail.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.update(userId, userDto));
    }
}