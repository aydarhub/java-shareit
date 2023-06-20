package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse postNewItemRequest(@RequestBody @Valid ItemRequestDtoRequest itemRequestDtoRequest,
                                                     @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestService.postNewItemRequest(itemRequestDtoRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> findItemRequestsByRequesterId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findItemRequestsByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAllItemRequests(
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestService.findAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse findItemRequestById(@PathVariable Long requestId,
                                                      @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findItemRequestById(requestId, userId);
    }

}
