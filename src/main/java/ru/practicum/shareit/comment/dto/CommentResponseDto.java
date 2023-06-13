package ru.practicum.shareit.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

}
