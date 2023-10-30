package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.PersonalValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(@Qualifier("bookingServiceImplBd") BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        if ((bookingDto.getStart() == null) || (bookingDto.getEnd() == null)) {
            throw new PersonalValidationException("Укажите время начала/окончания бронирования.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new PersonalValidationException("Время окончания бронирования не может быть раньше времени " +
                    "начала бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new PersonalValidationException("Время начала бронирования не может быть в прошлом.");
        }
        if (Objects.equals(bookingDto.getStart(), bookingDto.getEnd())) {
            throw new PersonalValidationException("Время начала бронирования не должно совпадать со временем " +
                    "окончания брони.");
        }
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto statusConfirm(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam("approved") String text) {
        boolean approved;
        isValid(userId);
        if (text.equalsIgnoreCase("true")) {
            approved = true;
        } else if (text.equalsIgnoreCase("false")) {
            approved = false;
        } else {
            throw new PersonalValidationException("Поле 'approved' должно принимать значение true либо false");
        }
        return bookingService.statusConfirm(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getBookingsByBookerId(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        isValidPagination(from, size);
        return bookingService.getBookingsByBookerId(state, from, size, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        isValidPagination(from, size);
        return bookingService.getBookingsByOwnerId(state, from, size, userId);
    }

    private boolean isValid(Long userId) {
        if (userId == null) {
            throw new PersonalValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        return true;
    }

    private boolean isValidPagination(int from, int size) {
        if (from < 0) {
            throw new PersonalValidationException("Параметр 'from' не должен принимать отрицательное значение.");
        }
        if (size <= 0) {
            throw new PersonalValidationException("Параметр 'size' не должен принимать пустое или отрицательное значение.");
        }
        return true;
    }
}