package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestDao itemRequestDao;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestDao itemRequestDao) {
        this.itemRequestDao = itemRequestDao;
    }

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        return itemRequestDao.createItemRequest(itemRequest);
    }

    @Override
   public List<ItemRequest> getAllItemRequests() {
        return itemRequestDao.getAllItemRequests();
    }

    @Override
    public ItemRequest getItemRequestById(long id) {
        return itemRequestDao.getItemRequestById(id);
    }

    @Override
    public ItemRequest updateItemRequest(long id) {
        return itemRequestDao.updateItemRequest(id);
    }

    @Override
    public void deleteItemRequest(long  id) {
        itemRequestDao.deleteItemRequest(id);
    }
}
