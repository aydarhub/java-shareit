package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ItemServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ItemServiceImplTest {

    @MockBean
    private BookingJpaRepository bookingJpaRepository;

    @MockBean
    private CommentJpaRepository commentJpaRepository;

    @MockBean
    private ItemJpaRepository itemJpaRepository;

    @MockBean
    private ItemRequestJpaRepository itemRequestJpaRepository;

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @Test
    void testAddItem() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.save(any())).thenReturn(item);
        ItemDto itemDto = itemServiceImpl.addItem(new ItemRequestDto(), 1L);
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
        assertEquals("Item1", itemDto.getName());
        assertEquals(1L, itemDto.getId().longValue());
        assertEquals("Item description", itemDto.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemJpaRepository).save(any());
    }

    @Test
    void testAddItem2() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.save(any())).thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.addItem(new ItemRequestDto(), 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemJpaRepository).save(any());
    }

    @Test
    void testAddItem3() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);
        when(itemJpaRepository.save(any())).thenReturn(item);
        assertThrows(NotFoundException.class, () -> itemServiceImpl.addItem(new ItemRequestDto(), 1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testUpdateItem() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);

        User user2 = createUser();

        User user3 = createUser();

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest1.setDescription("Item request description 2");
        itemRequest1.setId(1L);
        itemRequest1.setRequester(user3);

        Item item1 = createItem(user2, itemRequest1);
        when(itemJpaRepository.save(any())).thenReturn(item1);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        ItemDto updatedItem = itemServiceImpl.updateItem(1L, new ItemRequestDto(), 1L);
        assertTrue(updatedItem.getAvailable());
        assertEquals(1L, updatedItem.getRequestId());
        assertEquals("Item1", updatedItem.getName());
        assertEquals(1L, updatedItem.getId().longValue());
        assertEquals("Item description", updatedItem.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository, atLeast(1)).getReferenceById(anyLong());
        verify(itemJpaRepository).save(any());
    }

    @Test
    void testUpdateItem2() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);
        when(itemJpaRepository.save(any())).thenThrow(new NotFoundException("An error occurred"));
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemServiceImpl.updateItem(1L, new ItemRequestDto(), 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository, atLeast(1)).getReferenceById(anyLong());
        verify(itemJpaRepository).save(any());
    }

    @Test
    void testUpdateItem3() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);

        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);

        User user2 = createUser();

        User user3 = createUser();

        ItemRequest itemRequest1 = createRequest(user3);

        Item item1 = createItem(user2, itemRequest1);
        when(itemJpaRepository.save(any())).thenReturn(item1);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemServiceImpl.updateItem(1L, new ItemRequestDto(), 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).existsById(anyLong());
    }

    @Test
    void testFindItemById() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Booking booking = createBooking(user, item);
        Optional<Booking> ofResult = Optional.of(booking);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Booking booking1 = createBooking(user3, item1);

        Optional<Booking> ofResult1 = Optional.of(booking1);
        when(bookingJpaRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(ofResult);
        when(bookingJpaRepository.findFirstByItemIdAndStartIsAfterOrderByStart(anyLong(), any()))
                .thenReturn(ofResult1);

        User user6 = createUser();

        User user7 = createUser();

        ItemRequest itemRequest2 = createRequest(user7);

        Item item2 = createItem(user6, itemRequest2);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item2);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Comment> commentList = new ArrayList<>();
        when(commentJpaRepository.findAllByItemId(anyLong())).thenReturn(commentList);
        ItemWithBookingsResponseDto actualItemById = itemServiceImpl.findItemById(1L, 1L);
        assertTrue(actualItemById.getAvailable());
        assertEquals("Item1", actualItemById.getName());
        assertEquals(commentList, actualItemById.getComments());
        assertEquals(1L, actualItemById.getId().longValue());
        assertEquals("Item description", actualItemById.getDescription());
        BookingDto lastBooking = actualItemById.getLastBooking();
        assertEquals(1L, lastBooking.getId());
        assertEquals(1L, lastBooking.getBookerId());
        BookingDto nextBooking = actualItemById.getNextBooking();
        assertEquals(1L, nextBooking.getId());
        assertEquals(1L, nextBooking.getBookerId());
        verify(bookingJpaRepository).findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any());
        verify(bookingJpaRepository).findFirstByItemIdAndStartIsAfterOrderByStart(anyLong(), any());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).getReferenceById(anyLong());
        verify(commentJpaRepository).findAllByItemId(anyLong());
    }

    @Test
    void testFindItemById2() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Booking booking = createBooking(user, item);
        Optional<Booking> ofResult = Optional.of(booking);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Booking booking1 = createBooking(user3, item1);
        Optional<Booking> ofResult1 = Optional.of(booking1);
        when(bookingJpaRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(ofResult);
        when(bookingJpaRepository.findFirstByItemIdAndStartIsAfterOrderByStart(anyLong(), any()))
                .thenReturn(ofResult1);

        User user6 = createUser();

        User user7 = createUser();

        ItemRequest itemRequest2 = createRequest(user7);

        Item item2 = createItem(user6, itemRequest2);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item2);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        when(commentJpaRepository.findAllByItemId(anyLong())).thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.findItemById(1L, 1L));
        verify(bookingJpaRepository).findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any());
        verify(bookingJpaRepository).findFirstByItemIdAndStartIsAfterOrderByStart(anyLong(), any());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).getReferenceById(anyLong());
        verify(commentJpaRepository).findAllByItemId(anyLong());
    }

    @Test
    void testFindItemById3() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        User user3 = new User();
        user3.setEmail("user3@example.org");
        user3.setId(1L);
        user3.setName("User3");
        Booking booking = mock(Booking.class);
        when(booking.getId()).thenReturn(1L);
        when(booking.getStatus()).thenReturn(Status.REJECTED);
        when(booking.getBooker()).thenReturn(user3);
        doNothing().when(booking).setBooker(any());
        doNothing().when(booking).setEnd(any());
        doNothing().when(booking).setId(anyLong());
        doNothing().when(booking).setItem(any());
        doNothing().when(booking).setStart(any());
        doNothing().when(booking).setStatus(any());
        booking.setBooker(user);
        booking.setEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(1, 1, 1, 1, 1));
        booking.setStatus(Status.WAITING);
        Optional<Booking> ofResult = Optional.of(booking);

        User user4 = createUser();

        User user5 = createUser();

        User user6 = createUser();

        ItemRequest itemRequest1 = createRequest(user6);

        Item item1 = createItem(user5, itemRequest1);

        Booking booking1 = createBooking(user4, item1);

        Optional<Booking> ofResult1 = Optional.of(booking1);
        when(bookingJpaRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(ofResult);
        when(bookingJpaRepository.findFirstByItemIdAndStartIsAfterOrderByStart(anyLong(), any()))
                .thenReturn(ofResult1);

        User user7 = createUser();

        User user8 = createUser();

        ItemRequest itemRequest2 = createRequest(user8);

        Item item2 = createItem(user7, itemRequest2);
        when(itemJpaRepository.getReferenceById(anyLong())).thenReturn(item2);
        when(itemJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Comment> commentList = new ArrayList<>();
        when(commentJpaRepository.findAllByItemId(anyLong())).thenReturn(commentList);
        ItemWithBookingsResponseDto actualItemById = itemServiceImpl.findItemById(1L, 1L);
        assertTrue(actualItemById.getAvailable());
        assertEquals("Item1", actualItemById.getName());
        assertEquals(commentList, actualItemById.getComments());
        assertEquals(1L, actualItemById.getId());
        assertEquals("Item description", actualItemById.getDescription());
        BookingDto nextBooking = actualItemById.getNextBooking();
        assertEquals(1L, nextBooking.getId());
        assertEquals(1L, nextBooking.getBookerId());
        verify(bookingJpaRepository).findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any());
        verify(bookingJpaRepository).findFirstByItemIdAndStartIsAfterOrderByStart(anyLong(), any());
        verify(booking).getStatus();
        verify(booking).setBooker(any());
        verify(booking).setEnd(any());
        verify(booking).setId(anyLong());
        verify(booking).setItem(any());
        verify(booking).setStart(any());
        verify(booking).setStatus(any());
        verify(itemJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).getReferenceById(anyLong());
        verify(commentJpaRepository).findAllByItemId(anyLong());
    }

    @Test
    void testFindUserItems() {
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.findUserItems(1L).isEmpty());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItems2() {
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any()))
                .thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.findUserItems(1L));
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItems3() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Booking booking = createBooking(user, item);

        ArrayList<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(bookingList);
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.findUserItems(1L).isEmpty());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItems4() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Booking booking = createBooking(user, item);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Booking booking1 = createBooking(user3, item1);
        booking1.setStatus(Status.APPROVED);

        ArrayList<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking1);
        bookingList.add(booking);
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(bookingList);
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
    }

    @Test
    void testFindUserItems5() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Booking booking = createBooking(user, item);

        ArrayList<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(bookingList);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.findUserItems(1L).isEmpty());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItems6() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Booking booking = createBooking(user, item);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Booking booking1 = createBooking(user3, item1);

        ArrayList<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking1);
        bookingList.add(booking);
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(bookingList);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
    }

    @Test
    void testFindUserItems7() {
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        assertThrows(NotFoundException.class, () -> itemServiceImpl.findUserItems(1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testFindUserItems8() {
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(itemList);
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        assertEquals(1, itemServiceImpl.findUserItems(1L).size());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItems9() {
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());

        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Comment comment = createComment(user, item);

        ArrayList<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(commentList);
        assertTrue(itemServiceImpl.findUserItems(1L).isEmpty());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItems10() {
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any())).thenReturn(new ArrayList<>());
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any())).thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());

        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest itemRequest = createRequest(user2);

        Item item = createItem(user1, itemRequest);

        Comment comment = createComment(user, item);

        User user3 = createUser();

        User user4 = createUser();

        User user5 = createUser();

        ItemRequest itemRequest1 = createRequest(user5);

        Item item1 = createItem(user4, itemRequest1);

        Comment comment1 = createComment(user3, item1);

        ArrayList<Comment> commentList = new ArrayList<>();
        commentList.add(comment1);
        commentList.add(comment);
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(commentList);
        assertTrue(itemServiceImpl.findUserItems(1L).isEmpty());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(),
                any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(),
                any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testFindUserItem() {
        User user = createUser();

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest request = createRequest(user2);

        Item item = createItem(user1, request);

        Booking booking = createBooking(user, item);

        ArrayList<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingJpaRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(), any()))
                .thenReturn(bookingList);
        when(bookingJpaRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(any(), any()))
                .thenReturn(new ArrayList<>());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user3 = createUser();

        User user4 = createUser();

        ItemRequest request1 = createRequest(user4);

        Item item1 = createItem(user3, request1);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        when(itemJpaRepository.findAllByOwnerId(anyLong())).thenReturn(itemList);
        when(commentJpaRepository.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        assertEquals(1, itemServiceImpl.findUserItems(1L).size());
        verify(bookingJpaRepository).findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(any(), any());
        verify(bookingJpaRepository).findFirstByItemIdInAndStartIsAfterOrderByStart(any(), any());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByOwnerId(anyLong());
        verify(commentJpaRepository).findAllByItemIdIn(any());
    }

    @Test
    void testSearchItemsByText() {
        when(itemJpaRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("Text", "Text", true))
                .thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.searchItemsByText("Text", 1L).isEmpty());
        verify(itemJpaRepository)
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("Text", "Text", true);
    }

    @Test
    void testSearchItemsByText2() {
        User user = createUser();

        User user1 = createUser();

        ItemRequest request = createRequest(user1);

        Item item = createItem(user, request);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemJpaRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("Text", "Text", true))
                .thenReturn(itemList);
        List<ItemDto> itemDtoList = itemServiceImpl.searchItemsByText("Text", 1L);
        assertEquals(1, itemDtoList.size());
        ItemDto getResult = itemDtoList.get(0);
        assertTrue(getResult.getAvailable());
        assertEquals(1L, getResult.getRequestId());
    }

    @Test
    void testSearchItemsByText3() {
        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);

        User user2 = createUser();

        User user3 = createUser();

        ItemRequest itemRequest1 = createRequest(user3);

        Item item1 = createItem(user2, itemRequest1);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item);
        when(itemJpaRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("Text", "Text", true))
                .thenReturn(itemList);
        List<ItemDto> itemDtoList = itemServiceImpl.searchItemsByText("Text", 1L);
        assertEquals(2, itemDtoList.size());
        ItemDto getResult = itemDtoList.get(0);
        assertEquals(1L, getResult.getRequestId());
        ItemDto getResult1 = itemDtoList.get(1);
        assertEquals(1L, getResult1.getRequestId());
        assertEquals("Item description", getResult1.getDescription());
        assertTrue(getResult1.getAvailable());
        assertEquals("Item1", getResult.getName());
    }

    @Test
    void testSearchItemsByText4() {
        when(itemJpaRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("", "", true))
                .thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.searchItemsByText("", 1L).isEmpty());
    }

    @Test
    void testSearchItemsByText5() {
        when(itemJpaRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("Text", "Text", true))
                .thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.searchItemsByText("Text", 1L));
        verify(itemJpaRepository)
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable("Text", "Text", true);
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

    private Booking createBooking(User user, Item item) {
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(1, 1, 1, 1, 1));
        booking.setStatus(Status.WAITING);
        return booking;
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