package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PersonalValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImplDb;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemServiceImplDbTest {
    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserService userService;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private User testUser;
    private Item testItem;
    private ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        itemService = new ItemServiceImplDb(itemRepository, userService, bookingRepository, commentRepository);
        testUser = new User(1L, "Test User", "user@example.com");
        testItemDto = new ItemDto(1L, "Test ItemDto", "Description", true, null);
        testItem = new Item(1L, "Test Item", "Description", true, null, testUser);
        when(userService.validUser(testUser.getId())).thenReturn(testUser);
    }

    @Test
    public void testCreateItem() {
        when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    Item item = invocation.getArgument(0);
                    item.setId(1L);
                    item.setOwner(testUser);
                    return item;
                });

        ItemDto createdItem = itemService.create(testItemDto, testUser.getId());

        assertNotNull(createdItem.getId());
        assertEquals("Test ItemDto", createdItem.getName());
        assertEquals("Description", createdItem.getDescription());
    }

    @Test
    public void testGetAll() {
        List<Item> userItems = new ArrayList<>();
        userItems.add(new Item(1L, "Item 1", "Description 1", true, null,
                testUser));
        userItems.add(new Item(2L, "Item 2", "Description 2", true, null,
                testUser));
        userItems.add(new Item(3L, "Item 3", "Description 3", true, null,
                testUser));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1),
                userItems.get(0), testUser, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4),
                userItems.get(0), testUser, Status.APPROVED));
        bookings.add(new Booking(3L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                userItems.get(1), testUser, Status.REJECTED));
        bookings.add(new Booking(4L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3),
                userItems.get(1), testUser, Status.APPROVED));
        bookings.add(new Booking(5L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                userItems.get(2), testUser, Status.APPROVED));
        bookings.add(new Booking(6L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3),
                userItems.get(2), testUser, Status.APPROVED));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, "Comment 1", userItems.get(0), testUser, LocalDateTime.now()));
        comments.add(new Comment(2L, "Comment 2", userItems.get(0), testUser, LocalDateTime.now()));
        comments.add(new Comment(3L, "Comment 3", userItems.get(1), testUser, LocalDateTime.now()));
        comments.add(new Comment(4L, "Comment 4", userItems.get(2), testUser, LocalDateTime.now()));

        Mockito.when(itemRepository.findByOwnerId(1L)).thenReturn(userItems);
        Mockito.when(bookingRepository.findAllByItemIdIn(Mockito.anyList())).thenReturn(bookings);
        Mockito.when(commentRepository.findAll()).thenReturn(comments);

        List<ItemDtoToGet> items = itemService.getAll(1L, 0, 3);

        assertEquals(3, items.size());
        assertEquals("Item 3", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
        assertEquals("Item 1", items.get(2).getName());
    }

    @Test
    public void testGetItemById() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1),
                testItem, testUser, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(4),
                testItem, testUser, Status.APPROVED));

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, "Comment 1", testItem, testUser, LocalDateTime.now()));
        comments.add(new Comment(2L, "Comment 2", testItem, testUser, LocalDateTime.now()));

        // пользователь является владельцем товара
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findAllByItemId(testItem.getId())).thenReturn(bookings);
        when(commentRepository.findAllByItemId(testItem.getId())).thenReturn(comments);

        ItemDtoToGet itemDtoForOwner = itemService.getById(testItem.getId(), testUser.getId());

        assertNotNull(itemDtoForOwner);
        assertEquals(testItem.getId(), itemDtoForOwner.getId());

        // пользователь не владеет товаром
        User testUser2 = new User(2L, "Test User2", "user2@example.com");

        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findAllByItemId(testItem.getId())).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemId(testItem.getId())).thenReturn(new ArrayList<>());

        ItemDtoToGet itemDtoForNonOwner = itemService.getById(testItem.getId(), testUser2.getId());

        assertNotNull(itemDtoForNonOwner);
        assertEquals(testItem.getId(), itemDtoForNonOwner.getId());
        assertTrue(itemDtoForNonOwner.getComments().isEmpty());

        // товар не существует.
        assertThrows(EntityNotFoundException.class, () -> itemService.getById(1000L, testUser.getId()));
    }

    @Test
    public void testUpdateItemForOwner() {
        testItemDto.setName("Updated Item");

        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(testItem);

        ItemDto updatedItemDto = itemService.update(testItem.getId(), testUser.getId(), testItemDto);

        assertNotNull(updatedItemDto);
        assertEquals("Updated Item", updatedItemDto.getName());
    }

    @Test
    public void testUpdateItemForNonOwner() {
        User testUser2 = new User(2L, "Test User2", "user2@example.com");
        testItemDto.setName("Updated Item");

        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(testItem.getId(), testUser2.getId(),
                testItemDto));
    }

    @Test
    public void testSearchItemsSuccess() {
        String searchText = "item";
        Item item2 = new Item(2L, "Item 2", "Description 2", true, null, testUser);

        Mockito.when(itemRepository.searchItems(searchText)).thenReturn(List.of(testItem, item2));

        List<ItemDto> items = itemService.search(searchText, 0, 2, 1L);

        assertEquals(2, items.size());
        assertEquals("Test Item", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
    }

    @Test
    public void testSearchItemsEmptyResult() {
        String searchText = "nonexistent";

        Mockito.when(itemRepository.searchItems(searchText)).thenReturn(Collections.emptyList());

        List<ItemDto> items = itemService.search(searchText, 0, 2, 1L);

        assertTrue(items.isEmpty());
    }

    @Test
    public void testSearchItemsWithPagination() {
        String searchText = "item";
        Item item2 = new Item(2L, "Item 2", "Description 2", true, null, testUser);
        Item item3 = new Item(3L, "Item 3", "Description 3", true, null, testUser);

        Mockito.when(itemRepository.searchItems(searchText)).thenReturn(List.of(testItem, item2, item3));

        List<ItemDto> items = itemService.search(searchText, 1, 2, 1L);

        assertEquals(2, items.size());
        assertEquals("Item 2", items.get(0).getName());
        assertEquals("Item 3", items.get(1).getName());
    }

    @Test
    public void testCreateCommentWithValidBooking() {
        CommentDto commentDto = new CommentDto(1L, "Eww", "Test User", LocalDateTime.now());
        Comment comment = new Comment(1L, "Comment 1", testItem, testUser, LocalDateTime.now());
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1),
                testItem, testUser, Status.APPROVED));
        bookings.add(new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                testItem, testUser, Status.APPROVED));

        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(testItem));
        when(bookingRepository.findBookingByBookerIdAndItemId(testUser.getId(), testItem.getId())).thenReturn(bookings);
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto createdComment = itemService.createComment(commentDto, testItem.getId(), testUser.getId());

        assertNotNull(createdComment);
        assertEquals("Comment 1", createdComment.getText());
        assertEquals("Test User", createdComment.getAuthorName());

    }

    @Test
    public void testCreateCommentWithoutValidBooking() {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "Test User", LocalDateTime.now());

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        Mockito.when(bookingRepository.findBookingByBookerIdAndItemId(1L,
                1L)).thenReturn(Collections.emptyList());

        assertThrows(PersonalValidationException.class,
                () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    public void testCreateCommentRejectedBooking() {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "Test User", LocalDateTime.now());
        Booking testBooking = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                testItem, testUser, Status.APPROVED);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        Mockito.when(bookingRepository.findBookingByBookerIdAndItemId(1L,
                1L)).thenReturn(Collections.singletonList(testBooking));

        testBooking.setStatus(Status.REJECTED);

        assertThrows(PersonalValidationException.class,
                () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    public void testCreateCommentUnfinishedBooking() {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "Test User", LocalDateTime.now());
        Booking testBooking = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                testItem, testUser, Status.APPROVED);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        Mockito.when(bookingRepository.findBookingByBookerIdAndItemId(1L, 1L))
                .thenReturn(Collections.singletonList(testBooking));

        testBooking.setStart(LocalDateTime.now().plusHours(1));
        testBooking.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(PersonalValidationException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    public void testDeleteItemSuccess() {
        Mockito.when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        itemService.delete(testItem.getId(), testUser.getId());

        Mockito.verify(itemRepository).deleteById(testItem.getId());
    }

    @Test
    public void testDeleteItemUserNotFound() {
        Long userId = 999L;

        assertThrows(EntityNotFoundException.class, () -> itemService.delete(testItem.getId(), userId));
    }

    @Test
    public void testDeleteItemItemNotFound() {
        Long itemId = 999L;

        assertThrows(EntityNotFoundException.class, () -> itemService.delete(itemId, testUser.getId()));
    }

    @Test
    public void testDeleteItemNotOwner() {
        User user2 = new User(2L, "Test User2", "user2@mail.com");
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        assertThrows(PersonalValidationException.class, () -> itemService.delete(testItem.getId(), user2.getId()));
    }
}