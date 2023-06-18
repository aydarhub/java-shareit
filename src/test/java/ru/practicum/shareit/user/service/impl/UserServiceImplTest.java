package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UserServiceImpl.class})
@ExtendWith(SpringExtension.class)
class UserServiceImplTest {

    @MockBean
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void testAddUser() {
        User user = createUser();
        when(userJpaRepository.save(any())).thenReturn(user);
        UserDto actualAddUserResult = userServiceImpl.addUser(new UserRequestDto("User1", "user1@example.org"));
        assertEquals("user1@example.org", actualAddUserResult.getEmail());
        assertEquals("User1", actualAddUserResult.getName());
        assertEquals(1L, actualAddUserResult.getId());
        verify(userJpaRepository).save(any());
    }

    @Test
    void testAddUserNotFoundException() {
        when(userJpaRepository.save(any())).thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class,
                () -> userServiceImpl.addUser(new UserRequestDto("User1", "user1@example.org")));
        verify(userJpaRepository).save(any());
    }

    @Test
    void testUpdateUser() {
        User user = createUser();

        User user1 = createUser();
        when(userJpaRepository.save(any())).thenReturn(user1);
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        UserDto actualUpdateUserResult = userServiceImpl
                .updateUser(new UserRequestDto("User1", "user1@example.org"), 1L);
        assertEquals("user1@example.org", actualUpdateUserResult.getEmail());
        assertEquals("User1", actualUpdateUserResult.getName());
        assertEquals(1L, actualUpdateUserResult.getId());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(userJpaRepository).save(any());
    }

    @Test
    void testUpdateUserNotFoundException() {
        User user = createUser();
        when(userJpaRepository.save(any())).thenThrow(new NotFoundException("An error occurred"));
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class,
                () -> userServiceImpl.updateUser(new UserRequestDto("User1", "user1@example.org"), 1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
        verify(userJpaRepository).save(any());
    }

    @Test
    void testUpdateUserNotFoundEx() {
        User user = createUser();

        User user1 = createUser();
        when(userJpaRepository.save(any())).thenReturn(user1);
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> userServiceImpl.updateUser(new UserRequestDto("User1", "user1@example.org"), 1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testFindUserById() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        UserDto actualUserById = userServiceImpl.findUserById(1L);
        assertEquals("user1@example.org", actualUserById.getEmail());
        assertEquals("User1", actualUserById.getName());
        assertEquals(1L, actualUserById.getId());
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
    }

    @Test
    void testFindUserByIdNotFoundException() {
        when(userJpaRepository.getReferenceById(anyLong())).thenThrow(new NotFoundException("An error occurred"));
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> userServiceImpl.findUserById(1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).getReferenceById(anyLong());
    }

    @Test
    void testFindUserByIdNotFoundEx() {
        User user = createUser();
        when(userJpaRepository.getReferenceById(anyLong())).thenReturn(user);
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userServiceImpl.findUserById(1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testDeleteUserById() {
        doNothing().when(userJpaRepository).deleteById(anyLong());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        userServiceImpl.deleteUserById(1L);
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).deleteById(anyLong());
    }

    @Test
    void testDeleteUserByIdNotFoundException() {
        doThrow(new NotFoundException("An error occurred")).when(userJpaRepository).deleteById(anyLong());
        when(userJpaRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> userServiceImpl.deleteUserById(1L));
        verify(userJpaRepository).existsById(anyLong());
        verify(userJpaRepository).deleteById(anyLong());
    }

    @Test
    void testDeleteUserByIdNotFoundEx() {
        doNothing().when(userJpaRepository).deleteById(anyLong());
        when(userJpaRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userServiceImpl.deleteUserById(1L));
        verify(userJpaRepository).existsById(anyLong());
    }

    @Test
    void testFindUsers() {
        when(userJpaRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(userServiceImpl.findAllUsers().isEmpty());
        verify(userJpaRepository).findAll();
    }

    @Test
    void testFindUser() {
        User user = createUser();

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user);
        when(userJpaRepository.findAll()).thenReturn(userList);
        List<UserDto> actualUsers = userServiceImpl.findAllUsers();
        assertEquals(1, actualUsers.size());
        UserDto getResult = actualUsers.get(0);
        assertEquals("user1@example.org", getResult.getEmail());
        assertEquals("User1", getResult.getName());
        assertEquals(1L, getResult.getId());
        verify(userJpaRepository).findAll();
    }

    @Test
    void testFindUsersForTwoUsers() {
        User user = createUser();

        User user2 = new User();
        user2.setEmail("user2@example.org");
        user2.setId(2L);
        user2.setName("User2");

        ArrayList<User> users = new ArrayList<>();
        users.add(user2);
        users.add(user);
        when(userJpaRepository.findAll()).thenReturn(users);
        List<UserDto> actualUsers = userServiceImpl.findAllUsers();
        assertEquals(2, actualUsers.size());
        UserDto getResult = actualUsers.get(0);
        assertEquals("User2", getResult.getName());
        UserDto getResult1 = actualUsers.get(1);
        assertEquals("User1", getResult1.getName());
        assertEquals(1L, getResult1.getId());
        assertEquals("user2@example.org", getResult.getEmail());
        assertEquals(2L, getResult.getId());
        assertEquals("user1@example.org", getResult1.getEmail());
        verify(userJpaRepository).findAll();
    }

    @Test
    void testFindUsersNotFoundEx() {
        when(userJpaRepository.findAll()).thenThrow(new NotFoundException("An error occurred"));
        assertThrows(NotFoundException.class, () -> userServiceImpl.findAllUsers());
        verify(userJpaRepository).findAll();
    }

    private User createUser() {
        User user = new User();
        user.setEmail("user1@example.org");
        user.setId(1L);
        user.setName("User1");
        return user;
    }

}