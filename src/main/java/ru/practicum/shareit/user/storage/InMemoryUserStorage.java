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

        final String userName = users.get(user.getId()).getName();
        final String userEmail = users.get(user.getId()).getEmail();

        if (!users.containsKey(user.getId())) {

            throw new EntityNotFoundException("Пользователь не существует");
        }

        if (user.getId() == null) {

            throw new ValidationException("Неправильный аргумент");
        }


        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(userName);
        }

        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {

            if (user.getEmail() == null || user.getEmail().isBlank()) {

                user.setEmail(userEmail);
            }

            users.put(user.getId(), user);

        } else {
            throw new UserAlreadyExistException("Пользователь с email: " + user.getEmail() + " уже существует");
        }

        return user;
    }

    @Override
    public User deleteUser(Long userId) {

        if (!users.containsKey(userId)) {

            throw new EntityNotFoundException("Пользователь не существует");
        }

        if (userId == null) {

            throw new ValidationException("Неправилььный аргумент");
        }

        return users.remove(userId);
    }

    @Override
    public Optional<User> getUserById(Long userId) {

        if (userId == null) {

            throw new ValidationException("Неправилььный аргумент");
        }

        if (!users.containsKey(userId)) {

            throw new EntityNotFoundException("Пользователь не существует");
        }

        return Optional.of(users.get(userId));

    }

    @Override
    public List<User> getUsers() {

        return new ArrayList<>(users.values());
    }
}
