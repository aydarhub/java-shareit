package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
public class ItemRequestDtoRequest {

    @NotEmpty
    private String description;

}
