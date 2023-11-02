package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceIntegrationTest {
    @Qualifier("bookingServiceImplBd")
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @Test
    public void getUserBookingsTest() {
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
                () -> bookingService.getBookingsByBookerId("ALL", 0, 10, 999L));
        assertThrows(PersonalValidationException.class,
                () -> bookingService.getBookingsByOwnerId("WRONGWORD", 0, 10, 1L));

        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2),
                item1, user2, Status.APPROVED);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                item1, user2, Status.APPROVED);
        bookingRepository.save(booking2);
        Booking booking3 = new Booking(3L, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(8),
                item1, user2, Status.REJECTED);
        bookingRepository.save(booking3);

        Comment comment1 = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        commentRepository.save(comment1);
        Comment comment2 = new Comment(2L, "comment2", item1, user2, LocalDateTime.now());
        commentRepository.save(comment2);
        Comment comment3 = new Comment(3L, "comment3", item1, user2, LocalDateTime.now());
        commentRepository.save(comment3);
        List<BookingDto> result = bookingService.getBookingsByBookerId("ALL", 0, 2, user2.getId());
        assertEquals(2, result.size());

        result = bookingService.getBookingsByBookerId("ALL", 2, 10, user2.getId());
        assertEquals(1, result.size());

        result = bookingService.getBookingsByBookerId("ALL", 0, 10, user2.getId());
        assertEquals(3, result.size());
        assertEquals(1L, result.get(2).getId());
        assertEquals(Status.REJECTED, result.get(0).getStatus());

        result = bookingService.getBookingsByBookerId("CURRENT", 0, 10, user2.getId());
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());

        result = bookingService.getBookingsByBookerId("PAST", 0, 10, user2.getId());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        result = bookingService.getBookingsByBookerId("FUTURE", 0, 10, user2.getId());
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());

        result = bookingService.getBookingsByBookerId("WAITING", 0, 10, user2.getId());
        assertEquals(0, result.size());
        assertEquals(new ArrayList<>(), result);

        result = bookingService.getBookingsByBookerId("REJECTED", 0, 10, user2.getId());
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());

    }
}