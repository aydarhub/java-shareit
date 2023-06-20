package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private ItemJpaRepository itemJpaRepository;
    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private BookingJpaRepository bookingJpaRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void findBookingsByUserIdForAllState() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        List<Booking> bookings = Collections.singletonList(booking);
        when(userJpaRepository.existsById(userId)).thenReturn(true);
        when(bookingJpaRepository.findBookingByBookerId(any(), any())).thenReturn(bookings);

        List<BookingResponseDto> actualBookings =
                bookingService.findBookingsByUserId(userId, state, from, size);

        assertEquals(bookings.get(0).getId(), actualBookings.get(0).getId());
    }

    @Test
    void findBookingsByUserIdForFutureState() {
        Long userId = 1L;
        String state = "FUTURE";
        Integer from = 0;
        Integer size = 10;

        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(users.get(1));
        booking.setStatus(Status.WAITING);
        List<Booking> bookings = Collections.singletonList(booking);
        when(userJpaRepository.existsById(userId)).thenReturn(true);
        when(bookingJpaRepository.findBookingByBookerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(bookings);
        List<BookingResponseDto> result = bookingService.findBookingsByUserId(userId, state, from, size);

        assertEquals(bookings.get(0).getId(), result.get(0).getId());
    }

    @Test
    void findBookingsByUserIdForWaitingState() {
        Long userId = 1L;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        List<Booking> bookings = Collections.singletonList(booking);
        when(userJpaRepository.existsById(userId)).thenReturn(true);
        when(bookingJpaRepository.findBookingByBookerIdAndStatusEquals(
                userId, Status.WAITING, PageRequest.of(from / size, size)))
                .thenReturn(bookings);
        List<BookingResponseDto> result =
                bookingService.findBookingsByUserId(userId, state, from, size);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findBookingsByUserIdForCurrentState() {
        Long userId = 1L;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 10;

        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));
        List<Booking> bookings = List.of(booking);
        when(userJpaRepository.existsById(userId)).thenReturn(true);
        when(bookingJpaRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(
                any(), any(), any(), any()))
                .thenReturn(bookings);
        List<BookingResponseDto> result =
                bookingService.findBookingsByUserId(userId, state, from, size);
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findBookingsByOwnerIdWhenStateIsWaiting() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));
        itemJpaRepository.save(item);
        Booking booking = createBooking(item, users.get(0));
        booking.setStatus(Status.WAITING);
        bookingJpaRepository.save(booking);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        Iterable<BookingResponseDto> bookings =
                bookingService.findBookingsByOwnerId(1L, "WAITING", 0, 10);

        assertEquals(1, List.of(bookings).size());
    }

    @Test
    void findBookingsByOwnerIdWhenStateIsFuture() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));
        itemJpaRepository.save(item);
        Booking booking = createBooking(item, users.get(0));
        bookingJpaRepository.save(booking);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        Iterable<BookingResponseDto> bookings =
                bookingService.findBookingsByOwnerId(1L, "FUTURE", 0, 10);

        assertEquals(1, List.of(bookings).size());
    }

    @Test
    void findBookingsByOwnerIdWhenStateIsAll() {
        List<User> users = createUsers();
        User user = users.get(0);

        Item item = createItem(user);
        itemJpaRepository.save(item);

        Booking booking = createBooking(item, user);
        bookingJpaRepository.save(booking);

        when(userJpaRepository.existsById(any())).thenReturn(true);
        Iterable<BookingResponseDto> bookings = bookingService.findBookingsByOwnerId(user.getId(), "ALL", 0, 10);
        assertEquals(1, List.of(bookings).size());
    }

    @Test
    void findBookingsByOwnerIdWhenStateIsPast() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));
        itemJpaRepository.save(item);

        Booking booking = createBooking(item, users.get(0));
        ;
        booking.setStatus(Status.APPROVED);
        bookingJpaRepository.save(booking);

        when(userJpaRepository.existsById(any())).thenReturn(true);
        Iterable<BookingResponseDto> bookings =
                bookingService.findBookingsByOwnerId(users.get(0).getId(), "PAST", 0, 1);
        assertEquals(1, List.of(bookings).size());
    }

    @Test
    void findBookingsByOwnerIdWhenStateIsCurrent() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));
        itemJpaRepository.save(item);

        Booking booking = createBooking(item, users.get(0));
        booking.setStatus(Status.APPROVED);
        bookingJpaRepository.save(booking);
        when(userJpaRepository.existsById(any())).thenReturn(true);

        Iterable<BookingResponseDto> bookings =
                bookingService.findBookingsByOwnerId(users.get(0).getId(), "CURRENT", 0, 10);

        assertEquals(1, List.of(bookings).size());
    }

    @Test
    void addBookingWhenStartTimeIsEqualToEndTimeThenThrowBadRequestException() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now());
        bookingRequestDto.setItemId(1L);
        Long userId = 1L;

        assertThrows(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingRequestDto, userId));
    }

    @Test
    void addBookingWhenStartTimeIsAfterEndTimeThenThrowBadRequestException() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now());
        bookingRequestDto.setItemId(1L);
        Long userId = 1L;

        assertThrows(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingRequestDto, userId));
    }

    @Test
    void addBookingWhenStartTimeIsBeforeCurrentTimeThenThrowBadRequestException() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setItemId(1L);
        Long userId = 1L;

        assertThrows(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingRequestDto, userId));
    }

    @Test
    void addBookingWhenItemOrUserDoesNotExistThenThrowNotFoundException() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        Long userId = 1L;
        when(itemJpaRepository.existsById(bookingRequestDto.getItemId())).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingRequestDto, userId));
    }

    @Test
    void addBookingWhenAllInputParametersAreValid() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));

        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));
        booking.setStatus(Status.APPROVED);

        when(itemJpaRepository.getReferenceById(any())).thenReturn(item);
        when(bookingJpaRepository.save(any())).thenReturn(booking);
        when(itemJpaRepository.existsById(any())).thenReturn(true);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        BookingResponseDto bookingResponseDto =
                bookingService.addBooking(bookingRequestDto, users.get(1).getId());
        assertEquals(booking.getId(), bookingResponseDto.getId());
    }

    @Test
    void updateBookingWhenBookingIdOrUserIdIsInvalidThenThrowNotFoundException() {
        Long bookingId = 1L;
        Long userId = 1L;
        when(bookingJpaRepository.existsById(bookingId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));
    }

    @Test
    void updateBookingWhenStatusIsAlreadyApprovedThenThrowBadRequestException() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));
        booking.setStatus(Status.APPROVED);

        when(bookingJpaRepository.getReferenceById(booking.getId())).thenReturn(booking);
        when(bookingJpaRepository.existsById(booking.getId())).thenReturn(true);
        when(userJpaRepository.existsById(any())).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> bookingService.updateBooking(users.get(0).getId(), booking.getId(), true));
    }

    @Test
    void updateBookingWhenUserIdIsNotOwnerThenThrowNotFoundException() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));
        booking.setStatus(Status.APPROVED);

        when(userJpaRepository.existsById(any())).thenReturn(true);
        when(bookingJpaRepository.existsById(any())).thenReturn(true);
        when(bookingJpaRepository.getReferenceById(any())).thenReturn(booking);

        assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(3L, booking.getId(), true));
    }

    @Test
    void updateBookingToApprovedWhenUserIsOwnerAndStatusNotApproved() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));

        when(bookingJpaRepository.existsById(any())).thenReturn(true);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        when(bookingJpaRepository.getReferenceById(any())).thenReturn(booking);
        when(bookingJpaRepository.save(booking)).thenReturn(booking);

        BookingResponseDto bookingResponseDto =
                bookingService.updateBooking(users.get(0).getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    void updateBookingToRejectedWhenUserIsOwnerAndStatusNotApproved() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));

        when(bookingJpaRepository.existsById(any())).thenReturn(true);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        when(bookingJpaRepository.getReferenceById(any())).thenReturn(booking);
        when(bookingJpaRepository.save(booking)).thenReturn(booking);

        BookingResponseDto bookingResponseDto =
                bookingService.updateBooking(users.get(0).getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, bookingResponseDto.getStatus());
    }

    @Test
    void findBookingWhenInvalidIdsThenThrowNotFoundException() {
        when(bookingJpaRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(1L, 1L));
    }

    @Test
    void findBookingWhenUserIsNotOwnerOrBookerThenThrowNotFoundException() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));

        when(bookingJpaRepository.existsById(any())).thenReturn(true);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        when(bookingJpaRepository.getReferenceById(any())).thenReturn(booking);
        assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(3L, booking.getId()));
    }

    @Test
    void findBookingOnlyForOwnerOrBookerWhenUserIsOwnerOrBooker() {
        List<User> users = createUsers();

        Item item = createItem(users.get(0));

        Booking booking = createBooking(item, users.get(1));

        when(bookingJpaRepository.existsById(any())).thenReturn(true);
        when(userJpaRepository.existsById(any())).thenReturn(true);
        when(bookingJpaRepository.getReferenceById(any())).thenReturn(booking);
        BookingResponseDto bookingResponseDto =
                bookingService.findBooking(users.get(0).getId(), booking.getId());
        assertEquals(booking.getId(), bookingResponseDto.getId());
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

    private Item createItem(User user) {
        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Description of item");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user);
        return item;
    }

    private Booking createBooking(Item item, User user2) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user2);
        return booking;
    }

}