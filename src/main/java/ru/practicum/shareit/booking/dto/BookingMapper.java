package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.LinkedList;
import java.util.List;

public class BookingMapper {

    public static Booking toBooking(BookingRequestDto bookingRequestDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingResponseDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingResponseDto.setStatus(booking.getStatus());

        return bookingResponseDto;
    }

    public static List<BookingResponseDto> toListBookingResponseDto(Iterable<Booking> bookingList) {
        List<BookingResponseDto> bookingResponseDtoList = new LinkedList<>();
        for (Booking booking : bookingList) {
            bookingResponseDtoList.add(BookingMapper.toBookingResponseDto(booking));
        }

        return bookingResponseDtoList;
    }

}
