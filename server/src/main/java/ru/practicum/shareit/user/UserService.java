package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUserById(Long userId, UserDto userDto);

    void deleteUserById(Long userId);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();
}
