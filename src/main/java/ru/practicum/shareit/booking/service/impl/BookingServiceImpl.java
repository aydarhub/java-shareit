package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemJpaRepository itemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;

    @Override
    @Transactional
    public BookingResponseDto addBooking(BookingRequestDto bookingRequestDto, Long userId) {
        checkBookingDate(bookingRequestDto);
        checkItemExistsById(bookingRequestDto.getItemId());
        checkUserExistsById(userId);

        Item item = itemJpaRepository.getReferenceById(bookingRequestDto.getItemId());

        checkUserIsNotOwnerItem(item, userId);
        checkIsItemAvailable(item);

        User user = userJpaRepository.getReferenceById(userId);
        Booking booking = bookingJpaRepository.save(BookingMapper.toBooking(bookingRequestDto, user, item));

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        checkBookingExistsById(bookingId);
        checkUserExistsById(userId);

        Booking booking = bookingJpaRepository.getReferenceById(bookingId);

        checkUserIsOwner(booking, userId);
        checkBookingIsNotApproved(booking);

        Status status = approved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(status);
        Booking updatedBooking = bookingJpaRepository.save(booking);

        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto findBooking(Long userId, Long bookingId) {
        checkBookingExistsById(bookingId);
        checkUserExistsById(userId);

        Booking booking = bookingJpaRepository.getReferenceById(bookingId);

        checkUserIsOwnerOrBooker(booking, userId);

        log.info("Получены данные о бронировании с id = {}", booking.getId());

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> findBookingsByUserId(Long userId, String stateStr, Integer from, Integer size) {
        checkUserExistsById(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        Pageable pageableSorted = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", stateStr));
        }
        switch (state) {
            case ALL:
                bookings = bookingJpaRepository
                        .findBookingByBookerId(
                                userId,
                                pageableSorted);
                break;
            case CURRENT:
                bookings = bookingJpaRepository
                        .findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingJpaRepository
                        .findBookingByBookerIdAndStartIsBeforeAndEndIsBefore(userId, now, now, pageableSorted);
                break;
            case FUTURE:
                bookings = bookingJpaRepository
                        .findBookingByBookerIdAndStartIsAfter(
                                userId,
                                now,
                                pageableSorted);
                break;
            case WAITING:
                bookings = bookingJpaRepository
                        .findBookingByBookerIdAndStatusEquals(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingJpaRepository
                        .findBookingByBookerIdAndStatusEquals(userId, Status.REJECTED, pageable);
                break;
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }

        return BookingMapper.toListBookingResponseDto(bookings);
    }

    @Override
    public Iterable<BookingResponseDto> findBookingsByOwnerId(Long ownerId, String stateStr, Integer from, Integer size) {
        checkUserExistsById(ownerId);

        Pageable pageable = PageRequest.of(from / size, size);
        Pageable pageableSorted = PageRequest.of(from / size, size, Sort.by("start").descending());
        Iterable<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", stateStr));
        }

        switch (state) {
            case ALL:
                bookings = bookingJpaRepository
                        .findAllByItemOwnerId(ownerId, pageableSorted);
                break;
            case CURRENT:
                bookings = bookingJpaRepository
                        .findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingJpaRepository
                        .findBookingByItemOwnerIdAndEndIsBefore(
                                ownerId,
                                now,
                                pageableSorted);
                break;
            case FUTURE:
                bookings = bookingJpaRepository
                        .findBookingByItemOwnerIdAndStartIsAfter(
                                ownerId,
                                now,
                                pageableSorted);
                break;
            case WAITING:
                bookings = bookingJpaRepository
                        .findBookingByItemOwnerIdAndStatusEquals(ownerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingJpaRepository
                        .findBookingByItemOwnerIdAndStatusEquals(ownerId, Status.REJECTED, pageable);
                break;
            default:
                throw new BadRequestException(String.format("Unknown state: %s", stateStr));
        }

        return BookingMapper.toListBookingResponseDto(bookings);
    }


    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BadRequestException("Дата старта не может быть позже даты окончания");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Некорректная дата старта");
        }
        if (bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new BadRequestException("Дата старта не может быть равной дате окончания");
        }
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

    private void checkUserIsNotOwnerItem(Item item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Арендатор не может быть владельцем вещи");
        }
    }

    private void checkIsItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }

    private void checkBookingExistsById(Long bookingId) {
        if (!bookingJpaRepository.existsById(bookingId)) {
            throw new NotFoundException(String.format("Брони с id = %d не существует", bookingId));
        }
    }

    private void checkUserIsOwner(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format(
                    "Указанный пользователь с id = %d не является владельцем вещи с id = %d",
                    userId,
                    booking.getItem().getId()));
        }
    }

    private void checkUserIsOwnerOrBooker(Booking booking, Long userId) {
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format(
                    "Указанный пользователь с id = %d не является владельцем вещи с id = %d или брони с id = %d",
                    userId,
                    booking.getItem().getId(),
                    booking.getId()));
        }
    }

    private void checkBookingIsNotApproved(Booking booking) {
        if (booking.getStatus() == Status.APPROVED) {
            throw new BadRequestException(String.format("Бронирование с id = %d уже подтверждено", booking.getId()));
        }
    }
}
