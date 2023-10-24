package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImplBd implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImplBd(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userById);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        return itemRequestRepository.findAll().stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long id, Long userId) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        ;
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto update(Long id, Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest2 = itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден запрос с id: " + id));
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + id));
        if (!Objects.equals(itemRequest2.getRequestor().getId(), userId)) {
            throw new PersonalValidationException("Нельзя изменить чужой запрос.");
        }
        if (!itemRequestDto.getDescription().isBlank() || itemRequestDto.getDescription() != null) {
            itemRequest2.setDescription(itemRequestDto.getDescription());
        }
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest2));
    }

    @Override
    public void delete(Long id, Long userId) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        ItemRequest itemRequest2 = itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден запрос с id: " + id));
        if (!Objects.equals(itemRequest2.getRequestor().getId(), userId)) {
            throw new PersonalValidationException("Пользователь с id = " + userId + " не может удалить чужой запрос.");
        }
        itemRequestRepository.deleteById(id);
    }
}