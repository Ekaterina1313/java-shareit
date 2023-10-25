package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long userId);

    List<BookingDto> getBookingsByBookerId(String state, int from, int size, Long userId);

    List<BookingDto> getBookingsByOwnerId(String state, int from, int size, Long userId);

    List<BookingDto> getAll(Long userId);

    BookingDto getById(Long id, Long userId);

    BookingDto update(Long id, Long userId, BookingDto bookingDto);

    void delete(Long id, Long userId);

    BookingDto statusConfirm(Long bookingId, Long userId, Boolean text);
}