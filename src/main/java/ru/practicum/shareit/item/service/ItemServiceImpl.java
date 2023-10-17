package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User userById = userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userById);
        Item createdItem = itemDao.create(item);
        log.info("'{}' успешно добавлена пользователем с id = {}.", itemDto.getName(), userId);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public List<ItemDtoToGet> getAll(Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        log.info("Получен список вещей пользователя с id = {}.", userId);
        return null;
    }

    @Override
    public ItemDtoToGet getById(Long id, Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        return null;
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        User userById = userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemDao.getById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь не является владельцем вещи.");
        }
        itemDto.setId(itemId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userById);
        log.info("Информация о вещи '{}' успешно обновлена.", itemDto.getName());
        return ItemMapper.toItemDto(itemDao.update(item));
    }


    @Override
    public void delete(Long id, Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new PersonalValidationException("Пользователь не является владельцем вещи.");
        }
        itemDao.delete(id);
    }

    @Override
    public List<ItemDto> search(String searchText, Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        log.info("Составлен список вещей, найденных по ключевым словам '{}'.", searchText);
        return itemDao.search(searchText, userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long authorId) {
        return null;
    }

    @Override
    public Item validItem(Long itemId) {
        return null;
    }
}