package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public ItemRequest createItemRequest(@RequestBody ItemRequest itemRequest) {
        return itemRequestService.createItemRequest(itemRequest);
    }

    @PostMapping
    public List<ItemRequest> getAllItemRequests() {
        return itemRequestService.getAllItemRequests();
    }

    @GetMapping("/{id}")
    public ItemRequest getItemRequestById(@PathVariable long id) {
        return itemRequestService.getItemRequestById(id);
    }

    @PatchMapping
    public ItemRequest updateItemRequest(@RequestBody long id) {
        return itemRequestService.updateItemRequest(id);
    }

    @DeleteMapping("/{id}")
    public void deleteItemRequest(@PathVariable long  id) {
        itemRequestService.deleteItemRequest(id);
    }
}
