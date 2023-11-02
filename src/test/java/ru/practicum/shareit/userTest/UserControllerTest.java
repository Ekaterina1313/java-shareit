package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    @Qualifier("userServiceImplBd")
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setName("John");
        newUser.setEmail("john@mail.com");

        when(userService.create(newUser)).thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@mail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUserWithEmptyName() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setName("");
        newUser.setEmail("john@mail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"john@mail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserWithEmptyEmail() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setName("John");
        newUser.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setName("John");
        newUser.setEmail("invalid-email");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"invalid-email\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "John", "john@mail.com"),
                new UserDto(2L, "Alice", "alice@mail.com")
        );

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@mail.com"))
                .andExpect(jsonPath("$[1].name").value("Alice"))
                .andExpect(jsonPath("$[1].email").value("alice@mail.com"));
    }

    @Test
    public void testGetUserById() throws Exception {
        Long userId = 1L;
        UserDto user = new UserDto(userId, "John", "john@mail.com");

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(userId);
    }

    @Test
    public void testUpdateUser() throws Exception {
        Long userId = 1L;
        UserDto updatedUser = new UserDto();
        updatedUser.setName("John");
        updatedUser.setEmail("john@mail.com");

        when(userService.update(userId, updatedUser)).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@mail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));

        verify(userService, times(1)).update(userId, updatedUser);
    }

    @Test
    public void testUpdateUserEmptyName() throws Exception {
        Long userId = 1L;
        UserDto updatedUser = new UserDto();
        updatedUser.setName("");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"john@mail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Поле не должно быть пустым."));

        verify(userService, never()).update(userId, updatedUser);
    }

    @Test
    public void testUpdateUserInvalidEmail() throws Exception {
        Long userId = 1L;
        UserDto updatedUser = new UserDto();
        updatedUser.setEmail("invalid-email");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"invalid-email\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Некорректный формат e-mail. Адрес электронной почты должен содержать " +
                                "символ '@'."));

        verify(userService, never()).update(userId, updatedUser);
    }
}