package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    @Qualifier("itemRequestServiceImplBd")
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoFull createdRequest;
    LocalDateTime created;
    Long userId = 1L;

    @BeforeEach
    void setup() {
        created = LocalDateTime.now();
        itemRequestDto = new ItemRequestDto(userId, "Request description", created);
        createdRequest = new ItemRequestDtoFull(userId, "Request description", created, new ArrayList<>());
    }

    @Test
    public void testCreateRequestSuccess() throws Exception {
        Mockito.when(itemRequestService.create(itemRequestDto, 1L)).thenReturn(createdRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", itemRequestDto.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"description\": \"Request description\", \"created\": \"" + created + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Request description"));

        verify(itemRequestService, times(1)).create(itemRequestDto, 1L);
    }

    @Test
    public void testCreateRequestMissingDescription() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Поле с описанием не должно быть пустым."));

        verify(itemRequestService, never()).create(any(), any());
    }

    @Test
    public void testGetAllRequestsByOwner() throws Exception {
        Long userId = 1L;
        List<ItemRequestDtoFull> expectedResponse = Arrays.asList(
                new ItemRequestDtoFull(),
                new ItemRequestDtoFull()
        );

        Mockito.when(itemRequestService.getAllByOwner(userId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResponse.size()));
    }

    @Test
    public void testGetAllByOwnerWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllRequestsByOthers() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        List<ItemRequestDtoFull> expectedResponse = Arrays.asList(
                new ItemRequestDtoFull(),
                new ItemRequestDtoFull()
        );

        Mockito.when(itemRequestService.getAllByOthers(userId, from, size))
                .thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResponse.size()));
    }

    @Test
    public void testGetOthersRequestsWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/all?from=0&size=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOthersRequestsWithInvalidFrom() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/all?from=-1&size=10")
                        .header("X-Sharer-User-Id", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOthersRequestsWithInvalidSize() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/all?from=0&size=0")
                        .header("X-Sharer-User-Id", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testGetByIdSuccess() throws Exception {
        Long userId = 1L;
        List<ItemDto> items = Arrays.asList(new ItemDto(), new ItemDto());
        createdRequest.setItems(items);

        Mockito.when(itemRequestService.getById(itemRequestDto.getId(), userId)).thenReturn(createdRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items.size()").value(2));
    }

    @Test
    public void testGetByIdWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/" + itemRequestDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRequestSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/requests/" + itemRequestDto.getId())
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRequestWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/requests/" + itemRequestDto.getId()))
                .andExpect(status().isBadRequest());
    }
}