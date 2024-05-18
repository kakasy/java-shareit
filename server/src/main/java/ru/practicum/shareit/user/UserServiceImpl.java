package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {

        User createdUser = userRepository.save(UserMapper.toUser(userDto));
        log.info("Создан пользователь {}", createdUser);

        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto updateUserById(Long userId, UserDto user) {
        User expectedUser = checkUserId(userId);

        if (user.getName() != null  && !user.getName().isBlank()) {
            expectedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            expectedUser.setEmail(user.getEmail());
        }
        expectedUser.setId(userId);
        userRepository.save(expectedUser);

        log.info("Обновлен пользователь с id {}", userId);
        return UserMapper.toUserDto(expectedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {

        User findedUser = checkUserId(userId);

        log.info("Получен пользователь с id {}", userId);

        return UserMapper.toUserDto(findedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {

        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Получен список из {} пользователей", users.size());

        return users;
    }

    @Override
    public void deleteUserById(Long userId) {

        checkUserId(userId);

        userRepository.deleteById(userId);

        log.info("Удален пользователь с id {}", userId);
    }

    private User checkUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

}
