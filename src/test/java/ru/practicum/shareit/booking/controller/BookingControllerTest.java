package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {BookingController.class})
@ExtendWith(SpringExtension.class)
class BookingControllerTest {

    @Autowired
    private BookingController bookingController;

    @MockBean
    private BookingService bookingService;

    @Test
    void testAddBooking() {
        UserDto userDto = createUserDto();
        ItemDto itemDto = createItemDto();
        BookingResponseDto bookingResponseDto = createBookingResponseDto(userDto, itemDto);
        BookingServiceImpl bookingServiceImpl = Mockito.mock(BookingServiceImpl.class);
        when(bookingServiceImpl.addBooking((BookingRequestDto) any(), (Long) any())).thenReturn(bookingResponseDto);
        BookingController bookingController = new BookingController(bookingServiceImpl);

        BookingRequestDto bookItemRequestDto = new BookingRequestDto();
        bookItemRequestDto.setEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        bookItemRequestDto.setItemId(1L);
        bookItemRequestDto.setStart(LocalDateTime.of(1, 1, 1, 1, 1));
        assertSame(bookingResponseDto, bookingController.addBooking(bookItemRequestDto, 1L));
        verify(bookingServiceImpl).addBooking((BookingRequestDto) any(), (Long) any());
    }

    @Test
    void testUpdateBooking() throws Exception {
        UserDto userDto = createUserDto();
        ItemDto itemDto = createItemDto();
        BookingResponseDto bookingResponseDto = createBookingResponseDto(userDto, itemDto);
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);
        MockHttpServletRequestBuilder patchResult = MockMvcRequestBuilders.patch("/bookings/{bookingId}", 1L);
        MockHttpServletRequestBuilder requestBuilder = patchResult.param("approved", String.valueOf(true))
                .header("X-Sharer-User-Id", "42");
        MockMvcBuilders.standaloneSetup(bookingController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{" +
                                        "\"id\":1," +
                                        "\"start\":[2023,6,17,1,1,1]," +
                                        "\"end\":[2023,6,18,1,1,1]," +
                                        "\"status\":\"WAITING\"," +
                                        "\"booker\":{\"id\":1,\"name\":\"User1\",\"email\":\"user1@mail.ru\"}," +
                                        "\"item\":{\"id\":1,\"name\":\"Item1\",\"description\":\"Item1desc\",\"available\":true,\"requestId\":null}" +
                                        "}"));
    }

    @Test
    void testFindBooking() throws Exception {
        UserDto userDto = createUserDto();
        ItemDto itemDto = createItemDto();
        BookingResponseDto bookingResponseDto = createBookingResponseDto(userDto, itemDto);

        when(bookingService.findBooking(anyLong(), anyLong())).thenReturn(bookingResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", "42");
        MockMvcBuilders.standaloneSetup(bookingController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{" +
                                "\"id\":1," +
                                "\"start\":[2023,6,17,1,1,1]," +
                                "\"end\":[2023,6,18,1,1,1]," +
                                "\"status\":\"WAITING\"," +
                                "\"booker\":{\"id\":1,\"name\":\"User1\",\"email\":\"user1@mail.ru\"}," +
                                "\"item\":{\"id\":1,\"name\":\"Item1\",\"description\":\"Item1desc\",\"available\":true,\"requestId\":null}" +
                                "}"));
    }

    @Test
    void testFindBookingsByUserId() throws Exception {
        when(bookingService.findBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/bookings");
        MockHttpServletRequestBuilder paramResult = getResult.param("from", "1");
        MockHttpServletRequestBuilder requestBuilder = paramResult
                .param("size", "1")
                .header("X-Sharer-User-Id", "2");
        MockMvcBuilders.standaloneSetup(bookingController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testFindBookingsByOwnerId() throws Exception {
        when(bookingService.findBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(mock(Iterable.class));
        MockHttpServletRequestBuilder paramResult = MockMvcRequestBuilders.get("/bookings/owner")
                .param("from", "1ug");
        MockHttpServletRequestBuilder requestBuilder = paramResult
                .param("size", "1")
                .header("X-Sharer-User-Id", "2");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(bookingController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void findBookingsByOwnerId2() throws Exception {
        when(bookingService.findBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(mock(Iterable.class));
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/bookings/owner");
        getResult.accept("ddd");
        MockHttpServletRequestBuilder paramResult = getResult.param("from", "1");
        MockHttpServletRequestBuilder requestBuilder = paramResult
                .param("size", "1")
                .header("X-Sharer-User-Id", "2");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(bookingController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(406));
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto(1L, "User1", "user1@mail.ru");
        return userDto;
    }

    private ItemDto createItemDto() {
        ItemDto itemDto = new ItemDto(1L, "Item1", "Item1desc", true, null);
        return itemDto;
    }

    private BookingResponseDto createBookingResponseDto(UserDto userDto, ItemDto itemDto) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(userDto);
        bookingResponseDto.setEnd(LocalDateTime.of(2023, Month.JUNE, 18, 1, 1, 1));
        bookingResponseDto.setId(1L);
        bookingResponseDto.setItem(itemDto);
        bookingResponseDto.setStart(LocalDateTime.of(2023, Month.JUNE, 17, 1, 1, 1));
        bookingResponseDto.setStatus(Status.WAITING);
        return bookingResponseDto;
    }

}