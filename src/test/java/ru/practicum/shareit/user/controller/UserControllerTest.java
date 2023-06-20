package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any(), anyLong())).thenReturn(new UserDto());

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("user1@example.org");
        userRequestDto.setName("User1");
        String content = (new ObjectMapper()).writeValueAsString(userRequestDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/users/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{" +
                        "\"id\":null," +
                        "\"name\":null," +
                        "\"email\":null" +
                        "}"));
    }

    @Test
    void testFindUserById() throws Exception {
        when(userService.findUserById(anyLong())).thenReturn(new UserDto());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/{userId}", 1L);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{" +
                        "\"id\":null," +
                        "\"name\":null," +
                        "\"email\":null" +
                        "}"));
    }

    @Test
    void testFindUserByIdSecond() throws Exception {
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());
        when(userService.findUserById(anyLong())).thenReturn(new UserDto());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/{userId}", "", "Uri Variables");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testAddUser() throws Exception {
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("user1@example.org");
        userRequestDto.setName("User1");
        String content = (new ObjectMapper()).writeValueAsString(userRequestDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testDeleteUserById() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/users/{userId}", 1L);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteUserByIdSecond() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/users/{userId}", 1L);
        deleteResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(deleteResult)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testFindAllUsers() throws Exception {
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testFindAllUsersSecond() throws Exception {
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/users");
        getResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(getResult)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

}