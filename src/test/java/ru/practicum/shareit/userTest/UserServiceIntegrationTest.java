package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
public class UserServiceIntegrationTest {
    @Qualifier("userServiceImplBd")
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @Test
    public void userUpdateTest() {
        User user = new User(1L, "User1", "user1@mail.com");
        userRepository.save(user);

        UserDto userDto = new UserDto(1L, null, null);

        assertThrows(EntityNotFoundException.class,
                () -> userService.update(999L, userDto));

        UserDto result = userService.update(1L, userDto);
        assertEquals("User1", result.getName());
        assertEquals("user1@mail.com", result.getEmail());

        User user2 = new User(2L, "User2", "user2@mail.com");
        userRepository.save(user2);
        userDto.setEmail(user2.getEmail());

        assertThrows(RuntimeException.class,
                () -> userService.update(1L, userDto));

        userDto.setEmail("newmail2mail.com");
        userDto.setName("asdf");
        result = userService.update(1L, userDto);
        assertEquals("newmail2mail.com", result.getEmail());
        assertEquals("asdf", result.getName());
    }
}