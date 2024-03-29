package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);
    User updateUser(User user);
    User deleteUser(Long userId);
    User getUserById(Long userId);
    List<User> getUsers();

}
