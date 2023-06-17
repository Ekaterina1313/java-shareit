package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
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
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!userDao.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Имя вещи не должно быть пустым.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не должно быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле 'Available' не должно быть пустым. ");
        }
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userDao.getUserById(userId));
        Item createdItem = itemDao.createItem(item);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!userDao.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        return itemDao.getAllItems().values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!itemDao.isContainItem(id)) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        return ItemMapper.toItemDto(itemDao.getItemById(id));
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!userDao.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!itemDao.isContainItem(itemId)) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        if (!Objects.equals(itemDao.getItemById(itemId).getOwner().getId(), userId)) {
            throw new UserIsNotOwnerException("Пользователь не является владельцем вещи.");
        }
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Имя вещи не должно быть пустым.");
            }
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не должно быть пустым.");
            }
        }
        itemDto.setId(itemId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userDao.getUserById(userId));
        return ItemMapper.toItemDto(itemDao.updateItem(item));
    }


    @Override
    public void deleteItem (Long id, Long userId){
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!Objects.equals(itemDao.getItemById(id).getOwner().getId(), userId)) {
            throw new UserIsNotOwnerException("Пользователь не является владельцем вещи.");
        }
        if (!userDao.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (!itemDao.isContainItem(id)) {
            throw new EntityNotFoundException("Вещь под указанным id не найдена.");
        }
        itemDao.deleteItem(id);
    }

    @Override
    public List<ItemDto> searchForItem(String searchText, Long userId) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя в заголовке запроса.");
        }
        if (!userDao.isContainUser(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id = " + userId + " не найден.");
        }
        if (searchText == null || searchText.isBlank()) {
            return new ArrayList<>();
        }
        String toLowerCaseText = searchText.toLowerCase();
        List<ItemDto> itemDtos = new ArrayList<>();
        itemDao.getAllItems().values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(toLowerCaseText) ||
                        item.getDescription().toLowerCase().contains(toLowerCaseText)))
                .map(ItemMapper::toItemDto)
                .forEach(itemDtos::add);
        return itemDtos;
    }
}