package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getStatus()
        );
    }

    public static Booking fromBookingDto(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(),
                new User(),
                bookingDto.getStatus()
        );
    }
}