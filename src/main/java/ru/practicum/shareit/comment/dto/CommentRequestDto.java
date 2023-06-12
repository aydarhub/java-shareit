package ru.practicum.shareit.comment.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class CommentRequestDto {

    @NotEmpty
    private String text;

}
