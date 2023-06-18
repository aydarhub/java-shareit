package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void testToItemDto() {
        List<User> users = createUsers();
        User user = users.get(0);
        User user1 = users.get(1);

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId().longValue());
        assertEquals("Item1", itemDto.getName());
        assertEquals(1L, itemDto.getId().longValue());
        assertEquals("Description of item", itemDto.getDescription());
    }

    @Test
    void testToItemDto2() {
        List<User> users = createUsers();
        User user = users.get(0);

        Item item = createItem(user, null);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertTrue(itemDto.getAvailable());
        assertEquals("Item1", itemDto.getName());
        assertEquals(1L, itemDto.getId().longValue());
        assertEquals("Description of item", itemDto.getDescription());
    }

    @Test
    void testFromItemRequestDto() {
        Item item = ItemMapper.fromItemRequestDto(new ItemRequestDto());
        assertNull(item.getAvailable());
        assertNull(item.getName());
        assertNull(item.getId());
        assertNull(item.getDescription());
    }

    @Test
    void testToItemDtoList() {
        assertTrue(ItemMapper.toItemDtoList(new ArrayList<>()).isEmpty());
    }

    @Test
    void testToItemDtoList2() {
        List<User> users = createUsers();
        User user = users.get(0);
        User user1 = users.get(1);

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        List<ItemDto> itemDtoList = ItemMapper
                .toItemDtoList(itemList);
        assertEquals(1, itemDtoList.size());
        ItemDto getResult = itemDtoList.get(0);
        assertTrue(getResult.getAvailable());
        assertEquals(1L, getResult.getRequestId());
        assertEquals("Item1", getResult.getName());
        assertEquals(1L, getResult.getId());
        assertEquals("Description of item", getResult.getDescription());
    }

    @Test
    void testToItemDtoList3() {
        List<User> users = createUsers();
        User user = users.get(0);
        User user1 = users.get(1);
        User user2 = users.get(2);
        User user3 = users.get(3);

        ItemRequest itemRequest = createRequest(user1);

        Item item = createItem(user, itemRequest);


        ItemRequest itemRequest1 = createRequest(user3);

        Item item1 = createItem(user2, itemRequest1);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item);
        List<ItemDto> itemDtoList = ItemMapper
                .toItemDtoList(itemList);
        assertEquals(2, itemDtoList.size());
        ItemDto getResult = itemDtoList.get(0);
        assertEquals(1L, getResult.getRequestId().longValue());
        ItemDto getResult1 = itemDtoList.get(1);
        assertEquals(1L, getResult1.getRequestId().longValue());
        assertEquals("Item1", getResult1.getName());
        assertEquals(1L, getResult1.getId().longValue());
        assertEquals("Description of item", getResult1.getDescription());
        assertTrue(getResult1.getAvailable());
        assertEquals("Item1", getResult.getName());
        assertEquals(1L, getResult.getId().longValue());
    }

    @Test
    void testToItemWithBookingsResponseDto() {
        List<User> users = createUsers();
        User user = users.get(0);
        User user1 = users.get(1);
        User user2 = users.get(2);
        User user3 = users.get(3);
        User user4 = users.get(4);
        User user5 = users.get(5);
        User user6 = users.get(6);
        User user7 = users.get(7);

        ItemRequest itemRequest = createRequest(user1);
        Item item = createItem(user, itemRequest);
        ItemRequest itemRequest1 = createRequest(user4);
        Item item1 = createItem(user3, itemRequest1);

        Booking booking = new Booking();
        booking.setBooker(user2);
        booking.setEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        booking.setId(1L);
        booking.setItem(item1);
        booking.setStart(LocalDateTime.of(1, 1, 1, 1, 1));
        booking.setStatus(Status.WAITING);


        ItemRequest itemRequest2 = createRequest(user7);

        Item item2 = createItem(user6, itemRequest2);

        Booking booking1 = new Booking();
        booking1.setBooker(user5);
        booking1.setEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        booking1.setId(1L);
        booking1.setItem(item2);
        booking1.setStart(LocalDateTime.of(1, 1, 1, 1, 1));
        booking1.setStatus(Status.WAITING);
        ArrayList<Comment> commentList = new ArrayList<>();
        ItemWithBookingsResponseDto itemWithBookingsResponseDto = ItemMapper
                .toItemWithBookingsResponseDto(item, booking, booking1, commentList);
        assertTrue(itemWithBookingsResponseDto.getAvailable());
        assertEquals("Item1", itemWithBookingsResponseDto.getName());
        assertEquals(commentList, itemWithBookingsResponseDto.getComments());
        assertEquals(1L, itemWithBookingsResponseDto.getId());
        assertEquals("Description of item",
                itemWithBookingsResponseDto.getDescription());
        BookingDto lastBooking = itemWithBookingsResponseDto.getLastBooking();
        assertEquals(1L, lastBooking.getId().longValue());
    }

    @Test
    void testToItemWithBookingsResponseDtoSecond() {
        List<User> users = createUsers();

        ItemRequest itemRequest = createRequest(users.get(1));

        Item item = createItem(users.get(0), itemRequest);

        ItemRequest itemRequest1 = createRequest(users.get(4));

        Item item1 = createItem(users.get(3), itemRequest1);

        Comment comment = new Comment();
        comment.setAuthor(users.get(2));
        comment.setId(1L);
        comment.setItem(item1);
        comment.setText("Text");
        comment.setCreatedTime(LocalDateTime.of(1, 1, 1, 1, 1));

        ArrayList<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        ItemWithBookingsResponseDto itemWithBookingsResponseDto = ItemMapper
                .toItemWithBookingsResponseDto(item, null, null, commentList);
        assertTrue(itemWithBookingsResponseDto.getAvailable());
        assertEquals("Item1", itemWithBookingsResponseDto.getName());
        assertEquals(1L, itemWithBookingsResponseDto.getId());
        assertEquals("Description of item", itemWithBookingsResponseDto.getDescription());
        List<CommentResponseDto> comments = itemWithBookingsResponseDto.getComments();
        assertEquals(1, comments.size());
        CommentResponseDto getResult = comments.get(0);
        assertEquals("User3", getResult.getAuthorName());
        assertEquals("Text", getResult.getText());
        assertEquals(1L, getResult.getId());
        assertEquals("0001-01-01", getResult.getCreated().toLocalDate().toString());
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