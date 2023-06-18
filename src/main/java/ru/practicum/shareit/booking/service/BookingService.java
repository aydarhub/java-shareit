package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto addBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto findBooking(Long userId, Long bookingId);

    List<BookingResponseDto> findBookingsByUserId(Long userId, String state, Integer from, Integer size);

    Iterable<BookingResponseDto> findBookingsByOwnerId(Long userId, String state, Integer from, Integer size);

}
