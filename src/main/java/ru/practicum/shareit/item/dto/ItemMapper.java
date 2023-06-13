package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item fromItemRequestDto(ItemRequestDto itemRequestDto) {
        Item item = new Item();
        item.setName(itemRequestDto.getName());
        item.setDescription(itemRequestDto.getDescription());
        item.setAvailable(itemRequestDto.getAvailable());

        return item;
    }

    public static List<ItemDto> toItemDtoList(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemWithBookingsResponseDto toItemWithBookingsResponseDto(Item item,
                                                                            Booking lastBooking,
                                                                            Booking nextBooking,
                                                                            List<Comment> comments) {
        ItemWithBookingsResponseDto itemWithBookingsResponseDto = new ItemWithBookingsResponseDto();

        if (lastBooking != null) {
            itemWithBookingsResponseDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemWithBookingsResponseDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }

        itemWithBookingsResponseDto.setId(item.getId());
        itemWithBookingsResponseDto.setName(item.getName());
        itemWithBookingsResponseDto.setDescription(item.getDescription());
        itemWithBookingsResponseDto.setAvailable(item.isAvailable());
        itemWithBookingsResponseDto.setComments(CommentMapper.toCommentResponseDtoList(comments));

        return itemWithBookingsResponseDto;
    }


}
