package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager entityManager;
    LocalDateTime currentTime;
    User user1;
    User user2;
    Item item;
    Item otherItem;
    Booking booking1;
    Booking booking2;
    Booking booking3;
    Booking booking4;

    @BeforeEach
    public void setup() {
        currentTime = LocalDateTime.now();

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

        item = new Item();
        item.setName("ItemName");
        item.setDescription("ItemDesc");
        item.setAvailable(true);
        item.setOwner(user2);
        entityManager.persist(item);
        entityManager.flush();

        otherItem = new Item();
        otherItem.setName("OtherItemName");
        otherItem.setDescription("OtherItemDesc");
        otherItem.setAvailable(true);
        otherItem.setOwner(user2);
        entityManager.persist(otherItem);
        entityManager.flush();

        booking1 = new Booking();
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.of(2020, 10, 20, 20, 30));
        booking1.setEnd(LocalDateTime.of(2020, 11, 20, 20, 30));
        booking1.setItem(item);
        booking1.setBooker(user1);
        entityManager.persist(booking1);
        entityManager.flush();

        booking2 = new Booking();
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.of(2025, 10, 20, 20, 30));
        booking2.setEnd(LocalDateTime.of(2025, 11, 20, 20, 30));
        booking2.setItem(item);
        booking2.setBooker(user1);
        entityManager.persist(booking2);
        entityManager.flush();

        booking3 = new Booking();
        booking3.setStatus(Status.WAITING);
        booking3.setStart(LocalDateTime.of(2020, 10, 20, 20, 30));
        booking3.setEnd(LocalDateTime.of(2025, 11, 20, 20, 30));
        booking3.setItem(item);
        booking3.setBooker(user1);
        entityManager.persist(booking3);
        entityManager.flush();

        booking4 = new Booking();
        booking4.setStatus(Status.REJECTED);
        booking4.setStart(LocalDateTime.of(2026, 10, 20, 20, 30));
        booking4.setEnd(LocalDateTime.of(2026, 11, 20, 20, 30));
        booking4.setItem(otherItem);
        booking4.setBooker(user1);
        entityManager.persist(booking4);
        entityManager.flush();

    }

    @Test
    public void testFindByBooker() {
        List<Booking> bookings = bookingRepository.findByBooker(user1);
        assertEquals(4, bookings.size());

        List<Booking> bookingsUser2 = bookingRepository.findByBooker(user2);
        assertEquals(0, bookingsUser2.size());
    }

    @Test
    public void testFindByBookerCurrent() {
        List<Booking> currentBookings1 = bookingRepository.findByBookerCurrent(user1, currentTime);
        assertEquals(1, currentBookings1.size());

        List<Booking> currentBookings2 = bookingRepository.findByBookerCurrent(user2, currentTime);
        assertEquals(0, currentBookings2.size());
    }

    @Test
    public void testFindByBookerPast() {
        List<Booking> currentBookings1 = bookingRepository.findByBookerPast(user1, currentTime);
        assertEquals(1, currentBookings1.size());

        List<Booking> currentBookings2 = bookingRepository.findByBookerPast(user2, currentTime);
        assertEquals(0, currentBookings2.size());
    }

    @Test
    public void testFindByBookerFuture() {
        List<Booking> currentBookings1 = bookingRepository.findByBookerFuture(user1, currentTime);
        assertEquals(2, currentBookings1.size());

        List<Booking> currentBookings2 = bookingRepository.findByBookerFuture(user2, currentTime);
        assertEquals(0, currentBookings2.size());
    }

    @Test
    public void testFindBookingsByBookerAndStatus() {
        List<Booking> waitingBookingsUser1 = bookingRepository.findBookingsByBookerAndStatus(user1, Status.WAITING);
        assertEquals(3, waitingBookingsUser1.size());

        List<Booking> rejectedBookingsUser1 = bookingRepository.findBookingsByBookerAndStatus(user1, Status.REJECTED);
        assertEquals(1, rejectedBookingsUser1.size());

        List<Booking> waitingBookingsUser2 = bookingRepository.findBookingsByBookerAndStatus(user2, Status.WAITING);
        assertEquals(0, waitingBookingsUser2.size());
    }

    @Test
    public void testFindByOwner() {
        List<Booking> ownerBookings = bookingRepository.findByOwner(user2);
        assertEquals(4, ownerBookings.size());

        List<Booking> ownerBookingsUser1 = bookingRepository.findByOwner(user1);
        assertEquals(0, ownerBookingsUser1.size());
    }

    @Test
    public void testFindByOwnerPast() {
        List<Booking> pastBookingsUser2 = bookingRepository.findByOwnerPast(user2, currentTime);
        assertEquals(1, pastBookingsUser2.size());

        List<Booking> pastBookingsUser1 = bookingRepository.findByOwnerPast(user1, currentTime);
        assertEquals(0, pastBookingsUser1.size());
    }

    @Test
    public void testFindByOwnerFuture() {
        List<Booking> pastBookingsUser2 = bookingRepository.findByOwnerFuture(user2, currentTime);
        assertEquals(2, pastBookingsUser2.size());

        List<Booking> pastBookingsUser1 = bookingRepository.findByOwnerFuture(user1, currentTime);
        assertEquals(0, pastBookingsUser1.size());
    }

    @Test
    public void testFindByOwnerCurrent() {
        List<Booking> pastBookingsUser2 = bookingRepository.findByOwnerCurrent(user2, currentTime);
        assertEquals(1, pastBookingsUser2.size());

        List<Booking> pastBookingsUser1 = bookingRepository.findByOwnerCurrent(user1, currentTime);
        assertEquals(0, pastBookingsUser1.size());
    }

    @Test
    public void testFindByOwnerAndStatus() {
        List<Booking> ownerBookingsWaiting = bookingRepository.findBookingsByOwnerAndStatus(user2, Status.WAITING);
        assertEquals(3, ownerBookingsWaiting.size());

        List<Booking> ownerBookingsRejected = bookingRepository.findBookingsByOwnerAndStatus(user2, Status.REJECTED);
        assertEquals(1, ownerBookingsRejected.size());

        List<Booking> owner1BookingsWaiting = bookingRepository.findBookingsByOwnerAndStatus(user1, Status.WAITING);
        assertEquals(0, owner1BookingsWaiting.size());
    }

    @Test
    public void testFindAllByItemId() {
        List<Booking> bookingsForItem = bookingRepository.findAllByItemId(item.getId());
        assertEquals(3, bookingsForItem.size());

        List<Booking> bookingsForOtherItem = bookingRepository.findAllByItemId(otherItem.getId());
        assertEquals(1, bookingsForOtherItem.size());
    }

    @Test
    public void testFindAllByItemIdIn() {
        List<Booking> bookingsForItemIds = bookingRepository
                .findAllByItemIdIn(Arrays.asList(item.getId(), otherItem.getId()));

        assertEquals(4, bookingsForItemIds.size());
    }

    @Test
    public void testFindByBookedIdAndItemId() {
        List<Booking> bookingsForUser1AndItem = bookingRepository.findBookingByBookerIdAndItemId(user1.getId(),
                item.getId());
        assertEquals(3, bookingsForUser1AndItem.size());

        List<Booking> bookingsForUser2AndItem = bookingRepository.findBookingByBookerIdAndItemId(user2.getId(),
                item.getId());
        assertEquals(0, bookingsForUser2AndItem.size());
    }
}