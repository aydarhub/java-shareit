package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public ItemDto addItem(ItemRequestDto itemRequestDto, Long userId) {
        Item item = ItemMapper.fromItemRequestDto(itemRequestDto);
        checkUserExistsById(userId);
        item.setOwner(userJpaRepository.getReferenceById(userId));
        log.debug("Добавлена новая вещь пользователем с id = {}", userId);
        return ItemMapper.toItemDto(itemJpaRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemRequestDto itemRequestDto, Long userId) {
        checkItemExistsById(itemId);
        checkUserExistsById(userId);
        checkItemOwner(itemId, userId);

        Item item = itemJpaRepository.getReferenceById(itemId);
        Optional.ofNullable(itemRequestDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemRequestDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemRequestDto.getAvailable()).ifPresent(item::setAvailable);
        log.debug("Обновлен предмет с id = {} пользователем с id = {}", itemId, userId);
        return ItemMapper.toItemDto(itemJpaRepository.save(item));
    }

    @Override
    public ItemWithBookingsResponseDto findItemById(Long itemId, Long userId) {
        checkItemExistsById(itemId);
        log.debug("Получен предмет с id = {} пользователем с id = {}", itemId, userId);
        Item item = itemJpaRepository.getReferenceById(itemId);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingJpaRepository
                    .findFirstByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now().plusHours(1))
                    .orElse(null);
            if (lastBooking != null && lastBooking.getStatus() == Status.REJECTED) {
                lastBooking = null;
            }
            Booking nextBooking = bookingJpaRepository
                    .findFirstByItemIdAndStartIsAfterOrderByStart(item.getId(), LocalDateTime.now())
                    .orElse(null);
            if (nextBooking != null && nextBooking.getStatus() == Status.REJECTED) {
                nextBooking = null;
            }
            return ItemMapper.toItemWithBookingsResponseDto(item,
                    lastBooking,
                    nextBooking,
                    commentJpaRepository.findAllByItemId(itemId));
        }
        return ItemMapper.toItemWithBookingsResponseDto(item,
                null,
                null,
                commentJpaRepository.findAllByItemId(itemId));
    }

    @Override
    public List<ItemWithBookingsResponseDto> findUserItems(Long userId) {
        checkUserExistsById(userId);
        log.debug("Получение всех предметов пользователя с id = {}", userId);
        List<Item> items = itemJpaRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> lastBookings = bookingJpaRepository
                .findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(itemIds, LocalDateTime.now());
        List<Booking> nextBookings = bookingJpaRepository
                .findFirstByItemIdInAndStartIsAfterOrderByStart(itemIds, LocalDateTime.now());
        Map<Long, Booking> lastBookingMap = lastBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, Booking> nextBookingMap = nextBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, List<Comment>> commentMap = commentJpaRepository
                .findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        List<ItemWithBookingsResponseDto> itemWithBookingsResponseDtoList = new ArrayList<>();
        for (Item item : items) {
            itemWithBookingsResponseDtoList.add(ItemMapper.toItemWithBookingsResponseDto(item,
                    lastBookingMap.get(item.getId()),
                    nextBookingMap.get(item.getId()),
                    commentMap.getOrDefault(item.getId(), Collections.emptyList())));
        }

        return itemWithBookingsResponseDtoList;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text, Long userId) {
        if (text == null || text.isBlank()) {
            log.debug("Пустой запрос, возвращен пустой список");
            return Collections.emptyList();
        }
        List<Item> items = itemJpaRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable(text,
                text,
                true);
        log.debug("Найдены все предметы по запросу '{}'", text);
        return ItemMapper.toItemDtoList(items);
    }

    private void checkUserExistsById(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует", userId));

        }
    }

    private void checkItemExistsById(Long itemId) {
        if (!itemJpaRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("Предмета с id = %d не существует", itemId));
        }
    }

    private void checkBookingExists(Long userId, Long itemId, LocalDateTime time) {
        if (!bookingJpaRepository.existsByBookerIdAndItemIdAndEndIsBefore(userId, itemId, time)) {
            throw new BadRequestException(
                    String.format("Бронирование вещи с id = %d пользователем с id = %d не найдено", itemId, userId));
        }
    }

    private void checkItemOwner(Long itemId, Long userId) {
        Item item = itemJpaRepository.getReferenceById(itemId);
        if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("У вещи с id = %d другой владелец", itemId));
        }
    }
}
