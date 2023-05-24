package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemRequestDto itemRequestDto, Long userId);

    ItemDto updateItem(Long itemId, ItemRequestDto itemRequestDto, Long userId);

    ItemDto findItemById(Long itemId, Long userId);

    List<ItemDto> findUserItems(Long userId);

    List<ItemDto> searchItemsByText(String text, Long userId);
}
