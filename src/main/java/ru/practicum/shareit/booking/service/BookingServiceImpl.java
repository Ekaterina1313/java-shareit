package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
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
        bookingDao.getById(bookingDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        User userById = userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemDao.getById(bookingDto.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!itemById.getAvailable()) {
            throw new PersonalValidationException("Выбранная вещь недоступна для бронирования.");
        }
        Booking createdBooking = BookingMapper.fromBookingDto(bookingDto);
        createdBooking.setStatus(Status.WAITING);
        createdBooking.setBooker(userById);
        createdBooking.setItem(itemById);
        return BookingMapper.toBookingDto(bookingDao.create(createdBooking));
    }

    @Override
    public List<BookingDto> getAll(Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        return bookingDao.getAll(userId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto getById(Long id, Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Booking bookingById = bookingDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        return BookingMapper.toBookingDto(bookingById);
    }

    @Override
    public BookingDto update(Long id, Long userId, BookingDto bookingDto) {
        User userById = userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemDao.getById(bookingDto.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        Booking bookingById = bookingDao.getById(bookingDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        if (!Objects.equals(bookingById.getBooker().getId(), id)) {
            throw new PersonalValidationException("Пользователь с id = " + userId + " не оставлял бронь.");
        }
        bookingDto.setId(id);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setBooker(userById);
        booking.setItem(itemById);
        return BookingMapper.toBookingDto(bookingDao.update(booking));
    }

    @Override
    public void delete(Long id, Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Booking bookingById = bookingDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        if (!Objects.equals(bookingById.getBooker().getId(), id)) {
            throw new PersonalValidationException("Пользователь с id = " + userId + " не оставлял бронь.");
        }
        bookingDao.delete(id);
    }

    @Override
    public BookingDto statusConfirm(Long bookingId, Long userId, Boolean text) {
        return null;
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(States state, Long userId) {
        return null;
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(States state, Long userId) {
        return null;
    }

}