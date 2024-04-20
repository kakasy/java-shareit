package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUserDto(UserDto userDto) {

        try {
            return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));

        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("Пользователь с email:" + userDto.getEmail() + " уже существует");
        }

    }

    @Override
    public UserDto updateUserDto(UserDto userDto, Long userId) {

        userDto.setId(userId);

        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        final String name = userDto.getName();

        if (name != null && !name.isBlank()) {
            updatedUser.setName(name);
        }

        final String email = userDto.getEmail();

        if (userRepository.findByEmail(email)
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .allMatch(u -> u.getId().equals(userDto.getId()))) {

            if (email != null && !email.isBlank()) {
                updatedUser.setEmail(email);
            }
        } else {
            throw new UserAlreadyExistException("Пользователь с email: " + email + " уже существует");
        }
        return userMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUserDto(Long userId) {

        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Пользователь с id=" + userId + " не найден");
        }

    }

    @Override
    public UserDto getUserDtoById(Long userId) {

        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден")));
    }

    @Override
    public List<UserDto> getUsersDto() {

         return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
