package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void testFromUserRequestDto() {
        User actualFromUserRequestDtoResult = UserMapper
                .fromUserRequestDto(new UserRequestDto("User1", "user1@example.org"));
        assertEquals("user1@example.org", actualFromUserRequestDtoResult.getEmail());
        assertEquals("User1", actualFromUserRequestDtoResult.getName());
    }

    @Test
    void testToUserResponseDto() {
        User user = createUser();
        UserDto actualToUserResponseDtoResult = UserMapper.toUserDto(user);
        assertEquals("user1@example.org", actualToUserResponseDtoResult.getEmail());
        assertEquals("User1", actualToUserResponseDtoResult.getName());
        assertEquals(1L, actualToUserResponseDtoResult.getId());
    }

    @Test
    void testToUserResponseDtoList() {
        assertTrue(UserMapper.toUserDtoList(new ArrayList<>()).isEmpty());
    }

    @Test
    void testToUserResponseDtoListSecond() {
        User user = createUser();

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user);
        List<UserDto> actualToUserResponseDtoListResult = UserMapper.toUserDtoList(userList);
        assertEquals(1, actualToUserResponseDtoListResult.size());
        UserDto getResult = actualToUserResponseDtoListResult.get(0);
        assertEquals("user1@example.org", getResult.getEmail());
        assertEquals("User1", getResult.getName());
        assertEquals(1L, getResult.getId());
    }

    @Test
    void testToUserResponseDtoListThird() {
        User user = createUser();

        User user2 = new User();
        user2.setEmail("user2@example.org");
        user2.setId(2L);
        user2.setName("User2");

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user2);
        userList.add(user);
        List<UserDto> actualToUserResponseDtoListResult = UserMapper.toUserDtoList(userList);
        assertEquals(2, actualToUserResponseDtoListResult.size());
        UserDto getResult = actualToUserResponseDtoListResult.get(0);
        assertEquals("User2", getResult.getName());
        UserDto getResult1 = actualToUserResponseDtoListResult.get(1);
        assertEquals("User1", getResult1.getName());
        assertEquals(1L, getResult1.getId());
        assertEquals("user1@example.org", getResult1.getEmail());
        assertEquals(2L, getResult.getId());
        assertEquals("user2@example.org", getResult.getEmail());
    }

    private User createUser() {
        User user = new User();
        user.setEmail("user1@example.org");
        user.setId(1L);
        user.setName("User1");
        return user;
    }

}