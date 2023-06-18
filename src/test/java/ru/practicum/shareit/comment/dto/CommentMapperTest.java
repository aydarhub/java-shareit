package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CommentMapperTest {

    @Test
    void testToComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");

        List<User> users = createUsers();

        ItemRequest itemRequest = createRequest(users.get(2));

        Item item = createItem(users.get(1), itemRequest);

        Comment comment = CommentMapper.toComment(commentRequestDto, users.get(0), item,
                LocalDateTime.of(1, 1, 1, 1, 1));
        assertSame(users.get(0), comment.getAuthor());
        assertEquals("Text", comment.getText());
        assertEquals("0001-01-01", comment.getCreatedTime().toLocalDate().toString());
        assertSame(item, comment.getItem());
    }

    @Test
    void testToCommentResponseDto() {
        List<User> users = createUsers();

        ItemRequest itemRequest = createRequest(users.get(2));

        Item item = createItem(users.get(1), itemRequest);

        Comment comment = new Comment();
        comment.setAuthor(users.get(0));
        comment.setId(1L);
        comment.setItem(item);
        comment.setText("Text");
        comment.setCreatedTime(LocalDateTime.of(1, 1, 1, 1, 1));
        CommentResponseDto commentResponseDto = CommentMapper.toCommentResponseDto(comment);
        assertEquals("User1", commentResponseDto.getAuthorName());
        assertEquals("Text", commentResponseDto.getText());
        assertEquals(1L, commentResponseDto.getId());
        assertEquals("0001-01-01", commentResponseDto.getCreated().toLocalDate().toString());
    }

    private List<User> createUsers() {
        User user = new User();
        user.setEmail("user1@example.org");
        user.setId(1L);
        user.setName("User1");

        User user1 = new User();
        user1.setEmail("user2@example.org");
        user1.setId(2L);
        user1.setName("User2");

        User user2 = new User();
        user2.setEmail("user3@example.org");
        user2.setId(3L);
        user2.setName("User3");

        User user3 = new User();
        user3.setEmail("user4@example.org");
        user3.setId(4L);
        user3.setName("User4");

        User user4 = new User();
        user4.setEmail("user5@example.org");
        user4.setId(5L);
        user4.setName("User5");

        User user5 = new User();
        user5.setEmail("user6@example.org");
        user5.setId(6L);
        user5.setName("User6");

        User user6 = new User();
        user6.setEmail("user7@example.org");
        user6.setId(7L);
        user6.setName("User7");

        User user7 = new User();
        user7.setEmail("user8@example.org");
        user7.setId(8L);
        user7.setName("User8");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        users.add(user7);
        return users;
    }

    private Item createItem(User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Description of item");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user);
        item.setRequest(itemRequest);
        return item;
    }

    private ItemRequest createRequest(User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest.setDescription("Description of request");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        return itemRequest;
    }

}