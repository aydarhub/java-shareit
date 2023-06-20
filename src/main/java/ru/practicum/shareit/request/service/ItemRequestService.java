package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse postNewItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, Long userId);

    List<ItemRequestDtoResponse> findItemRequestsByRequesterId(Long userId);

    List<ItemRequestDtoResponse> findAllItemRequests(Integer from, Integer size, Long requesterId);

    ItemRequestDtoResponse findItemRequestById(Long requestId, Long userId);

}
