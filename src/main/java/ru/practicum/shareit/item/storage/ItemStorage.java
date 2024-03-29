package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item deleteItem(Long itemId);

    Optional<Item> getItemById(Long itemId);

    List<Item> getOwnerItems(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);

    void deleteOwnerItems(Long ownerId);

}
