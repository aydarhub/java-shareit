package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ItemRequestController.class})
@ExtendWith(SpringExtension.class)
class ItemRequestControllerTest {

    @Autowired
    private ItemRequestController itemRequestController;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void testFindAllItemRequests() throws Exception {
        when(itemRequestService.findAllItemRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/requests/all");
        MockHttpServletRequestBuilder paramResult = getResult.param("from", String.valueOf(1));
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("size", String.valueOf(1))
                .header("X-Sharer-User-Id", "2");
        MockMvcBuilders.standaloneSetup(itemRequestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testFindItemRequestById() throws Exception {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        itemRequestDtoResponse.setDescription("Item Request description");
        itemRequestDtoResponse.setId(1L);
        itemRequestDtoResponse.setItems(new ArrayList<>());
        when(itemRequestService.findItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDtoResponse);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/requests/{requestId}", 1L)
                .header("X-Sharer-User-Id", "2");
        MockMvcBuilders.standaloneSetup(itemRequestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{" +
                                "\"id\":1," +
                                "\"description\":\"Item Request description\"," +
                                "\"created\":[1,1,1,1,1]," +
                                "\"items\":[]}"));
    }

    @Test
    void testFindItemRequestsByRequesterId() throws Exception {
        when(itemRequestService.findItemRequestsByRequesterId(anyLong())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/requests")
                .header("X-Sharer-User-Id", "2");
        MockMvcBuilders.standaloneSetup(itemRequestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testPostNewItemRequest() throws Exception {
        when(itemRequestService.findItemRequestsByRequesterId(anyLong())).thenReturn(new ArrayList<>());

        ItemRequestDtoRequest itemRequestDtoRequest = new ItemRequestDtoRequest();
        itemRequestDtoRequest.setDescription("Item Request description");
        String content = (new ObjectMapper()).writeValueAsString(itemRequestDtoRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/requests")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(itemRequestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

}