package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    public void testSerialize() throws IOException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2022, 10, 26, 15, 30));
        bookingDto.setEnd(LocalDateTime.of(2022, 10, 26, 16, 30));
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setItemId(101L);

        User booker = new User();
        booker.setId(201L);
        booker.setName("John Doe");
        booker.setEmail("john.doe@example.com");
        bookingDto.setBooker(booker);

        Item item = new Item();
        item.setId(101L);
        item.setName("Sample Item");
        item.setDescription("Description of the item");
        bookingDto.setItem(item);

        bookingDto.setBookerId(201L);

        String expectedJson = "{\"id\":1,\"start\":\"2022-10-26T15:30:00\"," +
                "\"end\":\"2022-10-26T16:30:00\",\"status\":\"APPROVED\",\"itemId\":101," +
                "\"booker\":{\"id\":201,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "\"item\":{\"id\":101,\"name\":\"Sample Item\"," +
                "\"description\":\"Description of the item\"},\"bookerId\":201}";

        assertThat(json.write(bookingDto)).isEqualToJson(expectedJson);
    }

    @Test
    public void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"start\":\"2022-10-26T15:30:00\"," +
                "\"end\":\"2022-10-26T16:30:00\",\"status\":\"APPROVED\",\"itemId\":101," +
                "\"booker\":{\"id\":201,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "\"item\":{\"id\":101,\"name\":\"Sample Item\"," +
                "\"description\":\"Description of the item\"},\"bookerId\":201}";
        BookingDto bookingDto = json.parse(content).getObject();

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime
                .of(2022, 10, 26, 15, 30));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime
                .of(2022, 10, 26, 16, 30));
        assertThat(bookingDto.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(bookingDto.getItemId()).isEqualTo(101L);

        User booker = bookingDto.getBooker();
        assertThat(booker.getId()).isEqualTo(201L);
        assertThat(booker.getName()).isEqualTo("John Doe");
        assertThat(booker.getEmail()).isEqualTo("john.doe@example.com");

        Item item = bookingDto.getItem();
        assertThat(item.getId()).isEqualTo(101L);
        assertThat(item.getName()).isEqualTo("Sample Item");
        assertThat(item.getDescription()).isEqualTo("Description of the item");

        assertThat(bookingDto.getBookerId()).isEqualTo(201L);
    }
}