package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponseDto;

@Getter
@Setter
public class BookingDto {

    private Long id;
    private Long bookerId;

}
