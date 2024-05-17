package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;


    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {

        log.info("POST-запрос: '/users' на создание пользователя");

        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {

        log.info("GET-запрос: '/users/{userId}' на получение пользователя c id={}", userId);

        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Validated(Update.class) @RequestBody UserDto userDto,
                                         @PathVariable Long userId) {

        log.info("PATCH-запрос: '/users/{userId}' на обновление пользователя с id={}", userId);

        return userClient.updateUserById(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {

        log.info("DELETE-запрос: '/users/{userId}' на удаление пользователя с id={}", userId);

        userClient.deleteUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {

        log.info("GET-запрос: '/users' на получение всех пользователей");

        return userClient.getAllUsers();

    }

}
