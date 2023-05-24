package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Validated UserRequestDto userRequestDto) {
        log.info("Запрос на добавление пользователя");
        return userService.addUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserRequestDto userRequestDto,
                              @PathVariable Long userId) {
        log.info("Запрос на изменение пользователя с id = {}", userId);
        return userService.updateUser(userRequestDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с id = {}", userId);
        return userService.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Удаление пользователя по id = {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAllUsers();
    }
}
