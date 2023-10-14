package ru.practicum.shareit.request.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRequestDaoImpl implements ItemRequestDao {
    Map<Long, ItemRequest> mapOfItemRequests = new HashMap<>();
    private static Long requestId = 1L;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        itemRequest.setId(requestId);
        requestId++;
        mapOfItemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public List<ItemRequest> getAll(Long userId) {
        return mapOfItemRequests.values().stream()
                .filter(itemRequest -> itemRequest.getRequestor().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemRequest> getById(Long id) {
        return Optional.ofNullable(mapOfItemRequests.get(id));
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        ItemRequest itemRequest2 = mapOfItemRequests.get(itemRequest.getId());
        if (itemRequest.getDescription() != null) {
            itemRequest2.setDescription(itemRequest.getDescription());
        }
        mapOfItemRequests.put(itemRequest2.getId(), itemRequest2);
        return itemRequest2;
    }

    @Override
    public void delete(Long id) {
        mapOfItemRequests.remove(id);
    }
}