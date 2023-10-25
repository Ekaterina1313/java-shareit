package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.*;

@Service
@Slf4j
public class BookingServiceImplBd implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImplBd(BookingRepository bookingRepository,
                                @Qualifier("userServiceImplBd") UserService userService, ItemRepository itemRepository,
                                @Qualifier("itemServiceImplBd") ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        User userById = userService.validUser(userId);
        Item itemById = itemService.validItem(bookingDto.getItemId());
        if (Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Вещь недоступна для бронирования.");
        }
        if (!itemById.getAvailable()) {
            throw new PersonalValidationException("Выбранная вещь недоступна для бронирования.");
        }
        Booking createdBooking = BookingMapper.fromBookingDto(bookingDto);
        createdBooking.setStatus(WAITING);
        createdBooking.setBooker(userById);
        createdBooking.setItem(itemById);
        return BookingMapper.toBookingDto(bookingRepository.save(createdBooking));
    }

    @Override
    public BookingDto statusConfirm(Long bookingId, Long userId, Boolean approved) {
        userService.validUser(userId);
        Booking bookingToConfirm = validBooking(bookingId);
        if (bookingToConfirm.getStatus().equals(APPROVED)) {
            throw new PersonalValidationException("Нельзя изменить состояние брони после подтверждения.");
        }
        Item itemById = bookingToConfirm.getItem();
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь не является владельцем вещи.");
        }

        if (approved) {
            bookingToConfirm.setStatus(APPROVED);
        } else {
            bookingToConfirm.setStatus(REJECTED);
            itemById.setAvailable(true);
            itemRepository.save(itemById);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(bookingToConfirm));
    }

    @Override
    public BookingDto getById(Long id, Long userId) {
        userService.validUser(userId);
        Booking bookingById = validBooking(id);
        Item itemById = bookingById.getItem();
        if ((!Objects.equals(bookingById.getBooker().getId(), userId)) &&
                (!Objects.equals(itemById.getOwner().getId(), userId))) {
            throw new EntityNotFoundException("Пользователь не является владельцем вещи или арендатором.");
        }
        return BookingMapper.toBookingDto(bookingById);
    }


    @Override
    public List<BookingDto> getBookingsByBookerId(String state, int from, int size, Long userId) {
        User user = userService.validUser(userId);
        List<Booking> listOfBookings;
        if (state.equalsIgnoreCase("all")) {
            listOfBookings = bookingRepository.findByBooker(user);
        } else if (state.equalsIgnoreCase("current")) {
            listOfBookings = bookingRepository.findByBookerCurrent(user, LocalDateTime.now());
        } else if (state.equalsIgnoreCase("past")) {
            listOfBookings = bookingRepository.findByBookerPast(user, LocalDateTime.now());
        } else if (state.equalsIgnoreCase("future")) {
            listOfBookings = bookingRepository.findByBookerFuture(user, LocalDateTime.now());
        } else if (state.equalsIgnoreCase("waiting")) {
            listOfBookings = bookingRepository.findBookingsByBookerAndStatus(user, WAITING);
        } else if (state.equalsIgnoreCase("rejected")) {
            listOfBookings = bookingRepository.findBookingsByBookerAndStatus(user, REJECTED);
        } else {
            throw new PersonalValidationException("Unknown state: " + state);
        }
        return listOfBookings.stream()
                .skip(from)
                .limit(size)
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(String state, int from, int size, Long userId) {
        User user = userService.validUser(userId);
        List<Booking> listOfBookings;

        if (state.equalsIgnoreCase("all")) {
            listOfBookings = bookingRepository.findByOwner(user);
        } else if (state.equalsIgnoreCase("current")) {
            listOfBookings = bookingRepository.findByOwnerCurrent(user, LocalDateTime.now());
        } else if (state.equalsIgnoreCase("past")) {
            listOfBookings = bookingRepository.findByOwnerPast(user, LocalDateTime.now());
        } else if (state.equalsIgnoreCase("future")) {
            listOfBookings = bookingRepository.findByOwnerFuture(user, LocalDateTime.now());
        } else if (state.equalsIgnoreCase("waiting")) {
            listOfBookings = bookingRepository.findBookingsByOwnerAndStatus(user, WAITING);
        } else if (state.equalsIgnoreCase("rejected")) {
            listOfBookings = bookingRepository.findBookingsByOwnerAndStatus(user, REJECTED);
        } else {
            throw new PersonalValidationException("Unknown state: " + state);
        }
        return listOfBookings.stream()
                .skip(from)
                .limit(size)
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Booking validBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + bookingId));
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