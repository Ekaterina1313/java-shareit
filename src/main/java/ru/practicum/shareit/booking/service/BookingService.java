package ru.practicum.shareit.booking.service;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.States;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long userId);
    List<BookingDto> getBookingsByBookerId(String state, Long userId);

    List<BookingDto> getBookingsByOwnerId(String state, Long userId);

    List<BookingDto> getAll(Long userId);

    BookingDto getById(Long id, Long userId);

    BookingDto update(Long id, Long userId, BookingDto bookingDto);

    void delete(Long id, Long userId);

     BookingDto statusConfirm(Long bookingId, Long userId, Boolean text);


}