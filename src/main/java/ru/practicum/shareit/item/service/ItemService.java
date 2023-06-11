package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemRequestDto itemRequestDto, Long userId);

    ItemDto updateItem(Long itemId, ItemRequestDto itemRequestDto, Long userId);

    ItemWithBookingsResponseDto findItemById(Long itemId, Long userId);

    List<ItemWithBookingsResponseDto> findUserItems(Long userId);

    List<ItemDto> searchItemsByText(String text, Long userId);

    CommentResponseDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);

}
