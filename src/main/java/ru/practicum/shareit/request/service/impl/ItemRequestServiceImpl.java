package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final ItemRequestJpaRepository itemRequestJpaRepository;

    @Override
    public ItemRequestDtoResponse postNewItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, Long userId) {
        checkUserExistsById(userId);
        User user = userJpaRepository.getReferenceById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user);
        ItemRequest savedItemRequest = itemRequestJpaRepository.save(itemRequest);

        return ItemRequestMapper.toResponse(savedItemRequest, null);
    }

    @Override
    public List<ItemRequestDtoResponse> findItemRequestsByRequesterId(Long userId) {
        checkUserExistsById(userId);
        List<ItemRequest> itemRequests = itemRequestJpaRepository.findAllByRequesterId(
                userId,
                Sort.by("created").descending());
        List<List<Item>> items = itemRequests.stream()
                .map(itemRequest -> itemJpaRepository
                        .findAllByRequestId(itemRequest.getId()))
                .collect(Collectors.toList());
        List<ItemRequestDtoResponse> itemRequestDtoResponses = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            for (List<Item> itemList : items) {
                itemRequestDtoResponses.add(ItemRequestMapper.toResponse(itemRequest, itemList));
                break;
            }
            break;
        }

        return itemRequestDtoResponses;
    }

    @Override
    public List<ItemRequestDtoResponse> findAllItemRequests(Integer from, Integer size, Long requesterId) {
        checkUserExistsById(requesterId);
        List<ItemRequest> itemRequests = itemRequestJpaRepository
                .findOtherUserItems(requesterId, PageRequest.of(from / size,
                        size,
                        Sort.by("created").descending()));
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemJpaRepository.findAllByRequestId(itemRequest.getId());
            itemRequestDtoResponseList.add(ItemRequestMapper.toResponse(itemRequest, items));
        }

        return itemRequestDtoResponseList;
    }

    @Override
    public ItemRequestDtoResponse findItemRequestById(Long requestId, Long userId) {
        checkUserExistsById(userId);
        checkItemRequestExistsById(requestId);
        ItemRequest itemRequest = itemRequestJpaRepository.getReferenceById(requestId);
        List<Item> items = itemJpaRepository.findAllByRequestId(requestId);

        return ItemRequestMapper.toResponse(itemRequest, items);
    }

    private void checkUserExistsById(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует", userId));

        }
    }

    private void checkItemRequestExistsById(Long requestId) {
        if (!itemRequestJpaRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Запроса с id = %d не существует", requestId));
        }
    }

}
