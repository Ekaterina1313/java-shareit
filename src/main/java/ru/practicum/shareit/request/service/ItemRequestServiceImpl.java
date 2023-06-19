package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userDao.getById(userId).get());
        return ItemRequestMapper.toItemRequestDto(itemRequestDao.create(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        return itemRequestDao.getAll(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemRequestDto> getById(Long id) {
        return Optional.of(ItemRequestMapper.toItemRequestDto(itemRequestDao.getById(id).get()));
    }

    @Override
    public ItemRequestDto update(Long id, Long userId, ItemRequestDto itemRequestDto) {
        if (!Objects.equals(itemRequestDao.getById(id).get().getRequestor().getId(), userId)) {
            throw new BadRequestException("Нельзя изменить чужой запрос.");
        }
        itemRequestDto.setId(id);
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setRequestor(userDao.getById(userId).get());
        return ItemRequestMapper.toItemRequestDto(itemRequestDao.update(itemRequest));
    }

    @Override
    public void delete(Long id, Long userId) {
        if (!Objects.equals(itemRequestDao.getById(id).get().getRequestor().getId(), userId)) {
            throw new BadRequestException("Пользователь с id = " + userId + " не может удалить чужой запрос.");
        }
        itemRequestDao.delete(id);
    }

    @Override
    public boolean isContainItemRequest(Long id) {
        return itemRequestDao.isContainItemRequest(id);
    }

    @Override
    public boolean isContainUser(Long id) {
        return userDao.isContainUser(id);
    }
}