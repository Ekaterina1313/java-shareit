package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFull;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImplBd;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceImplDbTest {
    private UserService userService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestServiceImplBd itemRequestService;
    private User testUser;
    private Item testItem;
    private ItemDto testItemDto;
    ItemRequestDto testItemRequestDto;
    ItemRequest testItemRequest;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImplBd(userService, itemRepository, itemRequestRepository);
        testUser = new User(1L, "Test User", "user@example.com");
        testItemDto = new ItemDto(1L, "Test ItemDto", "Description", true, 1L);
        testItem = new Item(1L, "Test Item", "Description", true, 1L, testUser);
        testItemRequestDto = new ItemRequestDto(1L, "Description", LocalDateTime.now());
        testItemRequest = new ItemRequest(1L, "Description", testUser, LocalDateTime.now());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    }

    @Test
    void testCreateItemRequest() {
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(testItemRequest);

        ItemRequestDtoFull result = itemRequestService.create(testItemRequestDto, testUser.getId());

        verify(userService, Mockito.times(1)).validUser(testUser.getId());
        verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));

        assertEquals(testItemRequestDto.getId(), result.getId());
        assertEquals(testItemRequestDto.getDescription(), result.getDescription());
        assertEquals(new ArrayList<>(), result.getItems());
    }

    @Test
    public void testGetAllByUserIDUserExists() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(testItemRequest);
        itemRequests.add(new ItemRequest(2L, "Description 2", testUser, LocalDateTime.now()));

        List<Item> items = new ArrayList<>();
        items.add(testItem);
        items.add(new Item(2L, "Item 2", "Description 2", true, 2L, testUser));

        Mockito.when(itemRequestRepository.findByRequestorId(1L)).thenReturn(itemRequests);
        Mockito.when(itemRepository.findByRequestIds(List.of(1L, 2L))).thenReturn(items);

        List<ItemRequestDtoFull> result = itemRequestService.getAllByOwner(1L);

        assertEquals(2, result.size());
        assertEquals("Description 2", result.get(0).getDescription());
        assertEquals(2L, result.get(0).getId());
        assertEquals("Description", result.get(1).getDescription());
        assertEquals(1L, result.get(1).getId());
    }

    @Test
    public void testGetOtherUsersRequestsSuccess() {
        User otherUser = new User(2L, "Test User 2", "test2@mail");

        List<ItemRequest> otherUserRequests = new ArrayList<>();
        otherUserRequests.add(new ItemRequest(2L, "Request 1", otherUser, LocalDateTime.now()));
        otherUserRequests.add(new ItemRequest(3L, "Request 2", otherUser, LocalDateTime.now()));

        List<Item> items = new ArrayList<>();
        items.add(testItem);
        items.add(new Item(2L, "Item 2", "Description 2", true, 2L, otherUser));
        items.add(new Item(3L, "Item 3", "Description 3", true, 3L, otherUser));

        Mockito.when(itemRequestRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC,
                        "created"))))
                .thenReturn(new PageImpl<>(otherUserRequests));
        Mockito.when(itemRepository.findByRequestIds(List.of(2L, 3L)))
                .thenReturn(items);

        List<ItemRequestDtoFull> result = itemRequestService.getAllByOthers(1L, 0, 10);

        assertEquals(2, result.size());
        assertEquals("Request 1", result.get(0).getDescription());
        assertEquals(2L, result.get(0).getId());
        assertEquals("Request 2", result.get(1).getDescription());
        assertEquals(3L, result.get(1).getId());
    }

    @Test
    public void testGetItemRequestById() {
        when(itemRequestRepository.findById(testItemRequest.getId())).thenReturn(Optional.of(testItemRequest));

        List<Item> items = new ArrayList<>();
        items.add(testItem);
        items.add(new Item(2L, "Item 2", "Description 2", true, 2L, testUser));

        when(itemRepository.findByRequestId(testItemRequest.getId())).thenReturn(items);

        ItemRequestDtoFull result = itemRequestService.getById(testItemRequest.getId(), testUser.getId());

        assertEquals(result.getItems().size(), 2);
        assertEquals(result.getDescription(), "Description");
        assertEquals(result.getId(), testItemRequest.getId());
    }

    @Test
    public void testCreateItemRequestUserNotFound() {
        Long userId = 999L;
        Mockito.when(userService.validUser(999L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(testItemRequestDto, userId));
    }

    @Test
    public void testGetAllByUserIDEmptyResult() {
        Mockito.when(itemRequestRepository.findByRequestorId(1L)).thenReturn(new ArrayList<>());

        List<ItemRequestDtoFull> result = itemRequestService.getAllByOwner(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetOtherUsersRequestsEmptyResult() {
        Mockito.when(itemRequestRepository.findAll(PageRequest.of(0, 10,
                        Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDtoFull> result = itemRequestService.getAllByOthers(1L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByIdItemRequestNotFound() {
        Long itemRequestId = 999L;

        Mockito.when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getById(itemRequestId, 1L));
    }

    @Test
    public void testDeleteItemRequestSuccess() {
        Mockito.when(itemRequestRepository.findById(testItemRequest.getId())).thenReturn(Optional.of(testItemRequest));
        itemRequestService.delete(testItemRequest.getId(), testUser.getId());

        Mockito.verify(itemRequestRepository).deleteById(testItemRequest.getId());
    }

    @Test
    public void testDeleteItemRequestUserNotFound() {
        Long userId = 999L;

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.delete(testItemRequest.getId(), userId));
    }

    @Test
    public void testDeleteItemRequestItemRequestNotFound() {
        Long itemRequestId = 999L;

        Mockito.when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.delete(itemRequestId, testUser.getId()));
    }

    @Test
    public void testDeleteItemRequestNotOwner() {
        User otherUser = new User(2L, "Other User", "other@mail");
        Mockito.when(itemRequestRepository.findById(testItemRequest.getId())).thenReturn(Optional.of(testItemRequest));
        Mockito.when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        assertThrows(PersonalValidationException.class, () -> itemRequestService.delete(testUser.getId(), otherUser.getId()));
    }
}