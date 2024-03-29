package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.IntegrityService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final IntegrityService integrityService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {

        log.info("POST-запрос: '/users' на создание пользователя");

        return userService.createUserDto(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {

        log.info("GET-запрос: '/users/{userId}' на получение пользователя c id={}", userId);

        return userService.getUserDtoById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {

        log.info("PATCH-запрос: '/users/{userId}' на обновление пользователя с id={}", userId);

        return userService.updateUserDto(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {

        log.info("DELETE-запрос: '/users/{userId}' на удаление пользователя с id={}", userId);

        UserDto userDtoToDelete = userService.deleteUserDto(userId);

        integrityService.deleteOwnerItems(userId);

        return userDtoToDelete;
    }

    @GetMapping
    public List<UserDto> getUsers() {

        log.info("GET-запрос: '/users' на получение всех пользователей");

        return userService.getUsersDto();

    }

}
