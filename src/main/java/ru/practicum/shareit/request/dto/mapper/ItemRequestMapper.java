package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDtoRequest.getDescription());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public static ItemRequestDtoResponse toResponse(ItemRequest request, List<Item> items) {
        ItemRequestDtoResponse response = new ItemRequestDtoResponse();
        response.setId(request.getId());
        response.setDescription(request.getDescription());
        response.setCreated(request.getCreated());
        if (items != null) {
            response.setItems(ItemMapper.toItemDtoList(items));
        }
        return response;
    }

}
