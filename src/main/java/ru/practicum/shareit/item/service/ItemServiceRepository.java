package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceRepository implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceRepository(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
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
    public List<ItemDto> getAll(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        log.info("Получен список вещей пользователя с id = {}.", userId);
        return itemRepository.findByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id: " + userId));
        Item itemById = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь с id: " + userId));
        return ItemMapper.toItemDto(itemById);
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
