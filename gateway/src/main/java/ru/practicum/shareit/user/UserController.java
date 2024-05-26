package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class)
                                             @RequestBody UserDto user) {

        log.info("POST-запрос: '/users' на создание пользователя");

        return userClient.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @Validated(Update.class)
                                             @RequestBody UserDto user) {

        log.info("PATCH-запрос: '/users/{userId}' на обновление пользователя с id={}", userId);

        return userClient.updateUserById(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {

        log.info("GET-запрос: '/users/{userId}' на получение пользователя c id={}", userId);

        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {

        log.info("GET-запрос: '/users' на получение всех пользователей");

        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {

        log.info("DELETE-запрос: '/users/{userId}' на удаление пользователя с id={}", userId);

        return userClient.deleteUserById(userId);
    }

}
