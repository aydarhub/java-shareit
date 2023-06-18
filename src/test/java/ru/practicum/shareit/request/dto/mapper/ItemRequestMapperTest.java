package ru.practicum.shareit.request.dto.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void testToItemRequest() {
        ItemRequestDtoRequest requestDto = new ItemRequestDtoRequest();
        requestDto.setDescription("Item Request description");

        User user = createUser();
        ItemRequest actualToRequestResult = ItemRequestMapper.toItemRequest(requestDto, user);
        assertSame(user, actualToRequestResult.getRequester());
        assertEquals("Item Request description", actualToRequestResult.getDescription());
    }

    @Test
    void testToResponse() {
        User user = createUser();

        ItemRequest itemRequest = createRequest(user);
        ArrayList<Item> itemList = new ArrayList<>();
        ItemRequestDtoResponse actualToResponseResult = ItemRequestMapper.toResponse(itemRequest, itemList);
        assertEquals(itemList, actualToResponseResult.getItems());
        assertEquals("01:01", actualToResponseResult.getCreated().toLocalTime().toString());
        assertEquals(1L, actualToResponseResult.getId());
        assertEquals("Item Request description", actualToResponseResult.getDescription());
    }

    @Test
    void testToResponse2() {
        User user = createUser();

        ItemRequest itemRequest = createRequest(user);
        ItemRequestDtoResponse actualToResponseResult = ItemRequestMapper.toResponse(itemRequest, null);
        assertEquals("01:01", actualToResponseResult.getCreated().toLocalTime().toString());
        assertEquals(1L, actualToResponseResult.getId());
        assertEquals("Item Request description", actualToResponseResult.getDescription());
    }

    @Test
    void testToResponse3() {
        User user = createUser();

        ItemRequest request = createRequest(user);

        User user1 = createUser();

        User user2 = createUser();

        ItemRequest request1 = createRequest(user2);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Item description");
        item.setId(1L);
        item.setName("Item1");
        item.setOwner(user1);
        item.setRequest(request1);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        ItemRequestDtoResponse actualToResponseResult = ItemRequestMapper.toResponse(request, itemList);
        List<ItemDto> items = actualToResponseResult.getItems();
        assertEquals(1, items.size());
        assertEquals("01:01", actualToResponseResult.getCreated().toLocalTime().toString());
        assertEquals("Item Request description", actualToResponseResult.getDescription());
        assertEquals(1L, actualToResponseResult.getId());
        ItemDto getResult = items.get(0);
        assertTrue(getResult.getAvailable());
        assertEquals(1L, getResult.getRequestId());
        assertEquals("Item1", getResult.getName());
        assertEquals(1L, getResult.getId());
        assertEquals("Item description", getResult.getDescription());
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