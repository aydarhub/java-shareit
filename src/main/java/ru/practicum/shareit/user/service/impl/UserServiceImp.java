package ru.practicum.shareit.user.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {

    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public UserDto addUser(UserRequestDto userRequestDto) {
        User user = userJpaRepository.save(UserMapper.fromUserRequestDto(userRequestDto));
        log.debug("Пользователь с id = {} добавлен", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserRequestDto userRequestDto, Long userId) {
        checkUserExistsById(userId);
        User user = userJpaRepository.getReferenceById(userId);
        Optional.ofNullable(userRequestDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userRequestDto.getEmail()).ifPresent(user::setEmail);
        User updateedUser = userJpaRepository.save(user);
        log.debug("Пользователь с id = {} обновлен", userId);
        return UserMapper.toUserDto(updateedUser);
    }

    @Override
    public UserDto findUserById(Long userId) {
        checkUserExistsById(userId);
        log.debug("Получен пользователь с id = {}", userId);
        return UserMapper.toUserDto(userJpaRepository.getReferenceById(userId));
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        checkUserExistsById(userId);
        userJpaRepository.deleteById(userId);
        log.debug("Удалён пользователь с id = {}", userId);
    }

    @Override
    public List<UserDto> findAllUsers() {
        log.debug("Найдены все пользователи");
        return UserMapper.toUserDtoList(userJpaRepository.findAll());
    }

    private void checkUserExistsById(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует", userId));
        }
    }

}
