package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

public interface CommentService {

    CommentResponseDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);

}
