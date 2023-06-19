package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long userId);

    List<BookingDto> getAll(Long userId);

    Optional<BookingDto> getById(Long id);

    BookingDto update(Long id, Long userId, BookingDto bookingDto);

    void delete(Long id, Long userId);

    boolean isContainBooking(Long id);

    boolean isContainItem(Long id);

    boolean isContainUser(Long id);
}