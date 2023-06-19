package ru.practicum.shareit.booking.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class BookingDaoImpl implements BookingDao {
    private final Map<Long, Booking> mapOfBookings = new HashMap<>();
    private static Long bId = 1L;

    @Override
    public Booking create(Booking booking) {
        booking.setId(bId);
        bId++;
        mapOfBookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public List<Booking> getAll(Long id) {
        return mapOfBookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Booking> getById(Long id) {
        return Optional.ofNullable(mapOfBookings.get(id));
    }

    @Override
    public Booking update(Booking booking) {
        Booking mapBooking = mapOfBookings.get(booking.getId());
        if (booking.getStart() != null) {
            mapBooking.setStart(booking.getStart());
        }
        if (booking.getEnd() != null) {
            mapBooking.setEnd(booking.getEnd());
        }
        mapOfBookings.put(mapBooking.getId(), mapBooking);
        return mapBooking;
    }

    @Override
    public void delete(Long id) {
        mapOfBookings.remove(id);
    }

    @Override
    public boolean isContainBooking(Long id) {
        return mapOfBookings.containsKey(id);
    }
}