package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                new User(),
                itemRequestDto.getCreated()
        );
    }
}