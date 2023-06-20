package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ItemRequestServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ItemRequestServiceImplTest {

    @MockBean
    private ItemJpaRepository itemJpaRepository;

    @MockBean
    private ItemRequestJpaRepository itemRequestJpaRepository;

    @Autowired
    private ItemRequestServiceImpl itemRequestServiceImpl;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @Test
    void testPostNewItemRequest() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);
        when(itemRequestJpaRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDtoRequest requestDto = new ItemRequestDtoRequest();
        requestDto.setDescription("Item Request description");
        ItemRequestDtoResponse actualPostNewItemRequestResult = itemRequestServiceImpl.postNewItemRequest(requestDto, 1L);
        assertEquals("01:01", actualPostNewItemRequestResult.getCreated().toLocalTime().toString());
        assertEquals(1L, actualPostNewItemRequestResult.getId());
        assertEquals("Item Request description", actualPostNewItemRequestResult.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemRequestJpaRepository).save(any());
    }

    @Test
    void testPostNewItemRequest2() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.save(any())).thenThrow(new NotFoundException("An error occurred"));

        ItemRequestDtoRequest requestDto = new ItemRequestDtoRequest();
        requestDto.setDescription("Item Request description");
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.postNewItemRequest(requestDto, 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemRequestJpaRepository).save(any());
    }

    @Test
    void testPostNewItemRequest3() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);

        User user1 = createUser();

        ItemRequest request = createRequest(user1);
        when(itemRequestJpaRepository.save(any())).thenReturn(request);

        ItemRequestDtoRequest requestDto = new ItemRequestDtoRequest();
        requestDto.setDescription("Item Request description");
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.postNewItemRequest(requestDto, 1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testPostNewItemRequest4() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user1 = createUser();
        ItemRequest request = mock(ItemRequest.class);
        when(request.getId()).thenReturn(1L);
        when(request.getDescription()).thenReturn("Item Request description");
        when(request.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(request).setCreated(any());
        doNothing().when(request).setDescription(anyString());
        doNothing().when(request).setId(anyLong());
        doNothing().when(request).setRequester(any());
        request.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        request.setDescription("Item Request description");
        request.setId(1L);
        request.setRequester(user1);
        when(itemRequestJpaRepository.save(any())).thenReturn(request);

        ItemRequestDtoRequest itemRequestDtoRequest = new ItemRequestDtoRequest();
        itemRequestDtoRequest.setDescription("Item Request description");
        ItemRequestDtoResponse actualPostNewItemRequestResult = itemRequestServiceImpl.postNewItemRequest(itemRequestDtoRequest, 1L);
        assertEquals("01:01", actualPostNewItemRequestResult.getCreated().toLocalTime().toString());
        assertEquals(1L, actualPostNewItemRequestResult.getId());
        assertEquals("Item Request description", actualPostNewItemRequestResult.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(itemRequestJpaRepository).save(any());
        verify(request).getId();
        verify(request).getDescription();
        verify(request).getCreated();
        verify(request).setCreated(any());
        verify(request).setDescription(anyString());
        verify(request).setId(anyLong());
        verify(request).setRequester(any());
    }

    @Test
    void testFindItemRequestByRequesterId() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any())).thenReturn(new ArrayList<>());
        assertTrue(itemRequestServiceImpl.findItemRequestsByRequesterId(1L).isEmpty());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).findAllByRequesterId(anyLong(), any());
    }

    @Test
    void testFindItemRequestByRequesterId2() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any()))
                .thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findItemRequestsByRequesterId(1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).findAllByRequesterId(anyLong(), any());
    }

    @Test
    void testFindItemRequestByRequesterId3() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any())).thenReturn(new ArrayList<>());
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findItemRequestsByRequesterId(1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testFindItemRequestByRequesterId4() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest itemRequest = createRequest(user);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = itemRequestServiceImpl.findItemRequestsByRequesterId(1L);
        assertEquals(1, itemRequestDtoResponseList.size());
        ItemRequestDtoResponse getResult = itemRequestDtoResponseList.get(0);
        assertEquals(itemList, getResult.getItems());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals(1L, getResult.getId());
        assertEquals("Item Request description", getResult.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findAllByRequesterId(anyLong(), any());
    }

    @Test
    void testFindItemRequestByRequesterId5() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Item1 description");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user);
        item.setRequest(itemRequest);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user2 = createUser();

        ItemRequest itemRequest1 = createRequest(user2);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest1);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> actualItemRequestByRequesterId = itemRequestServiceImpl.findItemRequestsByRequesterId(1L);
        assertEquals(1, actualItemRequestByRequesterId.size());
        ItemRequestDtoResponse getResult = actualItemRequestByRequesterId.get(0);
        List<ItemDto> items = getResult.getItems();
        assertEquals(1, items.size());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals("Item Request description", getResult.getDescription());
        assertEquals(1L, getResult.getId());
        ItemDto getResult1 = items.get(0);
        assertTrue(getResult1.getAvailable());
        assertEquals(1L, getResult1.getRequestId());
        assertEquals("Item1", getResult1.getName());
        assertEquals(1L, getResult1.getId());
        assertEquals("Item1 description", getResult1.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findAllByRequesterId(anyLong(), any());
    }

    @Test
    void testFindItemRequestByRequesterId6() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest itemRequest = createRequest(user);

        User user1 = createUser();

        ItemRequest itemRequest1 = createRequest(user1);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest1);
        requestList.add(itemRequest);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> actualItemRequestByRequesterId = itemRequestServiceImpl.findItemRequestsByRequesterId(1L);
        assertEquals(1, actualItemRequestByRequesterId.size());
        ItemRequestDtoResponse getResult = actualItemRequestByRequesterId.get(0);
        assertEquals(itemList, getResult.getItems());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals(1L, getResult.getId());
        assertEquals("Item Request description", getResult.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository, atLeast(1)).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findAllByRequesterId(anyLong(), any());
    }


    @Test
    void testFindItemRequestByRequesterId7() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest request = mock(ItemRequest.class);
        when(request.getId()).thenReturn(1L);
        when(request.getDescription()).thenReturn("Item Request description");
        when(request.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(request).setCreated(any());
        doNothing().when(request).setDescription(anyString());
        doNothing().when(request).setId(anyLong());
        doNothing().when(request).setRequester(any());
        request.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        request.setDescription("Item Request description");
        request.setId(1L);
        request.setRequester(user);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(request);
        when(itemRequestJpaRepository.findAllByRequesterId(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> actualItemRequestByRequesterId = itemRequestServiceImpl.findItemRequestsByRequesterId(1L);
        assertEquals(1, actualItemRequestByRequesterId.size());
        ItemRequestDtoResponse getResult = actualItemRequestByRequesterId.get(0);
        assertEquals(itemList, getResult.getItems());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals(1L, getResult.getId());
        assertEquals("Item Request description", getResult.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findAllByRequesterId(anyLong(), any());
        verify(request, atLeast(1)).getId();
        verify(request).getDescription();
        verify(request).getCreated();
        verify(request).setCreated(any());
        verify(request).setDescription(anyString());
        verify(request).setId(anyLong());
        verify(request).setRequester(any());
    }

    @Test
    void testFindAllItemRequests() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findOtherUserItems(anyLong(), any())).thenReturn(new ArrayList<>());
        assertTrue(itemRequestServiceImpl.findAllItemRequests(1, 3, 1L).isEmpty());
        verify(itemRequestJpaRepository).findOtherUserItems(anyLong(), any());
    }

    @Test
    void testFindAllItemRequests2() {
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest request = createRequest(user);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(request);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findOtherUserItems(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> actualAllItemRequests = itemRequestServiceImpl.findAllItemRequests(1, 3, 1L);
        assertEquals(1, actualAllItemRequests.size());
        ItemRequestDtoResponse getResult = actualAllItemRequests.get(0);
        assertEquals(itemList, getResult.getItems());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals(1L, getResult.getId());
        assertEquals("Item Request description", getResult.getDescription());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findOtherUserItems(anyLong(), any());
    }

    @Test
    void testFindAllItemRequests3() {
        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Item1 description");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user);
        item.setRequest(itemRequest);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user2 = createUser();

        ItemRequest itemRequest1 = createRequest(user2);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest1);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findOtherUserItems(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> actualAllItemRequests = itemRequestServiceImpl.findAllItemRequests(1, 3, 1L);
        assertEquals(1, actualAllItemRequests.size());
        ItemRequestDtoResponse getResult = actualAllItemRequests.get(0);
        List<ItemDto> items = getResult.getItems();
        assertEquals(1, items.size());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals("Item Request description", getResult.getDescription());
        assertEquals(1L, getResult.getId());
        ItemDto getResult1 = items.get(0);
        assertTrue(getResult1.getAvailable());
        assertEquals(1L, getResult1.getRequestId());
        assertEquals("Item1", getResult1.getName());
        assertEquals(1L, getResult1.getId());
        assertEquals("Item1 description", getResult1.getDescription());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findOtherUserItems(anyLong(), any());
    }

    @Test
    void testFindAllItemRequests4() {
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest itemRequest = mock(ItemRequest.class);
        when(itemRequest.getId()).thenReturn(1L);
        when(itemRequest.getDescription()).thenReturn("Item Request description");
        when(itemRequest.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(itemRequest).setCreated(any());
        doNothing().when(itemRequest).setDescription(anyString());
        doNothing().when(itemRequest).setId(anyLong());
        doNothing().when(itemRequest).setRequester((User) any());
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest.setDescription("Item Request description");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findOtherUserItems(anyLong(), any())).thenReturn(requestList);
        List<ItemRequestDtoResponse> actualAllItemRequests = itemRequestServiceImpl.findAllItemRequests(1, 3, 1L);
        assertEquals(1, actualAllItemRequests.size());
        ItemRequestDtoResponse getResult = actualAllItemRequests.get(0);
        assertEquals(itemList, getResult.getItems());
        assertEquals("01:01", getResult.getCreated().toLocalTime().toString());
        assertEquals(1L, getResult.getId());
        assertEquals("Item Request description", getResult.getDescription());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).findOtherUserItems(anyLong(), any());
        verify(itemRequest, atLeast(1)).getId();
        verify(itemRequest).getDescription();
        verify(itemRequest).getCreated();
        verify(itemRequest).setCreated(any());
        verify(itemRequest).setDescription(anyString());
        verify(itemRequest).setId(anyLong());
        verify(itemRequest).setRequester(any());
    }

    @Test
    void testFindAllItemRequestsArithmeticException() {
        when(itemJpaRepository.findAllByRequestId((Long) any())).thenReturn(new ArrayList<>());

        User user = createUser();

        ItemRequest itemRequest = mock(ItemRequest.class);
        when(itemRequest.getId()).thenReturn(1L);
        when(itemRequest.getDescription()).thenReturn("Item Request description");
        when(itemRequest.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(itemRequest).setCreated(any());
        doNothing().when(itemRequest).setDescription(anyString());
        doNothing().when(itemRequest).setId(anyLong());
        doNothing().when(itemRequest).setRequester(any());
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest.setDescription("Item Request description");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestJpaRepository.findOtherUserItems(anyLong(), any())).thenReturn(requestList);
        assertThrows(ArithmeticException.class, () -> itemRequestServiceImpl.findAllItemRequests(1, 0, 1L));
        verify(itemRequest).setCreated(any());
        verify(itemRequest).setDescription(anyString());
        verify(itemRequest).setId(anyLong());
        verify(itemRequest).setRequester(any());
    }

    @Test
    void testFindAllItemRequestsBadRequestException() {
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(new ArrayList<>());

        User user = createUser();

        ItemRequest itemRequest = mock(ItemRequest.class);
        when(itemRequest.getId()).thenReturn(1L);
        when(itemRequest.getDescription()).thenReturn("Item Request description");
        when(itemRequest.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(itemRequest).setCreated(any());
        doNothing().when(itemRequest).setDescription(anyString());
        doNothing().when(itemRequest).setId(anyLong());
        doNothing().when(itemRequest).setRequester(any());
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest.setDescription("Item Request description");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);

        ArrayList<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        when(itemRequestJpaRepository.findOtherUserItems(anyLong(), any())).thenReturn(requestList);
        verify(itemRequest).setCreated(any());
        verify(itemRequest).setDescription(anyString());
        verify(itemRequest).setId(anyLong());
        verify(itemRequest).setRequester(any());
    }

    @Test
    void testFindItemRequestById() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest request = createRequest(user);
        when(itemRequestJpaRepository.getReferenceById(anyLong())).thenReturn(request);
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(true);
        ItemRequestDtoResponse actualItemRequestById = itemRequestServiceImpl.findItemRequestById(1L, 1L);
        assertEquals(itemList, actualItemRequestById.getItems());
        assertEquals("01:01", actualItemRequestById.getCreated().toLocalTime().toString());
        assertEquals(1L, actualItemRequestById.getId());
        assertEquals("Item Request description", actualItemRequestById.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).getReferenceById(anyLong());
    }

    @Test
    void testFindItemRequestById2() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(new ArrayList<>());
        when(itemRequestJpaRepository.getReferenceById(anyLong()))
                .thenThrow(new NotFoundException("An error occurred"));
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findItemRequestById(1L, 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).getReferenceById(anyLong());
    }

    @Test
    void testFindItemRequestById3() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(new ArrayList<>());

        User user = createUser();

        ItemRequest itemRequest = createRequest(user);
        when(itemRequestJpaRepository.getReferenceById(anyLong())).thenReturn(itemRequest);
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findItemRequestById(1L, 1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testFindItemRequestById4() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();

        User user1 = createUser();

        ItemRequest itemRequest = createRequest(user1);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Item1 description");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user);
        item.setRequest(itemRequest);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user2 = createUser();

        ItemRequest itemRequest1 = createRequest(user2);
        when(itemRequestJpaRepository.getReferenceById(anyLong())).thenReturn(itemRequest1);
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(true);
        ItemRequestDtoResponse actualItemRequestById = itemRequestServiceImpl.findItemRequestById(1L, 1L);
        List<ItemDto> items = actualItemRequestById.getItems();
        assertEquals(1, items.size());
        assertEquals("01:01", actualItemRequestById.getCreated().toLocalTime().toString());
        assertEquals("Item Request description", actualItemRequestById.getDescription());
        assertEquals(1L, actualItemRequestById.getId());
        ItemDto getResult = items.get(0);
        assertTrue(getResult.getAvailable());
        assertEquals(1L, getResult.getRequestId());
        assertEquals("Item1", getResult.getName());
        assertEquals(1L, getResult.getId());
        assertEquals("Item1 description", getResult.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).getReferenceById(anyLong());
    }

    @Test
    void testFindItemRequestById5() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        ArrayList<Item> itemList = new ArrayList<>();
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        User user = createUser();

        ItemRequest itemRequest = mock(ItemRequest.class);
        when(itemRequest.getId()).thenReturn(1L);
        when(itemRequest.getDescription()).thenReturn("Item Request description");
        when(itemRequest.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(itemRequest).setCreated(any());
        doNothing().when(itemRequest).setDescription(anyString());
        doNothing().when(itemRequest).setId(anyLong());
        doNothing().when(itemRequest).setRequester(any());
        itemRequest.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequest.setDescription("Item Request description");
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        when(itemRequestJpaRepository.getReferenceById(anyLong())).thenReturn(itemRequest);
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(true);
        ItemRequestDtoResponse actualItemRequestById = itemRequestServiceImpl.findItemRequestById(1L, 1L);
        assertEquals(itemList, actualItemRequestById.getItems());
        assertEquals("01:01", actualItemRequestById.getCreated().toLocalTime().toString());
        assertEquals(1L, actualItemRequestById.getId());
        assertEquals("Item Request description", actualItemRequestById.getDescription());
        verify(userJpaRepository).existsById(anyLong());
        verify(itemJpaRepository).findAllByRequestId(anyLong());
        verify(itemRequestJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).getReferenceById(anyLong());
        verify(itemRequest).getId();
        verify(itemRequest).getDescription();
        verify(itemRequest).getCreated();
        verify(itemRequest).setCreated(any());
        verify(itemRequest).setDescription(anyString());
        verify(itemRequest).setId(anyLong());
        verify(itemRequest).setRequester(any());
    }

    @Test
    void testFindItemRequestByIdNotFoundException() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(new ArrayList<>());

        User user = createUser();

        ItemRequest request = mock(ItemRequest.class);
        when(request.getId()).thenReturn(1L);
        when(request.getDescription()).thenReturn("Item Request description");
        when(request.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(request).setCreated(any());
        doNothing().when(request).setDescription(anyString());
        doNothing().when(request).setId(anyLong());
        doNothing().when(request).setRequester(any());
        request.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        request.setDescription("Item Request description");
        request.setId(1L);
        request.setRequester(user);
        when(itemRequestJpaRepository.getReferenceById(anyLong())).thenReturn(request);
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findItemRequestById(1L, 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(itemRequestJpaRepository).existsById(anyLong());
        verify(request).setCreated(any());
        verify(request).setDescription(anyString());
        verify(request).setId(anyLong());
        verify(request).setRequester(any());
    }

    @Test
    void testFindItemRequestByIdBadRequestException() {
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        when(itemJpaRepository.findAllByRequestId(anyLong())).thenReturn(new ArrayList<>());

        User user = createUser();
        ItemRequest request = mock(ItemRequest.class);
        when(request.getId()).thenReturn(1L);
        when(request.getDescription()).thenReturn("Item Request description");
        when(request.getCreated()).thenReturn(LocalDateTime.of(1, 1, 1, 1, 1));
        doNothing().when(request).setCreated(any());
        doNothing().when(request).setDescription(anyString());
        doNothing().when(request).setId(anyLong());
        doNothing().when(request).setRequester(any());
        request.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        request.setDescription("Item Request description");
        request.setId(1L);
        request.setRequester(user);
        when(itemRequestJpaRepository.getReferenceById(anyLong())).thenReturn(request);
        when(itemRequestJpaRepository.existsById(anyLong())).thenReturn(true);
        verify(request).setCreated(any());
        verify(request).setDescription(anyString());
        verify(request).setId(anyLong());
        verify(request).setRequester(any());
    }

    private User createUser() {
        User user = new User();
        user.setEmail("user1@example.org");
        user.setId(1L);
        user.setName("User1");
        return user;
    }

    private ItemRequest createRequest(User user) {
        ItemRequest request = new ItemRequest();
        request.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        request.setDescription("Item Request description");
        request.setId(1L);
        request.setRequester(user);
        return request;
    }

}