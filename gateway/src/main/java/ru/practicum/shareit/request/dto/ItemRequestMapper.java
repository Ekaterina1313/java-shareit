package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDtoFull toItemRequestDtoFull(ItemRequest itemRequest, List<ItemDto> responses) {
        return new ItemRequestDtoFull(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                responses
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

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}