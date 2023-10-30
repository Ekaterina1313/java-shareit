package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImplBd;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingServiceImplDbTest {
    private BookingRepository bookingRepository;
    private BookingService bookingService;
    private UserService userService;
    private ItemRepository itemRepository;
    private ItemService itemService;
    private User testUser;
    private User anotherUser;
    private Item testItem;
    private Booking testBooking;


    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemService = mock(ItemService.class);
        userService = mock(UserService.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImplBd(bookingRepository, userService, itemRepository, itemService);
        testUser = new User(1L, "Test User", "user@example.com");
        anotherUser = new User(2L, "Another User", "another@mail.com");
        testItem = new Item(1L, "Test Item", "Description", true, null, testUser);
        testBooking = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                testItem, anotherUser, Status.APPROVED);

        when(userService.validUser(testUser.getId())).thenReturn(testUser);
        when(itemService.validItem(testItem.getId())).thenReturn(testItem);

    }

    @Test
    public void testCreateBookingSuccess() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);

        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingDto result = bookingService.create(bookingDto, 2L);

        assertEquals(Status.APPROVED, result.getStatus());
        assertEquals(1L, result.getItemId());
    }

    @Test
    public void testCreateBookingItemNotAvailable() {
        Item testItem2 = new Item(2L, "Test Item2", "Description2", false, null,
                testUser);

        when(itemService.validItem(2L)).thenReturn(testItem2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);

        assertThrows(PersonalValidationException.class, () -> bookingService.create(bookingDto, anotherUser.getId()));
    }

    @Test
    public void testCreateBookingByOwner() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(bookingDto, testUser.getId()));
    }

    @Test
    public void testConfirmStatusApproved() {
        Booking waitingBooking = new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                testItem, anotherUser, Status.WAITING);

        when(bookingRepository.findById(2L)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(waitingBooking);

        BookingDto result = bookingService.statusConfirm(2L, 1L, false);
        assertEquals(Status.REJECTED, result.getStatus());

        result = bookingService.statusConfirm(2L, 1L, true);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    public void testStatusConfirmWithNonOwner() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(userService.validUser(anotherUser.getId())).thenReturn(anotherUser);

        assertThrows(EntityNotFoundException.class, () -> bookingService.statusConfirm(testBooking.getId(),
                anotherUser.getId(), true));
    }

    @Test
    public void testGetByIdOwnerSuccess() {
        when(userService.validUser(anotherUser.getId())).thenReturn(anotherUser);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        BookingDto result = bookingService.getById(testBooking.getId(), anotherUser.getId());
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetByIdBookerSuccess() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        BookingDto result = bookingService.getById(1L, 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }


    @Test
    public void testGetByIdBookingNotFound() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(1L, 1L));
    }


    @Test
    public void testGetBookingsByBooker() {
        when(userService.validUser(anotherUser.getId())).thenReturn(anotherUser);
        Booking anotherBooking = new Booking(2L, LocalDateTime.now().minusHours(7),
                LocalDateTime.now().plusHours(1), testItem, anotherUser, Status.WAITING);
        Booking thirdBooking = new Booking(3L, LocalDateTime.now().minusHours(7),
                LocalDateTime.now().plusHours(6), testItem, anotherUser, Status.WAITING);
        List<Booking> userBookings = Arrays.asList(
                testBooking,
                anotherBooking,
                thirdBooking
        );

        when(bookingRepository.findByBooker(anotherUser)).thenReturn(userBookings);

        List<BookingDto> result = bookingService.getBookingsByBookerId("ALL", 0, 10, 2L);

        assertNotNull(result);
        assertEquals(userBookings.size(), result.size());
        for (BookingDto bookingDto : result) {
            assertEquals(anotherUser.getId(), bookingDto.getBookerId());
        }
    }


    @Test
    public void testGetBookingsByBookerInvalidState() {
        assertThrows(PersonalValidationException.class,
                () -> bookingService.getBookingsByBookerId("INVALID_STATE", 0, 10, 2L));
    }

    @Test
    public void testGetBookingsByOwner() {
        when(userService.validUser(anotherUser.getId())).thenReturn(anotherUser);
        Booking anotherBooking = new Booking(2L, LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2), testItem, anotherUser, Status.WAITING);
        Booking thirdBooking = new Booking(3L, LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(4), testItem, anotherUser, Status.WAITING);

        List<Booking> userBookings = Arrays.asList(
                testBooking,
                anotherBooking,
                thirdBooking
        );
        Mockito.when(bookingRepository.findByOwner(testUser)).thenReturn(userBookings);

        List<BookingDto> result = bookingService.getBookingsByOwnerId("ALL", 0, 10, 1L);

        assertNotNull(result);
        assertEquals(userBookings.size(), result.size());
        for (BookingDto bookingDto : result) {
            assertEquals(anotherUser.getId(), bookingDto.getBookerId());
        }
    }

    @Test
    public void testGetBookingsByOwnerInvalidState() {
        assertThrows(PersonalValidationException.class,
                () -> bookingService.getBookingsByOwnerId("INVALID_STATE", 0, 10, 1L));
    }
}