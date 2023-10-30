package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    @Qualifier("bookingServiceImplBd")
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;
    BookingDto bookingDto;
    LocalDateTime start = LocalDateTime.of(2024, 8, 26, 15,30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 26, 15,30);
    Long userId = 1L;

    @BeforeEach
    void setup() {
        bookingDto = new BookingDto(1L, start, end, new User(), 1L, new Item(), Status.WAITING, 1L);
    }

    @Test
    public void testCreateBookingSuccess() throws Exception {

        when(bookingService.create(bookingDto, userId)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"start\": \"2024-08-26T15:30\", " +
                                "\"end\": \"2024-09-26T15:30\", " +
                                "\"booker\": {}," +
                                "\"itemId\": 1,  " +
                                "\"item\": {}, " +
                                "\"status\": \"WAITING\", " +
                                "\"bookerId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).create(bookingDto, userId);
    }

    @Test
    public void testCreateBookingWithoutUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"start\": \"2024-08-26T15:30\", " +
                                "\"end\": \"2024-09-26T15:30\", " +
                                "\"booker\": {}," +
                                "\"itemId\": 1,  " +
                                "\"item\": {}, " +
                                "\"status\": \"WAITING\", " +
                                "\"bookerId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateBookingWithEndBeforeStart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"start\": \"2024-08-26T15:30\", " +
                                "\"end\": \"2024-07-26T15:30\", " +
                                "\"booker\": {}," +
                                "\"itemId\": 1,  " +
                                "\"item\": {}, " +
                                "\"status\": \"WAITING\", " +
                                "\"bookerId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateBookingWithEmptyStartAndEnd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"start\": \"\", " +
                                "\"end\": \"\", " +
                                "\"booker\": {}," +
                                "\"itemId\": 1,  " +
                                "\"item\": {}, " +
                                "\"status\": \"WAITING\", " +
                                "\"bookerId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateBookingWithStartEqualsEnd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"start\": \"2024-08-26T15:30\", " +
                                "\"end\": \"2024-08-26T15:30\", " +
                                "\"booker\": {}," +
                                "\"itemId\": 1,  " +
                                "\"item\": {}, " +
                                "\"status\": \"WAITING\", " +
                                "\"bookerId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateBookingWithStartInPast() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"start\": \"2023-08-26T15:30\", " +
                                "\"end\": \"2024-09-26T15:30\", " +
                                "\"booker\": {}," +
                                "\"itemId\": 1,  " +
                                "\"item\": {}, " +
                                "\"status\": \"WAITING\", " +
                                "\"bookerId\": 1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testStatusConfirmApprovedTrue() throws Exception {
        when(bookingService.statusConfirm(1L, userId, true)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1)).statusConfirm(1L, userId, true);
    }

    @Test
    public void testStatusConfirmApprovedFalse() throws Exception {
        when(bookingService.statusConfirm(1L, userId, false)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/bookings/1")
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1)).statusConfirm(1L, userId, false);
    }

    @Test
    public void testStatusConfirmWithoutUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/bookings/1")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testStatusConfirmInvalidApprovedValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/bookings/1")
                        .param("approved", "invalid_value")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetByIdWithValidUserId() throws Exception {
        when(bookingService.getById(1L, userId)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings/1")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1)).getById(1L, userId);
    }

    @Test
    public void testGetByIdWithoutHeaders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBookingsByBookerWithValidUserId() throws Exception {
        when(bookingService.getBookingsByBookerId("ALL", 0, 10, userId))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(bookingDto.getId()));

        verify(bookingService, times(1)).getBookingsByBookerId("ALL", 0, 10, userId);
    }

    @Test
    public void testGetUserBookingsWithoutUserIdHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserBookingsWithInvalidPagination() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "-1")
                        .param("size", "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBookingsByOwnerSuccess() throws Exception {
        List<BookingDto> expectedBookings = new ArrayList<>();
        expectedBookings.add(bookingDto);

        when(bookingService.getBookingsByOwnerId("ALL", 0, 10, userId)).thenReturn(expectedBookings);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedBookings.get(0).getId()));
    }

    @Test
    public void testGetBookingsByOwnerWithoutHeaders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBookingsByOwnerWithInvalidPaginationParameters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", "-1")
                        .param("size", "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}