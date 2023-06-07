package ru.practicum.shareit.user.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Slf4j
@Service
public class UserServiceImp implements UserService {
    private Long id = 1L;

    @Getter
    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public UserDto addUser(UserRequestDto userRequestDto) {
        User user = UserMapper.fromUserRequestDto(userRequestDto);
        checkUniqueEmail(user.getEmail());
        user.setId(id++);
        userMap.put(user.getId(), user);
        log.debug("Пользователь с id = {} добавлен", user.getId());

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserRequestDto userRequestDto, Long userId) {
        checkUserExistsById(userId);
        checkUniqueEmail(userRequestDto.getEmail(), userId);
        User user = userMap.get(userId);
        Optional.ofNullable(userRequestDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userRequestDto.getEmail()).ifPresent(user::setEmail);
        userMap.put(userId, user);
        log.debug("Пользователь с id = {} обновлен", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserById(Long userId) {
        checkUserExistsById(userId);
        log.debug("Получен пользователь с id = {}", userId);
        return UserMapper.toUserDto(userMap.get(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserExistsById(userId);
        userMap.remove(userId);
        log.debug("Удалён пользователь с id = {}", userId);
    }

    @Override
    public List<UserDto> findAllUsers() {
        log.debug("Найдены все пользователи");
        return UserMapper.toUserDtoList(userMap.values());
    }

    private void checkUserExistsById(Long userId) {
        if (!userMap.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует", userId));
        }
    }

    private void checkUniqueEmail(String email) {
        for (User user : userMap.values()) {
            if (user.getEmail().equals(email)) {
                throw new EmailAlreadyExistException(String.format("Пользователь с таким email (%s) уже существует", email));

            }
        }
    }

    private void checkUniqueEmail(String email, Long userId) {
        for (User user : userMap.values()) {
            if (user.getEmail().equals(email)
                    && !Objects.equals(user.getId(), userId)) {
                throw new EmailAlreadyExistException(String.format("Пользователь с таким email (%s) уже существует", userId));

            }
        }
    }
}
