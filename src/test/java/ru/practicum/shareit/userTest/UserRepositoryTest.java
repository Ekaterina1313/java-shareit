package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;
    User user = new User();

    @BeforeEach
    public void setup() {
        user.setName("Test User");
        user.setEmail("test@mail.com");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void testIsContainEmailWithExistingEmail() {
        boolean result = userRepository.existsByEmail("test@mail.com");
        assertTrue(result);
    }

    @Test
    public void testIsContainEmailWithNonExistingEmail() {
        boolean result = userRepository.existsByEmail("nonex@mail.com");
        assertFalse(result);
    }
}