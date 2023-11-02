package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void testSerialize() throws IOException {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Sample Item");
        itemDto.setDescription("Description of the item");
        itemDto.setAvailable(true);
        itemDto.setRequestId(101L);

        String expectedJson = "{\"id\":1,\"name\":\"Sample Item\"," +
                "\"description\":\"Description of the item\",\"available\":true,\"requestId\":101}";

        assertThat(json.write(itemDto)).isEqualToJson(expectedJson);
    }

    @Test
    public void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"name\":\"Sample Item\"," +
                "\"description\":\"Description of the item\",\"available\":true,\"requestId\":101}";
        ItemDto itemDto = json.parse(content).getObject();

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Sample Item");
        assertThat(itemDto.getDescription()).isEqualTo("Description of the item");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(101L);
    }
}