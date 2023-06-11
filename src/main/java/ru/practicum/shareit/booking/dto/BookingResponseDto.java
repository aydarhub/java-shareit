package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private UserDto booker;
    private ItemDto item;

    @Getter
    @Setter
    public static class UserDto {

        private Long id;

        public static UserDto fromUser(User user) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            return userDto;
        }

    }

    @Getter
    @Setter
    public static class ItemDto {

        private Long id;
        private String name;

        public static ItemDto fromItem(Item item) {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            return itemDto;
        }

    }

}
