package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.PersonalValidationException;

import java.time.LocalDateTime;
import java.util.Objects;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody BookingDto bookingDto,
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
        return bookingClient.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> statusConfirm(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
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
        return bookingClient.confirmStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable Long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingsByBookerId(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @RequestParam(name = "size", defaultValue = "10") int size,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        isValidPagination(from, size);
        return bookingClient.getAllByBooker(state, from, size, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from", defaultValue = "0") int from,
                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        isValid(userId);
        isValidPagination(from, size);
        return bookingClient.getAllByOwner(state, from, size, userId);
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