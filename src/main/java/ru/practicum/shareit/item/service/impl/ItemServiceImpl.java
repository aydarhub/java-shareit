package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> itemMap = new HashMap<>();

    private final UserService userService;

    private Long id = 1L;

    @Override
    public ItemDto addItem(ItemRequestDto itemRequestDto, Long userId) {
        Item item = ItemMapper.fromItemRequestDto(itemRequestDto);
        checkUserExistsById(userId);
        item.setId(id++);
        item.setOwner(userService.getUserMap().get(userId));
        itemMap.put(item.getId(), item);
        log.debug("Добавлена новая вещь пользователем с id = {}", userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemRequestDto itemRequestDto, Long userId) {
        checkItemExistsById(itemId);
        checkUserExistsById(userId);
        checkItemOwner(itemId, userId);
        Item item = itemMap.get(itemId);
        Optional.ofNullable(itemRequestDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemRequestDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemRequestDto.getAvailable()).ifPresent(item::setAvailable);
        itemMap.put(itemId, item);
        log.debug("Обновлен предмет с id = {} пользователем с id = {}", itemId, userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        checkItemExistsById(itemId);
        log.debug("Получен предмет с id = {} пользователем с id = {}", itemId, userId);
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public List<ItemDto> findUserItems(Long userId) {
        checkUserExistsById(userId);
        List<Item> items = itemMap.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
        log.debug("Получение всех предметов пользователя с id = {}", userId);
        return ItemMapper.toItemDtoList(items);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text, Long userId) {
        if (text == null || text.isBlank()) {
            log.debug("Пустой запрос, возвращен пустой список");
            return Collections.emptyList();
        }
        text = text.toLowerCase();
        List<Item> items = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (!item.isAvailable()) {
                continue;
            }
            boolean nameContains = item.getName().toLowerCase().contains(text);
            boolean descriptionContains = item.getDescription().toLowerCase().contains(text);
            if (nameContains || descriptionContains) {
                items.add(item);
            }
        }
        log.debug("Найдены все предметы по запросу '{}'", text);
        return  ItemMapper.toItemDtoList(items);
    }

    private void checkUserExistsById(Long userId) {
        if (!userService.getUserMap().containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует", userId));

        }
    }

    private void checkItemExistsById(Long itemId) {
        if (!itemMap.containsKey(itemId)) {
            throw new NotFoundException(String.format("Предмета с id = %d не существует", itemId));
        }
    }

    private void checkItemOwner(Long itemId, Long userId) {
        if (itemMap.get(itemId).getOwner() == null || !itemMap.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("У вещи с id = %d другой владелец", itemId));
        }
    }
}
