package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;

    @Autowired
    public BookingServiceImpl(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    @Override
    public Booking createBooking(Booking booking) {
        return bookingDao.createBooking(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDao.getAllBookings();
    }

    @Override
    public Booking getBookingById(long id) {
        return bookingDao.getBookingById(id);
    }

    @Override
    public Booking updateBooking(long id) {
        return bookingDao.updateBooking(id);
    }

    @Override
    public void deleteBooking(long  id) {
        bookingDao.deleteBooking(id);
    }
}
