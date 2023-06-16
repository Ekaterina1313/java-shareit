package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
   public Booking createBooking(Booking booking) {
        return bookingService.createBooking(booking);
    }

    @PostMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable long id) {
        return bookingService.getBookingById(id);
    }

    @PatchMapping
    public Booking updateBooking(long id) {
        return bookingService.updateBooking(id);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable long  id) {
        bookingService.deleteBooking(id);
    }
}
