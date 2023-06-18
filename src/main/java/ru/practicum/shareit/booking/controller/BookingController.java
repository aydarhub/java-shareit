package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                         @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingService.addBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBooking(@PathVariable Long bookingId,
                                          @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public Iterable<BookingResponseDto> findBookingsByUserId(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        return bookingService.findBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Iterable<BookingResponseDto> findBookingsByOwnerId(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        return bookingService.findBookingsByOwnerId(ownerId, state, from, size);
    }

}
