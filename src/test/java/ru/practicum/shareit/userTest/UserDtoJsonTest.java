package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "john@mail.com");
        String json = objectMapper.writeValueAsString(userDto);
        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@mail.com\"}");
    }

    @Test
    public void testDeserializeUserDto() throws Exception {
        String json = "{\"id\":2,\"name\":\"Alice Smith\",\"email\":\"alice@mail.com\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);
        assertThat(userDto.getId()).isEqualTo(2L);
        assertThat(userDto.getName()).isEqualTo("Alice Smith");
        assertThat(userDto.getEmail()).isEqualTo("alice@mail.com");
    }
}