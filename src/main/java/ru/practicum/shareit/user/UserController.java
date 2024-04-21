package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {

        log.info("POST-запрос: '/users' на создание пользователя");

        return userService.createUserDto(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {

        log.info("GET-запрос: '/users/{userId}' на получение пользователя c id={}", userId);

        return userService.getUserDtoById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long userId) {

        log.info("PATCH-запрос: '/users/{userId}' на обновление пользователя с id={}", userId);

        return userService.updateUserDto(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {

        log.info("DELETE-запрос: '/users/{userId}' на удаление пользователя с id={}", userId);

        userService.deleteUserDto(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {

        log.info("GET-запрос: '/users' на получение всех пользователей");

        return userService.getUsersDto();

    }

}
