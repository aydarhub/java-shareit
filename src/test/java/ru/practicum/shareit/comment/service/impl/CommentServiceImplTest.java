package ru.practicum.shareit.comment.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CommentServiceImpl.class})
@ExtendWith(SpringExtension.class)
class CommentServiceImplTest {

    @MockBean
    private BookingJpaRepository bookingJpaRepository;

    @MockBean
    private CommentJpaRepository commentJpaRepository;

    @MockBean
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private CommentServiceImpl commentServiceImpl;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @Test
    void testPostComment() {
        when(bookingJpaRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);

        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Comment comment = createComment(user3, item1);
        when(commentJpaRepository.save(any())).thenReturn(comment);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");
        CommentResponseDto postComment = commentServiceImpl.postComment(1L, 1L, commentRequestDto);
        assertEquals("User1", postComment.getAuthorName());
        assertEquals("Text", postComment.getText());
        assertEquals(1L, postComment.getId());
        assertEquals("0001-01-01", postComment.getCreated().toLocalDate().toString());
        verify(bookingJpaRepository).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).getReferenceById(anyLong());
        verify(commentJpaRepository).save(any());
    }

    @Test
    void testPostComment2() {
        when(bookingJpaRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);

        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        when(commentJpaRepository.save(any())).thenThrow(new NotFoundException("An error occurred"));

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");
        assertThrows(NotFoundException.class,
                () -> commentServiceImpl.postComment(1L, 1L, commentRequestDto));
        verify(bookingJpaRepository).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).getReferenceById(anyLong());
        verify(commentJpaRepository).save(any());
    }

    @Test
    void testPostComment3() {
        when(bookingJpaRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(false);

        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Comment comment = createComment(user3, item1);
        when(commentJpaRepository.save(any())).thenReturn(comment);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");
        assertThrows(BadRequestException.class,
                () -> commentServiceImpl.postComment(1L, 1L, commentRequestDto));
        verify(bookingJpaRepository).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
    }

    @Test
    void testPostCommentFourth() {
        when(bookingJpaRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);

        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Comment comment = createComment(user3, item1);
        when(commentJpaRepository.save(any())).thenReturn(comment);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");
        assertThrows(NotFoundException.class, () -> commentServiceImpl.postComment(1L, 1L, commentRequestDto));
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
    }

    @Test
    void testPostComment5() {
        when(bookingJpaRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);

        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(false);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest request1 = createRequest(user5);

        Item item1 = createItem(user4, request1);

        Comment comment = createComment(user3, item1);
        when(commentJpaRepository.save(any())).thenReturn(comment);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");
        assertThrows(NotFoundException.class,
                () -> commentServiceImpl.postComment(1L, 1L, commentRequestDto));
        verify(itemJpaRepository).existsById(anyLong());
    }

    private User createUser() {
        User user = new User();
        user.setEmail("user1@example.org");
        user.setId(1L);
        user.setName("User1");
        return user;
    }

    private ItemRequest createRequest(User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest.setDescription("Item request description");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        return itemRequest;
    }

    private Item createItem(User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Item description");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user);
        item.setRequest(itemRequest);
        return item;
    }

    private Comment createComment(User user, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setId(1L);
        comment.setItem(item);
        comment.setText("Text");
        comment.setCreatedTime(LocalDateTime.of(1, 1, 1, 1, 1));
        return comment;
    }

}