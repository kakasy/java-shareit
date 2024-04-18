package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final UserMapper userMapper;


    @Override
    public UserDto createUserDto(UserDto userDto) {

        return userMapper.toUserDto(userStorage.createUser(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUserDto(UserDto userDto, Long userId) {

        userDto.setId(userId);

        return userMapper.toUserDto(userStorage.updateUser(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto deleteUserDto(Long userId) {

        itemStorage.deleteOwnerItems(userId);

        return userMapper.toUserDto(userStorage.deleteUser(userId));
    }

    @Override
    public UserDto getUserDtoById(Long userId) {

        return userMapper.toUserDto(userStorage.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден")));
    }

    @Override
    public List<UserDto> getUsersDto() {

        return userStorage.getUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
