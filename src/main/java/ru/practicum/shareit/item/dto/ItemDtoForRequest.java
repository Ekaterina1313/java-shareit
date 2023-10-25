package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDtoForRequest {
    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;

}
