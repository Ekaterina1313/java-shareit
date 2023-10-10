package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceRepository implements BookingService{
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceRepository(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!itemById.getAvailable()) {
            throw new PersonalValidationException("Выбранная вещь недоступна для бронирования.");
        }
        Booking createdBooking = BookingMapper.fromBookingDto(bookingDto);
        createdBooking.setStatus(Status.WAITING);
        createdBooking.setBooker(userById);
        createdBooking.setItem(itemById);
        itemById.setAvailable(false);
        itemRepository.save(itemById);
        return BookingMapper.toBookingDto(bookingRepository.save(createdBooking));
    }

    @Override
    public BookingDto statusConfirm(Long bookingId, Long userId, Boolean text) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Booking bookingById = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        Item itemById = bookingById.getItem();
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь не является владельцем вещи.");
        }
        if (text) {
            bookingById.setStatus(Status.APPROVED);
        } else {
            bookingById.setStatus(Status.REJECTED);
            itemById.setAvailable(true);
            itemRepository.save(itemById);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(bookingById));
    }

    @Override
    public BookingDto getById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Booking bookingById = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        Item itemById = bookingById.getItem();
        if ((!Objects.equals(bookingById.getBooker().getId(), userId)) &&
                (!Objects.equals(itemById.getOwner().getId(), userId)))
        {
            throw new RuntimeException("Пользователь не является владельцем вещи или арендатором.");
        }
        return BookingMapper.toBookingDto(bookingById);
    }


    @Override
    public List<BookingDto> getBookingsByUserId(States state, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        List<Booking> listOfBookings;
        if (state == States.ALL) {
            listOfBookings = bookingRepository.findByBooker(user);
        } else if (state == States.CURRENT) {
            listOfBookings = bookingRepository.findCurrentBookings(user, LocalDateTime.now());
        } else if (state == States.PAST) {
            listOfBookings = bookingRepository.findPastBookings(user, LocalDateTime.now());
        } else if (state == States.FUTURE) {
            listOfBookings = bookingRepository.findFutureBookings(user, LocalDateTime.now());
        } else if (state == States.WAITING) {
            listOfBookings = bookingRepository.findBookingsByBookerAndStatus(user, Status.WAITING);
        } else if (state == States.REJECTED) {
            listOfBookings = bookingRepository.findBookingsByBookerAndStatus(user, Status.REJECTED);
        } else {
            throw new RuntimeException("Неподдерживаемое состояние бронирования: " + state);
        }
        return listOfBookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAll(Long userId) {
        return null;
    }


    @Override
    public BookingDto update(Long id, Long userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public void delete(Long id, Long userId) {

    }
}
