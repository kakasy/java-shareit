package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;


    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private User user;

    @BeforeEach
    void startUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("mail@mail.ru")
                .build();
    }

    @Test
    void createUser_whenValidUser_thenReturnUser() {

        UserDto userDtoToSave = new UserDto();
        User savedUser = new User();

        Mockito.when(userRepository.save(UserMapper.toUser(userDtoToSave))).thenReturn(savedUser);

        UserDto actualUser = userService.createUserDto(userDtoToSave);

        assertEquals(userDtoToSave, actualUser);
        Mockito.verify(userRepository).save(UserMapper.toUser(userDtoToSave));
        Mockito.verify(userRepository, Mockito.times(1)).save(UserMapper.toUser(userDtoToSave));

    }

    @Test
    void updateUser_whenValidUser_thenReturnUser() {

        Long userId = 1L;

        User user1 = new User(userId, "meh", "e@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        UserDto newUser = UserDto.builder()
                .id(userId)
                .name("NewName")
                .email("old@mail.ru")
                .build();

        UserDto actualUserDto = userService.updateUserDto(newUser, userId);
        assertEquals(newUser, actualUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();

        assertEquals("NewName", actualUser.getName());
        assertEquals("old@mail.ru", actualUser.getEmail());
    }

    @Test
    void updateUser_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 1L;
        User user1 = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user1);

    }

    @Test
    void updateUser_whenUserEmptyNameAndEmail_thenReturnUser() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto updatedUser = UserDto.builder()
                .id(userId)
                .name("")
                .email("")
                .build();

        userService.updateUserDto(updatedUser, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User actual = userArgumentCaptor.getValue();

        assertEquals("user", actual.getName());
        assertEquals("mail@mail.ru", actual.getEmail());
    }

    @Test
    void getUserById_whenValidUserId_thenReturnUser() {

        Long userId = 1L;
        User value = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(value));

        UserDto actual = userService.getUserDtoById(userId);

        assertEquals(UserMapper.toUserDto(value), actual);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(userId));
    }

    @Test
    void getAllUsers_whenUsersListIsNotEmpty_thenReturnList() {

        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);

        List<User> expected = userRepository.findAll();

        assertEquals(expected.size(), users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_whenUsersListIsEmpty_thenReturnEmptyList() {

        List<User> users = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(users);

        List<User> expected = userRepository.findAll();

        assertEquals(expected.size(), 0);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserById_whenValidUserId_thenDeleteUser() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserDto(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserById_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserDto(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).deleteById(userId);

    }
}
