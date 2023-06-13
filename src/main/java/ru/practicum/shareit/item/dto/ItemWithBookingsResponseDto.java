package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemWithBookingsResponseDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentResponseDto> comments;

}
