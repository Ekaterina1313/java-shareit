package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @MockBean
    @Qualifier("itemServiceImplDb")
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    Long userId = 1L;
    ItemDto itemDto;
    LocalDateTime created = LocalDateTime.of(2023, 10, 26, 15, 30);

    CommentDto commentDto;

    @BeforeEach
    void setup() {
        itemDto = new ItemDto(1L, "ItemDtoName", "ItemDtoDesc", true, null);
        commentDto = new CommentDto(1L, "This is a comment.", "AuthorName", created);
    }

    @Test
    public void testCreateItemSuccess() throws Exception {
        when(itemService.create(itemDto, userId)).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"ItemDtoName\", \"description\": \"ItemDtoDesc\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ItemDtoName"));

        verify(itemService, times(1)).create(itemDto, userId);
    }

    @Test
    public void testCreateItemWithoutUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"ItemDtoName\", \"description\": \"ItemDtoDesc\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateItemWithEmptyName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"\", \"description\": \"ItemDtoDesc\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateItemWithEmptyDescription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"ItemDtoName\", \"description\": \"\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateItemWithMissingAvailableField() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Item Name\", \"description\": \"Item Description\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllItemsSuccess() throws Exception {
        Long userId = 1L;
        when(itemService.getAll(userId, 0, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).getAll(userId, 0, 10);
    }

    @Test
    public void testGetAllItemsWithInvalidUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    public void testGetAllItemsWithInvalidPagination() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "-1")
                        .param("size", "-5"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    public void testUpdateItemSuccess() throws Exception {
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(false);
        when(itemService.update(itemDto.getId(), userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + itemDto.getId() +
                                ", \"name\": \"Updated Name\", \"description\": \"Updated Description\", " +
                                "\"available\": false, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemService, times(1)).update(itemDto.getId(), userId, itemDto);
    }

    @Test
    public void testUpdateItemWithoutUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + itemDto.getId() +
                                ", \"name\": \"Updated Name\", \"description\": \"Updated Description\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    public void testUpdateItemWithEmptyName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + itemDto.getId() +
                                ", \"name\": \"\", \"description\": \"Updated Description\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    public void testUpdateItemWithEmptyDescription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + itemDto.getId() +
                                ", \"name\": \"Updated Name\", \"description\": \"\", " +
                                "\"available\": true, \"requestId\": null}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    public void testDeleteItemSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(itemDto.getId(), userId);
    }

    @Test
    public void testDeleteItemWithoutUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/items/{itemId}", itemDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).delete(anyLong(), anyLong());
    }

    @Test
    public void testSearchItemsSuccess() throws Exception {
        String searchText = "example";
        int from = 0;
        int size = 10;

        List<ItemDto> searchResults = new ArrayList<>();

        when(itemService.search(searchText, from, size, userId)).thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", searchText)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).search(searchText, from, size, userId);
    }

    @Test
    public void testSearchItemsWithoutUserIdHeader() throws Exception {
        String searchText = "example";
        int from = 0;
        int size = 10;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items/search")
                        .param("text", searchText)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).search(any(), anyInt(), anyInt(), anyLong());
    }

    @Test
    public void testSearchItemsWithEmptySearchText() throws Exception {
        String searchText = "";
        int from = 0;
        int size = 10;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", searchText)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, never()).search(any(), anyInt(), anyInt(), anyLong());
    }

    @Test
    public void testSearchItemsWithBadPagination() throws Exception {
        String searchText = "example";
        int from = -1;
        int size = -1;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items/search")
                        .param("text", searchText)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).search(any(), anyInt(), anyInt(), anyLong());
    }

    @Test
    public void testCreateCommentSuccess() throws Exception {
        Long itemId = 2L;

        when(itemService.createComment(commentDto, itemId, userId)).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + commentDto.getId() + ", \"text\": \"This is a comment.\"," +
                                " \"authorName\":  \"AuthorName\", \"created\": \"2023-10-26T15:30\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("This is a comment."));

        verify(itemService, times(1)).createComment(commentDto, itemId, userId);
    }


    @Test
    public void testCreateCommentWithEmptyText() throws Exception {
        Long itemId = 2L;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + commentDto.getId() + ", \"text\": \"\"," +
                                " \"authorName\":  \"AuthorName\", \"created\": \"2023-10-26T15:30\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createComment(any(CommentDto.class), anyLong(), anyLong());
    }

    @Test
    public void testCreateCommentWithoutHeaders() throws Exception {
        Long itemId = 2L;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items/" + itemId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + commentDto.getId() + ", \"text\": \"This is a comment.\"," +
                                " \"authorName\":  \"AuthorName\", \"created\": \"2023-10-26T15:30\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}