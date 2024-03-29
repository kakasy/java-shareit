package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);
    Item updateItem(Item item);
    Item deleteItem(Long itemId);
    Item getItemById(Long itemId);
    List<Item> getOwnerItems(Long ownerId);
    List<Item> getItemsBySearchQuery(String text);
    void deleteOwnerItems(Long ownerId);

}
