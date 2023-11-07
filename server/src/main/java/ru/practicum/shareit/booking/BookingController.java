package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


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
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {

        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto statusConfirm(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam("approved") boolean approved) {

        return bookingService.statusConfirm(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {

        return bookingService.getById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getBookingsByBookerId(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {

        return bookingService.getBookingsByBookerId(state, from, size, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {

        return bookingService.getBookingsByOwnerId(state, from, size, userId);
    }
}