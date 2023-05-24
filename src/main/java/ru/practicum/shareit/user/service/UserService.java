package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto addUser(UserRequestDto userRequestDto);
    UserDto updateUser(UserRequestDto userRequestDto, Long userId);
    UserDto findUserById(Long userId);
    void deleteUserById(Long userId);
    List<UserDto> findAllUsers();
    Map<Long, User> getUserMap();
}
