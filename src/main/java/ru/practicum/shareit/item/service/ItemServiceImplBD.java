package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.REJECTED;

@Service
@Slf4j
public class ItemServiceImplBD implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImplBD(ItemRepository itemRepository, UserRepository userRepository,
                             BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User userById = validUser(userId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userById);
        Item createdItem = itemRepository.save(item);
        log.info("'{}' успешно добавлена пользователем с id = {}.", itemDto.getName(), userId);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public List<ItemDtoToGet> getAll(Long userId) {
        validUser(userId);
        log.info("Получен список вещей пользователя с id = {}.", userId);
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<Booking> allBooKings = bookingRepository.findAllByItemIdIn(userItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        List<ItemDtoToGet> itemDtos = new ArrayList<>();
        List<Comment> allComments = commentRepository.findAll();

        for (Item item : userItems) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> itemBookings = allBooKings.stream()
                    .filter(booking -> booking.getItem().getId()
                            .equals(item.getId())).collect(Collectors.toList());
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Booking booking : itemBookings) {
                if (!Objects.equals(booking.getStatus(), REJECTED)) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        if ((lastBooking == null) || (booking.getEnd().isAfter(lastBooking.getEnd()))) {
                            lastBooking = booking;
                        }
                    }
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        if ((nextBooking == null) || (booking.getStart().isBefore(nextBooking.getStart()))) {
                            nextBooking = booking;
                        }
                    }
                }
            }

            for (Comment comment : allComments) {
                if (Objects.equals(comment.getItem().getId(), item.getId())) {
                    commentDtos.add(CommentMapper.toCommentDto(comment));
                }
            }

            ItemDtoToGet itemDtoToGet = ItemMapper.toItemDtoToGet(item, BookingMapper.toBookingDto(lastBooking),
                    BookingMapper.toBookingDto(nextBooking), commentDtos);
            itemDtos.add(itemDtoToGet);
        }
        return itemDtos.stream()
                .sorted(Comparator
                        .comparing(ItemDtoToGet::getName)
                        .reversed()).collect(Collectors.toList());
    }

    @Override
    public ItemDtoToGet getById(Long id, Long userId) {
        validUser(userId);
        Item itemById = validItem(id);
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (Objects.equals(itemById.getOwner().getId(), userId)) {
            List<Booking> listOfBookings = bookingRepository.findAllByItemId(id);
            if (!listOfBookings.isEmpty()) {
                for (Booking booking : listOfBookings) {

                    if (!Objects.equals(booking.getStatus(), REJECTED)) {
                        if ((booking.getEnd().isBefore(LocalDateTime.now())) ||
                                (booking.getStart().isBefore(LocalDateTime.now()))) {
                            if (lastBooking == null || (booking.getEnd().isAfter(lastBooking.getEnd()))) {
                                lastBooking = booking;
                            }
                        }
                        if (booking.getStart().isAfter(LocalDateTime.now())) {
                            if (nextBooking == null || (booking.getStart().isBefore(nextBooking.getStart()))) {
                                nextBooking = booking;
                            }
                        }
                    }
                }
            }
        }

        List<Comment> comments = commentRepository.findAllByItemId(id);
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemDtoToGet(itemById, BookingMapper.toBookingDto(lastBooking),
                BookingMapper.toBookingDto(nextBooking), commentsDto);
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        User userById = validUser(userId);
        Item itemById = validItem(itemId);
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
        validUser(userId);
        Item itemById = validItem(id);
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new PersonalValidationException("Пользователь не является владельцем вещи.");
        }
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String searchText, Long userId) {
        validUser(userId);
        log.info("Составлен список вещей, найденных по ключевым словам '{}'.", searchText);
        return itemRepository.searchItems(searchText.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId) {
        User author = validUser(authorId);
        Item itemById = validItem(itemId);
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndItemId(authorId, itemId);
        if (bookings.isEmpty()) {
            throw new PersonalValidationException("Пользователь не брал вещь в аренду.");
        }
        Status elementStatus = null;
        LocalDateTime endTime = null;
        for (Booking element : bookings) {
            if (!Objects.equals(element.getStatus(), (REJECTED))) {
                elementStatus = element.getStatus();
            }
            if (element.getEnd().isBefore(LocalDateTime.now())) {
                endTime = element.getEnd();
            }
        }
        if (endTime == null) {
            throw new PersonalValidationException("Отзыв можно оставить только после завершения брони.");
        }
        if (elementStatus == null) {
            throw new PersonalValidationException("Нельзя оставить отзыв со статусом брони 'REJECTED'.");
        }
        Comment createdComment = CommentMapper.fromCommentDto(commentDto, itemById, author);
        createdComment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(createdComment));
    }

    private User validUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
    }

    private Item validItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + itemId));
    }
}