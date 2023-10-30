package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
public class ItemRequestServiceIntegrationTest {
    @Qualifier("itemRequestServiceImplBd")
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "User1", "user1@mail.com");
        userRepository.save(user1);
        user2 = new User(2L, "User2", "user2@mail.com");
        userRepository.save(user2);
        user3 = new User(3L, "User3", "user3@mail.com");
        userRepository.save(user3);
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @Test
    public void getOtherUsersRequestsTest() {
        ItemRequest itemRequest1 = new ItemRequest(1L, "Desc1", user1, LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);
        ItemRequest itemRequest2 = new ItemRequest(2L, "Desc2", user1, LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);
        ItemRequest itemRequest3 = new ItemRequest(3L, "Desc3", user1, LocalDateTime.now());
        itemRequestRepository.save(itemRequest3);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getAllByOthers(999L, 0, 10));

        List<ItemRequestDtoFull> result = itemRequestService.getAllByOthers(user3.getId(), 0, 10);
        assertEquals(3, result.size());
        assertEquals("Desc3", result.get(0).getDescription());
        assertEquals(new ArrayList<>(), result.get(0).getItems());

        Item item1 = new Item(1L, "Name 1", "Desc 1", true, itemRequest1.getId(), user2);
        itemRepository.save(item1);
        Item item2 = new Item(2L, "Name 2", "Desc 2", true, itemRequest1.getId(), user2);
        itemRepository.save(item2);
        Item item3 = new Item(3L, "Name 3", "Desc 3", true, itemRequest2.getId(), user2);
        itemRepository.save(item3);
        Item item4 = new Item(4L, "Name 4", "Desc 4", true, itemRequest3.getId(), user2);
        itemRepository.save(item4);

        result = itemRequestService.getAllByOthers(user3.getId(), 0, 2);
        assertEquals(2, result.size());
        assertEquals(user2.getId(), result.get(1).getId());
        assertEquals("Name 4", result.get(0).getItems().get(0).getName());

        result = itemRequestService.getAllByOthers(user3.getId(), 0, 10);
        assertEquals(3, result.size());
        assertEquals("Desc3", result.get(0).getDescription());

        result = itemRequestService.getAllByOthers(user3.getId(), 1, 2);
        assertEquals(1, result.size());
    }
}