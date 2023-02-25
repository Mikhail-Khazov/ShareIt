package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    Item item;
    User user;
    User owner;
    Comment comment;

    @BeforeEach
    void initialize() {
        user = userRepository.save(new User(1L, "John", "doe@ya.ru"));
        owner = userRepository.save(new User(2L, "Kevin", "kev@ya.ru"));
        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("Pen")
                .description("Blue")
                .available(true)
                .owner(owner)
                .request(null)
                .build());
        comment = commentRepository.save(Comment.builder()
                .id(1L)
                .text("ha ha")
                .item(item)
                .author(user)
                .created(LocalDateTime.now().minusMinutes(2))
                .build());
    }

    @Test
    void findByItemId_whenItemAndCommentExist_thenReturnListOfComment() {
        List<Comment> response = commentRepository.findByItemId(item.getId());

        compare(response);
    }

    @Test
    void findByUserId_whenUserAndCommentExist_thenReturnListOfComment() {
        List<Comment> response = commentRepository.findByUserId(user.getId());

        compare(response);
    }

    private void compare(List<Comment> response) {
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(comment.getId(), response.get(0).getId());
        assertEquals(comment.getText(), response.get(0).getText());
        assertEquals(comment.getAuthor().getEmail(), response.get(0).getAuthor().getEmail());
    }
}