package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoToGetJsonTest {

    @Autowired
    private JacksonTester<ItemDtoToGet> json;

    @Test
    public void testSerialize() throws IOException {
        ItemDtoToGet itemDtoFull = new ItemDtoToGet();
        itemDtoFull.setId(1L);
        itemDtoFull.setName("Sample Item");
        itemDtoFull.setDescription("Description of the item");
        itemDtoFull.setAvailable(true);

        BookingDto lastBooking = new BookingDto();
        lastBooking.setId(101L);
        lastBooking.setStart(LocalDateTime.of(2023, 10, 26, 15, 30, 0));
        lastBooking.setEnd(LocalDateTime.of(2023, 10, 26, 16, 30, 0));

        BookingDto nextBooking = new BookingDto();
        nextBooking.setId(102L);
        nextBooking.setStart(LocalDateTime.of(2023, 10, 27, 14, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2023, 10, 27, 15, 0, 0));

        List<CommentDto> comments = new ArrayList<>();
        CommentDto comment1 = new CommentDto();
        comment1.setId(201L);
        comment1.setText("This is a comment.");
        CommentDto comment2 = new CommentDto();
        comment2.setId(202L);
        comment2.setText("Another comment.");

        comments.add(comment1);
        comments.add(comment2);

        itemDtoFull.setLastBooking(lastBooking);
        itemDtoFull.setNextBooking(nextBooking);
        itemDtoFull.setComments(comments);

        String expectedJson = "{\"id\":1,\"name\":\"Sample Item\"," +
                "\"description\":\"Description of the item\",\"available\":true," +
                "\"lastBooking\":{\"id\":101,\"start\":\"2023-10-26T15:30:00\",\"end\":\"2023-10-26T16:30:00\"}," +
                "\"nextBooking\":{\"id\":102,\"start\":\"2023-10-27T14:00:00\",\"end\":\"2023-10-27T15:00:00\"}," +
                "\"comments\":[{\"id\":201,\"text\":\"This is a comment.\"}," +
                "{\"id\":202,\"text\":\"Another comment.\"}]}";

        assertThat(json.write(itemDtoFull)).isEqualToJson(expectedJson);
    }

    @Test
    public void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"name\":\"Sample Item\"," +
                "\"description\":\"Description of the item\",\"available\":true," +
                "\"lastBooking\":{\"id\":101,\"start\":\"2023-10-26T15:30:00\",\"end\":\"2023-10-26T16:30:00\"}," +
                "\"nextBooking\":{\"id\":102,\"start\":\"2023-10-27T14:00:00\",\"end\":\"2023-10-27T15:00:00\"}," +
                "\"comments\":[{\"id\":201,\"text\":\"This is a comment.\"}," +
                "{\"id\":202,\"text\":\"Another comment.\"}]}";
        ItemDtoToGet itemDtoFull = json.parse(content).getObject();

        assertThat(itemDtoFull.getId()).isEqualTo(1L);
        assertThat(itemDtoFull.getName()).isEqualTo("Sample Item");
        assertThat(itemDtoFull.getDescription()).isEqualTo("Description of the item");
        assertThat(itemDtoFull.getAvailable()).isTrue();

        assertThat(itemDtoFull.getLastBooking().getId()).isEqualTo(101L);
        assertThat(itemDtoFull.getLastBooking().getStart()).isEqualTo("2023-10-26T15:30:00");
        assertThat(itemDtoFull.getLastBooking().getEnd()).isEqualTo("2023-10-26T16:30:00");

        assertThat(itemDtoFull.getNextBooking().getId()).isEqualTo(102L);
        assertThat(itemDtoFull.getNextBooking().getStart()).isEqualTo("2023-10-27T14:00:00");
        assertThat(itemDtoFull.getNextBooking().getEnd()).isEqualTo("2023-10-27T15:00:00");

        assertThat(itemDtoFull.getComments().size()).isEqualTo(2);
        assertThat(itemDtoFull.getComments().get(0).getId()).isEqualTo(201L);
        assertThat(itemDtoFull.getComments().get(0).getText()).isEqualTo("This is a comment.");
        assertThat(itemDtoFull.getComments().get(1).getId()).isEqualTo(202L);
        assertThat(itemDtoFull.getComments().get(1).getText()).isEqualTo("Another comment.");
    }
}