package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
public class ItemServiceIntegrationTest {

    @Qualifier("itemServiceImplDb")
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @Test
    public void testGetAllItemsForUser() {
        User user1 = new User(1L, "User1", "user1@mail.com");
        userRepository.save(user1);
        User user2 = new User(2L, "User2", "user2@mail.com");
        userRepository.save(user2);

        Item item1 = new Item(1L, "Name 1", "Desc 1", true, null, user1);
        itemRepository.save(item1);
        Item item2 = new Item(2L, "Name 2", "Desc 2", true, null, user1);
        itemRepository.save(item2);
        Item item3 = new Item(3L, "Name 3", "Desc 3", true, null, user1);
        itemRepository.save(item3);
        Item item4 = new Item(4L, "Name 4", "Desc 4", true, null, user2);
        itemRepository.save(item4);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.getAll(999L, 0, 10));

        List<ItemDtoToGet> resultUser1 = itemService.getAll(user1.getId(), 0, 2);
        assertNotNull(resultUser1);
        assertEquals(2, resultUser1.size());
        assertEquals("Name 2", resultUser1.get(0).getName());
        assertEquals("Desc 1", resultUser1.get(1).getDescription());

        resultUser1 = itemService.getAll(user1.getId(), 2, 10);
        assertEquals(1, resultUser1.size());
        assertEquals("Name 3", resultUser1.get(0).getName());

        resultUser1 = itemService.getAll(user1.getId(), 0, 10);
        assertEquals(3, resultUser1.size());

        List<ItemDtoToGet> resultUser2 = itemService.getAll(user2.getId(), 0, 10);
        assertEquals(1, resultUser2.size());
        assertEquals("Name 4", resultUser2.get(0).getName());

        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2),
                item1, user2, Status.APPROVED);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(9), LocalDateTime.now().minusDays(8),
                item2, user2, Status.APPROVED);
        bookingRepository.save(booking2);
        Booking booking3 = new Booking(3L, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(6),
                item3, user2, Status.APPROVED);
        bookingRepository.save(booking3);

        Comment comment1 = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        commentRepository.save(comment1);
        Comment comment2 = new Comment(2L, "comment2", item2, user2, LocalDateTime.now());
        commentRepository.save(comment2);
        Comment comment3 = new Comment(3L, "comment3", item3, user2, LocalDateTime.now());
        commentRepository.save(comment3);

        resultUser1 = itemService.getAll(user1.getId(), 0, 10);
        assertEquals(3, resultUser1.size());
        assertEquals("Name 3", resultUser1.get(0).getName());
        assertEquals("comment3", resultUser1.get(0).getComments().get(0).getText());
        assertEquals(2L, resultUser1.get(1).getLastBooking().getId());

    }
}