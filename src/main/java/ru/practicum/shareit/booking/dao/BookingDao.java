package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingDao {
    Booking createBooking(Booking booking);

    List<Booking> getAllBookings();

    Booking getBookingById(long id);

    Booking updateBooking(long id);

    void deleteBooking(long id);
}