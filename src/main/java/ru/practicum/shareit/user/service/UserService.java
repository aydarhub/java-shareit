package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserRequestDto userRequestDto);

    UserDto updateUser(UserRequestDto userRequestDto, Long userId);

    UserDto findUserById(Long userId);

    void deleteUserById(Long userId);

    List<UserDto> findAllUsers();
}
