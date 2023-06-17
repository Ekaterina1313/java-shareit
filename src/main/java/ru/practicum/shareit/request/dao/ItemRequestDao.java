package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestDao {
    ItemRequest createItemRequest(ItemRequest itemRequest);

    List<ItemRequest> getAllItemRequests();

    ItemRequest getItemRequestById(long id);

    ItemRequest updateItemRequest(long id);

    void deleteItemRequest(long id);
}