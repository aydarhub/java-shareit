package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
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

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setBooker(BookingResponseDto.UserDto.fromUser(booking.getBooker()));
        bookingResponseDto.setItem(BookingResponseDto.ItemDto.fromItem(booking.getItem()));
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
