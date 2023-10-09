package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
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
        bookingRepository.findById(bookingDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена бронь с id: " + userId));
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!itemById.getAvailable()) {
            throw new BadRequestException("Выбранная вещь недоступна для бронирования.");
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
    public List<BookingDto> getAll(Long userId) {
        return null;
    }

    @Override
    public BookingDto getById(Long id, Long userId) {
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
