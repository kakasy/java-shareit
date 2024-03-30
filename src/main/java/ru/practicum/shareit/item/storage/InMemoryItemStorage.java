package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long itemIdGen = 0L;

    @Override
    public Item createItem(Item item) {

        Long currId = ++itemIdGen;
        item.setId(currId);
        items.put(item.getId(), item);

        log.info("Вещь с id={} создана", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item) {

        Item updatedItem = items.get(item.getId());
        if (updatedItem == null) {
            throw new EntityNotFoundException("Вещь не существует");
        }

        final String name = item.getName();
        if (name != null && !name.isBlank()) {
            updatedItem.setName(name);
        }

        final String description = item.getDescription();
        if (description != null && !description.isBlank()) {
            updatedItem.setDescription(description);
        }

        final Boolean available = item.getAvailable();
        if (available != null) {
            updatedItem.setAvailable(available);
        }

        return updatedItem;

    }

    @Override
    public Item deleteItem(Long itemId) {

        Item item = items.remove(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Вещь не существует");
        }
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {

        if (!items.containsKey(itemId)) {

            throw new EntityNotFoundException("Вещь не существует");
        }

        if (itemId == null) {

            throw new ValidationException("Неправильный аргумент");
        }

        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getOwnerItems(Long ownerId) {

        return new ArrayList<>(items.values())
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> validItems = new ArrayList<>();

        if (!text.isBlank()) {

            validItems = items.values()
                    .stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getDescription().toLowerCase().contains(text) ||
                            item.getName().toLowerCase().contains(text))
                    .collect(Collectors.toList());
        }
        return validItems;
    }

    @Override
    public void deleteOwnerItems(Long ownerId) {

        List<Long> itemsIdToDelete = new ArrayList<>(items.values())
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(Item::getId)
                .collect(Collectors.toList());

        for (Long id : itemsIdToDelete) {
            items.remove(id);
        }

    }
}
