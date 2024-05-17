package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUserDto(UserDto userDto);

    UserDto updateUserDto(UserDto userDto, Long userId);

    void deleteUserDto(Long userId);

    UserDto getUserDtoById(Long userId);

    List<UserDto> getUsersDto();
}
