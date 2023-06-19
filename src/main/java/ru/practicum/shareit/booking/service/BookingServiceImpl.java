package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Autowired
    public BookingServiceImpl(BookingDao bookingDao, UserDao userDao, ItemDao itemDao) {
        this.bookingDao = bookingDao;
        this.userDao = userDao;
        this.itemDao = itemDao;
    }

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        if (!itemDao.getById(bookingDto.getItemId()).get().getAvailable()) {
            throw new BadRequestException("Выбранная вещь недоступна для бронирования.");
        }
        Booking createdBooking = BookingMapper.fromBookingDto(bookingDto);
        createdBooking.setStatus(Status.WAITING);
        createdBooking.setBooker(userDao.getById(userId).get());
        createdBooking.setItem(itemDao.getById(bookingDto.getId()).get());
        return BookingMapper.toBookingDto(bookingDao.create(createdBooking));
    }

    @Override
    public List<BookingDto> getAll(Long userId) {
        return bookingDao.getAll(userId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookingDto> getById(Long id) {
        return Optional.of(BookingMapper.toBookingDto(bookingDao.getById(id).get()));
    }

    @Override
    public BookingDto update(Long id, Long userId, BookingDto bookingDto) {
        if (!Objects.equals(bookingDao.getById(id).get().getBooker().getId(), id)) {
            throw new BadRequestException("Пользователь с id = " + userId + " не оставлял бронь.");
        }
        bookingDto.setId(id);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setBooker(userDao.getById(userId).get());
        booking.setItem(itemDao.getById(bookingDto.getId()).get());
        return BookingMapper.toBookingDto(bookingDao.update(booking));
    }

    @Override
    public void delete(Long id, Long userId) {
        if (!Objects.equals(bookingDao.getById(id).get().getBooker().getId(), id)) {
            throw new BadRequestException("Пользователь с id = " + userId + " не оставлял бронь.");
        }
        bookingDao.delete(id);
    }

    @Override
    public boolean isContainBooking(Long id) {
        return bookingDao.isContainBooking(id);
    }

    @Override
    public boolean isContainItem(Long id) {
        return itemDao.isContainItem(id);
    }

    @Override
    public boolean isContainUser(Long id) {
        return userDao.isContainUser(id);
    }
}