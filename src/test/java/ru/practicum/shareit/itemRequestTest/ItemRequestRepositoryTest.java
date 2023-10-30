package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    User user = new User();
    ItemRequest request = new ItemRequest();

    @BeforeEach
    public void setup() {
        user.setName("UserName");
        user.setEmail("user@mail.com");
        entityManager.persist(user);
        entityManager.flush();

        request.setRequestor(user);
        request.setDescription("Test Request");
        request.setCreated(LocalDateTime.now());
        entityManager.persist(request);
        entityManager.flush();
    }

    @Test
    public void testFindByRequestorId() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(user.getId());
        assertEquals(1, requests.size());
        assertEquals("Test Request", requests.get(0).getDescription());
        assertEquals("user@mail.com", requests.get(0).getRequestor().getEmail());
    }
}