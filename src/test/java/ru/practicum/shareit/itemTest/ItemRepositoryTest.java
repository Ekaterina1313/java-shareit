package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    private TestEntityManager entityManager;

    User user1;
    User user2;
    ItemRequest itemRequest1;
    Item item1;
    Item item2;

    @BeforeEach
    public void setup() {
        user1 = new User();
        user1.setName("User1Name");
        user1.setEmail("user1@mail.com");
        entityManager.persist(user1);
        entityManager.flush();
        user2 = new User();
        user2.setName("User2Name");
        user2.setEmail("user2@mail.com");
        entityManager.persist(user2);
        entityManager.flush();

        itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user1);
        itemRequest1.setDescription("ItemRequest1Desc");
        itemRequest1.setCreated(LocalDateTime.now());
        entityManager.persist(itemRequest1);
        entityManager.flush();

        item1 = new Item();
        item1.setName("Item1Name");
        item1.setDescription("Item1Desc");
        item1.setAvailable(true);
        item1.setOwner(user2);
        item1.setRequestId(itemRequest1.getId());
        entityManager.persist(item1);
        entityManager.flush();
        item2 = new Item();
        item2.setName("Item2Name");
        item2.setDescription("Item2Desc");
        item2.setAvailable(true);
        item2.setOwner(user2);
        item2.setRequestId(itemRequest1.getId());
        entityManager.persist(item2);
        entityManager.flush();
    }

    @Test
    public void testSearchItems() {
        List<Item> items1 = itemRepository.searchItems("item1name");
        assertEquals(1, items1.size());
        assertEquals("Item1Name", items1.get(0).getName());

        List<Item> items2 = itemRepository.searchItems("item2desc");
        assertEquals(1, items2.size());
        assertEquals("Item2Desc", items2.get(0).getDescription());

        List<Item> items3 = itemRepository.searchItems("NonExistentText");
        assertEquals(0, items3.size());
    }

    @Test
    public void testFindByOwnerId() {
        List<Item> items = itemRepository.findByOwnerId(user2.getId());
        assertEquals(2, items.size());
        assertEquals("Item1Name", items.get(0).getName());
        assertEquals("Item2Name", items.get(1).getName());
    }

    @Test
    public void testFindByRequestIds() {
        List<Long> requestIds = List.of(itemRequest1.getId());
        List<Item> items = itemRepository.findByRequestIds(requestIds);
        assertEquals(2, items.size());
        assertEquals("Item1Name", items.get(0).getName());
        assertEquals("Item2Name", items.get(1).getName());
    }

    @Test
    public void testFindByRequestId() {
        List<Item> items = itemRepository.findByRequestId(itemRequest1.getId());
        assertEquals(2, items.size());
        assertEquals("Item1Name", items.get(0).getName());
        assertEquals("Item2Name", items.get(1).getName());
    }
}