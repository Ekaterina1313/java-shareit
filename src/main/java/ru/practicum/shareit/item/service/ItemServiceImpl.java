package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userDao.getById(userId).get());
        Item createdItem = itemDao.create(item);
        log.info("'{}' успешно добавлена пользователем с id = {}.", itemDto.getName(), userId);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        log.info("Получен список вещей пользователя с id = {}.", userId);
        return itemDao.getAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> getById(Long id, Long userId) {
        return Optional.of(ItemMapper.toItemDto(itemDao.getById(id).get()));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        if (!Objects.equals(itemDao.getById(itemId).get().getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь не является владельцем вещи.");
        }
        itemDto.setId(itemId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userDao.getById(userId).get());
        log.info("Информация о вещи '{}' успешно обновлена.", itemDto.getName());
        return ItemMapper.toItemDto(itemDao.update(item));
    }


    @Override
    public void delete(Long id, Long userId) {
        if (!Objects.equals(itemDao.getById(id).get().getOwner().getId(), userId)) {
            throw new BadRequestException("Пользователь не является владельцем вещи.");
        }
        itemDao.delete(id);
    }

    @Override
    public List<ItemDto> search(String searchText, Long userId) {
        log.info("Составлен список вещей, найденных по ключевым словам '{}'.", searchText);
        return itemDao.search(searchText, userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
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