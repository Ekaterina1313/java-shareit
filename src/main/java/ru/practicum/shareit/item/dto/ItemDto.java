package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
}
