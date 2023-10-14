package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.booker = :booker ORDER BY b.start desc")
    List<Booking> findByBooker(User booker);

    @Query("select b from Booking b where b.booker = :booker and b.start < :time and b.end > :time ORDER BY b.start desc")
    List<Booking> findByBookerCurrent(@Param("booker") User booker, @Param("time") LocalDateTime time);

    @Query("select b from Booking b where b.booker = :booker and b.end < :endTime ORDER BY b.start desc")
    List<Booking> findByBookerPast(@Param("booker") User booker, @Param("endTime") LocalDateTime endTime);

    @Query("select b from Booking b where b.booker = :booker and b.start > :time ORDER BY b.start desc")
    List<Booking> findByBookerFuture(@Param("booker") User booker, @Param("time") LocalDateTime time);

    @Query("select b from Booking b where b.booker = :booker and b.status = :status ORDER BY b.start desc")
    List<Booking> findBookingsByBookerAndStatus(User booker, Status status);

    @Query("select b from Booking b where b.item.owner = :owner ORDER BY b.start desc")
    List<Booking> findByOwner(@Param("owner") User owner);

    @Query("select b from Booking b where b.item.owner = :owner and b.start < :time and b.end > :time " +
            " ORDER BY b.start desc")
    List<Booking> findByOwnerCurrent(@Param("owner") User owner, @Param("time") LocalDateTime time);

    @Query("select b from Booking b where b.item.owner = :owner and b.end < :endTime ORDER BY b.start desc")
    List<Booking> findByOwnerPast(@Param("owner") User owner, @Param("endTime") LocalDateTime endTime);

    @Query("select b from Booking b where b.item.owner = :owner and b.start > :time ORDER BY b.start desc")
    List<Booking> findByOwnerFuture(@Param("owner") User owner, @Param("time")LocalDateTime time);

    @Query("select b from Booking b where b.item.owner = :owner and b.status = :status ORDER BY b.start desc")
    List<Booking> findBookingsByOwnerAndStatus(User owner, Status status);

    @Query("select b from Booking  b where b.item.id = :itemId ")
    List<Booking> findAllByItemId(Long itemId);

    @Query("select b from Booking  b where b.item.id  in :itemIds ")
    List<Booking> findAllByItemIdIn(@Param("itemIds") List<Long> itemIds);

   // @Query("select b from Booking  b where b.booker.id = :bookerId and b.item.id = :itemId ")
    List<Booking> findBookingByBookerIdAndItemId(Long bookerId, Long itemId);

}
