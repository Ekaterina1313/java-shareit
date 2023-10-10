package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(User booker);

    @Query("select b from Booking b where b.booker = :booker and b.start < :time and b.end > :time")
    List<Booking> findCurrentBookings(@Param("booker") User booker, @Param("time")LocalDateTime time);

    @Query("select b from Booking b where b.booker = :booker and b.end < :endTime")
    List<Booking> findPastBookings(@Param("booker") User booker, @Param("endTime") LocalDateTime endTime);

    @Query("select b from Booking b where b.booker = :booker and b.start > :time")
    List<Booking> findFutureBookings(@Param("booker") User booker, @Param("time") LocalDateTime time);

    List<Booking> findBookingsByBookerAndStatus(User booker, Status status);
}
