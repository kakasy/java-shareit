package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUserDto(UserDto userDto);

    UserDto updateUserDto(UserDto userDto, Long userId);

    UserDto deleteUserDto(Long userId);

    UserDto getUserDtoById(Long userId);

    List<UserDto> getUsersDto();
}
