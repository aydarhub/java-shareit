package ru.practicum.shareit.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentJpaRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;

    @Override
    public CommentResponseDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        checkItemExistsById(itemId);
        checkUserExistsById(userId);
        LocalDateTime time = LocalDateTime.now();
        checkBookingExists(userId, itemId, time);
        Comment comment = CommentMapper.toComment(commentRequestDto,
                userJpaRepository.getReferenceById(userId),
                itemJpaRepository.getReferenceById(itemId),
                time);
        return CommentMapper.toCommentResponseDto(commentJpaRepository.save(comment));
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

}
