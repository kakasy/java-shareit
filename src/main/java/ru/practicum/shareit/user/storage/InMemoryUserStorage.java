package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long userIdGen = 0L;


    @Override
    public User createUser(User user) {

        if (users.values().stream().noneMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            if (user.getId() == null) {

                Long currId = ++userIdGen;
                user.setId(currId);
            }

            users.put(user.getId(), user);
            log.info("Пользователь с id={} создан", user.getId());

        } else {
            throw new UserAlreadyExistException("Пользователь с email: " + user.getEmail() + " уже существует");
        }

        return user;
    }

    @Override
    public User updateUser(User user) {

        User updatedUser = users.get(user.getId());
        if (updatedUser == null) {
            throw new EntityNotFoundException("Пользователь не существует");
        }
        final String name = user.getName();

        if (name != null && !name.isBlank()) {
            updatedUser.setName(name);
        }

        final String email = user.getEmail();

        if (users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .allMatch(u -> u.getId().equals(user.getId()))) {

            if (email != null && !email.isBlank()) {
                updatedUser.setEmail(email);
            }
        } else {
            throw new UserAlreadyExistException("Пользователь с email: " + email + " уже существует");
        }

        return updatedUser;
    }

    @Override
    public User deleteUser(Long userId) {

        User user = users.remove(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не существует");
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userId) {

        if (userId == null) {

            throw new ValidationException("Неправилььный аргумент");
        }

        if (!users.containsKey(userId)) {

            throw new EntityNotFoundException("Пользователь не существует");
        }

        return Optional.ofNullable(users.get(userId));

    }

    @Override
    public List<User> getUsers() {

        return new ArrayList<>(users.values());
    }
}
