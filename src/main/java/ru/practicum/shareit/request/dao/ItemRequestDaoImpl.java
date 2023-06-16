package ru.practicum.shareit.request.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ItemRequestDaoImpl implements ItemRequestDao {
    Map<Long, ItemRequest> mapOfItemRequests = new HashMap<>();

    @Override
    public ItemRequest createItemRequest (ItemRequest itemRequest) {
        return itemRequest;
    }

    @Override
    public List<ItemRequest> getAllItemRequests() {
        return new ArrayList<>(mapOfItemRequests.values());
    }

    @Override
    public ItemRequest getItemRequestById(long id) {
        return mapOfItemRequests.get(id);
    }

    @Override
    public ItemRequest updateItemRequest(long id) {
        return mapOfItemRequests.get(id);
    }

    @Override
    public void deleteItemRequest(long id) {

    }
}
