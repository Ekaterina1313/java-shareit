package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceRepository implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceRepository(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userById);
        Item createdItem = itemRepository.save(item);
        log.info("'{}' успешно добавлена пользователем с id = {}.", itemDto.getName(), userId);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public List<ItemDtoToGet> getAll(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        log.info("Получен список вещей пользователя с id = {}.", userId);
       List<Item> userItems = itemRepository.findByOwnerId(userId);
       List<Booking> allBooKings = bookingRepository.findAllByItemIdIn(userItems.stream().map(Item :: getId).collect(Collectors.toList()));
        List<ItemDtoToGet> itemDtos = new ArrayList<>();

        for (Item item : userItems) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> itemBookings = allBooKings.stream().filter(booking -> booking.getItem().getId()
                    .equals(item.getId())).collect(Collectors.toList());
            for (Booking booking : itemBookings) {
                if (booking.getEnd().isBefore(LocalDateTime.now())) {
                    if ((lastBooking ==null) || (booking.getEnd().isAfter(lastBooking.getEnd()))) {
                        lastBooking = booking;
                    }
                }
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    if ((nextBooking == null) || (booking.getStart().isBefore(nextBooking.getStart()))) {
                        nextBooking = booking;
                    }
                }
            }
            ItemDtoToGet itemDtoToGet = ItemMapper.toItemDtoToGet(item, BookingMapper.toBookingDto(lastBooking),
                    BookingMapper.toBookingDto(nextBooking));
            itemDtos.add(itemDtoToGet);
        }
        return itemDtos.stream().sorted(Comparator.comparing(ItemDtoToGet::getName).reversed()).collect(Collectors.toList());
    }

    @Override
    public ItemDtoToGet getById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (Objects.equals(itemById.getOwner().getId(), userId)) {
            List<Booking> listOfBookings = bookingRepository.findAllByItemId(id);
            if (!listOfBookings.isEmpty()) {
                for (Booking booking : listOfBookings) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        if ((Objects.equals(null, lastBooking)) || (booking.getEnd().isAfter(lastBooking.getEnd()))) {
                            lastBooking = booking;
                        }
                    }
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        if ((Objects.equals(null, nextBooking)) || (booking.getStart().isBefore(nextBooking.getStart()))) {
                            nextBooking = booking;
                        }
                    }
                }
            }
        }

        return ItemMapper.toItemDtoToGet(itemById, BookingMapper.toBookingDto(lastBooking), BookingMapper.toBookingDto(nextBooking));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь не является владельцем вещи.");
        }
        if (itemDto.getAvailable() != null) {
            itemById.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            itemById.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            itemById.setName(itemDto.getName());
        }
        itemById.setOwner(userById);
        Item updatedItem = itemRepository.save(itemById);
        log.info("Информация о вещи '{}' успешно обновлена.", itemDto.getName());
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new PersonalValidationException("Пользователь не является владельцем вещи.");
        }
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String searchText, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        log.info("Составлен список вещей, найденных по ключевым словам '{}'.", searchText);
        return itemRepository.searchItems(searchText.toLowerCase())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

}
