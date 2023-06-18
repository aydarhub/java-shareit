package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.MappingIterator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingMapperTest {

    @Test
    void testToBooking() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setEnd(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.of(1, 1, 1, 1, 1, 1));

        List<User> users = createUsers();

        ItemRequest itemRequest = createRequest(users.get(2));
        Item item = createItem(users.get(1), itemRequest);
        Booking booking = BookingMapper.toBooking(bookingRequestDto, users.get(0), item);
        assertSame(users.get(0), booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
        assertSame(item, booking.getItem());
        assertEquals("01:01:01", booking.getEnd().toLocalTime().toString());
        assertEquals("01:01:01", booking.getStart().toLocalTime().toString());
    }

    @Test
    void testToBookingResponseDto() {
        List<User> users = createUsers();
        ItemRequest itemRequest = createRequest(users.get(2));
        Item item = createItem(users.get(1), itemRequest);

        Booking booking = new Booking();
        booking.setBooker(users.get(0));
        booking.setEnd(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        booking.setStatus(Status.WAITING);
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);
        assertEquals(Status.WAITING, bookingResponseDto.getStatus());
        assertEquals("01:01:01", bookingResponseDto.getEnd().toLocalTime().toString());
        assertEquals("01:01:01", bookingResponseDto.getStart().toLocalTime().toString());
        assertEquals(1L, bookingResponseDto.getId());
        ItemDto itemDto = bookingResponseDto.getItem();
        assertEquals("Item1", itemDto.getName());
        assertEquals(1L, itemDto.getId());
        assertEquals(1L, bookingResponseDto.getBooker().getId());
    }

    @Test
    void testToListBookingResponseDto() {
        List<User> users = createUsers();
        ItemRequest itemRequest = createRequest(users.get(2));
        Item item = createItem(users.get(1), itemRequest);

        Booking booking = new Booking();
        booking.setBooker(users.get(0));
        booking.setEnd(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        booking.setStatus(Status.WAITING);

        LinkedHashSet<Booking> bookingSet = new LinkedHashSet<>();
        bookingSet.add(booking);
        assertEquals(1, BookingMapper.toListBookingResponseDto(bookingSet).size());
    }

    @Test
    void testToListBookingResponseDto2() {
        Iterable<Booking> iterable = mock(Iterable.class);
        when(iterable.iterator()).thenReturn(MappingIterator.emptyIterator());
        assertTrue(BookingMapper.toListBookingResponseDto(iterable).isEmpty());
        verify(iterable).iterator();
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
        List<User> listUsers = new ArrayList<>();
        listUsers.add(user);
        listUsers.add(user1);
        listUsers.add(user2);
        return listUsers;
    }

    private ItemRequest createRequest(User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1, 1));
        itemRequest.setDescription("Description of request");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        return itemRequest;
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

}