package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {

        log.info("POST-запрос: '/users' на создание пользователя");

        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UserDto user) {

        log.info("PATCH-запрос: '/users/{userId}' на обновление пользователя с id={}", userId);

        return userService.updateUserById(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {

        log.info("GET-запрос: '/users/{userId}' на получение пользователя c id={}", userId);

        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {

        log.info("GET-запрос: '/users' на получение всех пользователей");

        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {

        log.info("DELETE-запрос: '/users/{userId}' на удаление пользователя с id={}", userId);

        userService.deleteUserById(userId);
    }
}
