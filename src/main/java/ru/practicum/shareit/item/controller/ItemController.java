package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestBody @Validated ItemRequestDto itemRequestDto,
                              @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на добавление вещи пользователем с id = {}", userId);
        return itemService.addItem(itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemRequestDto itemRequestDto,
                              @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на изменение вещи с id = {}", itemId);
        return itemService.updateItem(itemId, itemRequestDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId,
                                @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение вещи с id = {}", itemId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findUserItems(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение вещей пользователя с id = {}", userId);
        return itemService.findUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text,
                                           @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Запрос на поиск вещей по запросу '{}'", text);
        return itemService.searchItemsByText(text, userId);
    }
}
