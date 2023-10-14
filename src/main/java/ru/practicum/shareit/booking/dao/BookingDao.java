package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingDao {
    Booking create(Booking booking);

    List<Booking> getAll(Long userId);

    Optional<Booking> getById(Long id);

    Booking update(Booking booking);

    void delete(Long id);

    boolean isContainBooking(Long id);
}