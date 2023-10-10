package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestDao itemRequestDao;
    private final UserDao userDao;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestDao itemRequestDao, UserDao userDao) {
        this.itemRequestDao = itemRequestDao;
        this.userDao = userDao;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User userById = userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userById);
        return ItemRequestMapper.toItemRequestDto(itemRequestDao.create(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        return itemRequestDao.getAll(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long id, Long userId) {
        userDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        return ItemRequestMapper.toItemRequestDto(itemRequestDao.getById(id).get());
    }

    @Override
    public ItemRequestDto update(Long id, Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest2 = itemRequestDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден запрос с id: " + id));
        User userById = userDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + id));
        if (!Objects.equals(itemRequest2.getRequestor().getId(), userId)) {
            throw new PersonalValidationException("Нельзя изменить чужой запрос.");
        }
        itemRequestDto.setId(id);
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setRequestor(userById);
        return ItemRequestMapper.toItemRequestDto(itemRequestDao.update(itemRequest));
    }

    @Override
    public void delete(Long id, Long userId) {
        userDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        ItemRequest itemRequest2 = itemRequestDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден запрос с id: " + id));
        if (!Objects.equals(itemRequest2.getRequestor().getId(), userId)) {
            throw new PersonalValidationException("Пользователь с id = " + userId + " не может удалить чужой запрос.");
        }
        itemRequestDao.delete(id);
    }
}