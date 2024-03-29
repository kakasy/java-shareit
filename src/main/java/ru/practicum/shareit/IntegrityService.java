package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class IntegrityService {

    private final UserService userService;
    private final ItemService itemService;


    public boolean isUserExist(Long userId) {

        return userService.getUserDtoById(userId) != null;
    }

    public void deleteOwnerItems(Long userId) {

        itemService.deleteOwnerItems(userId);
    }
}
