package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUserDto(UserDto userDto) {

        User userToCreate = userRepository.save(UserMapper.toUser(userDto));

        return UserMapper.toUserDto(userToCreate);
    }

    @Override
    public UserDto updateUserDto(UserDto userDto, Long userId) {

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id=%d не найден", userId)));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {

            userToUpdate.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {

            userToUpdate.setEmail(userDto.getEmail());
        }

        userToUpdate.setId(userId);
        userRepository.save(userToUpdate);

        return UserMapper.toUserDto(userToUpdate);

    }

    @Override
    public void deleteUserDto(Long userId) {

        User userToDelete = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Пользователь с id=%d не найден", userId)));

        userRepository.deleteById(userToDelete.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long userId) {

        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с id=%d не найден", userId))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersDto() {

         return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
