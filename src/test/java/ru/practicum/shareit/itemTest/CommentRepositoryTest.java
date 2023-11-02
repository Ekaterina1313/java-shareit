package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    User user1 = new User();
    User user2 = new User();
    Item item = new Item();
    Comment comment1 = new Comment();
    Comment comment2 = new Comment();
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setup() {
        user1.setName("User1Name");
        user1.setEmail("user1@mail.com");
        entityManager.persist(user1);
        entityManager.flush();

        user2.setName("User2Name");
        user2.setEmail("user2@mail.com");
        entityManager.persist(user2);
        entityManager.flush();

        item.setName("ItemName");
        item.setDescription("ItemDesc");
        item.setOwner(user1);
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();

        comment1.setText("Comment1Text");
        comment1.setItem(item);
        comment1.setAuthor(user2);
        comment1.setCreated(LocalDateTime.now());
        entityManager.persist(comment1);
        entityManager.flush();

        comment2.setText("Comment2Text");
        comment2.setItem(item);
        comment2.setAuthor(user2);
        comment2.setCreated(LocalDateTime.now());
        entityManager.persist(comment2);
        entityManager.flush();
    }

    @Test
    public void testFindAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertEquals(2, comments.size());
        assertEquals("Comment1Text", comments.get(0).getText());
        assertEquals("Comment2Text", comments.get(1).getText());
    }
}