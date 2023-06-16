package ru.practicum.shareit.booking.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class BookingDaoImpl implements BookingDao {
    private Map<Long, Booking> mapOfBookings = new HashMap();

    @Override
    public Booking createBooking(Booking booking) {
        return booking;
    }

    @Override
    public List<Booking> getAllBookings() {
        return new ArrayList<>(mapOfBookings.values());
    }

    @Override
    public Booking getBookingById(long id) {
        return mapOfBookings.get(id);
    }

    @Override
    public Booking updateBooking(long id) {
        return mapOfBookings.get(id);
    }

    @Override
     public void deleteBooking(long  id) {

    }
}
