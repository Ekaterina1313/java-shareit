package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!bookingService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!bookingService.isContainItem(bookingDto.getItemId())) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания бронирования не может быть раньше времени начала бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Время начала бронирования не может быть в прошлом.");
        }
        return bookingService.create(bookingDto, userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!bookingService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        return bookingService.getAll(userId);
    }

    @GetMapping("/{id}")
    public Optional<BookingDto> getById(@PathVariable Long id,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!bookingService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!bookingService.isContainBooking(id)) {
            throw new EntityNotFoundException("Бронь c указанным id не найдена.");
        }
        return bookingService.getById(id);
    }

    @PatchMapping("/{id}")
    public BookingDto update(@PathVariable Long id,
                             @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingDto bookingDto) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!bookingService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь под указанным id = " + userId + " не найден.");
        }
        if (!bookingService.isContainItem(bookingDto.getItemId())) {
            throw new EntityNotFoundException("Вещь с указанным id не найдена.");
        }
        if (!bookingService.isContainBooking(id)) {
            throw new EntityNotFoundException("Бронь с указанным id не найдена.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания бронирования не может быть раньше времени начала бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Время начала бронирования не может быть в прошлом.");
        }

        return bookingService.update(id, userId, bookingDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new BadRequestException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!bookingService.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!bookingService.isContainBooking(id)) {
            throw new EntityNotFoundException("Бронь с указанным id не найдена.");
        }
        bookingService.delete(id, userId);
    }
}