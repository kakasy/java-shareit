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

        final String itemName = items.get(item.getId()).getName();
        final String itemDescription = items.get(item.getId()).getDescription();
        final Boolean itemAvailable = items.get(item.getId()).getAvailable();

        if (!items.containsKey(item.getId())) {

            throw new EntityNotFoundException("Вещь не существует");
        }

        if (item.getId() == null) {

            throw new ValidationException("Неверный аргумент");
        }


        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(itemName);
        }

        if (item.getDescription() == null || item.getName().isBlank()) {
            item.setDescription(itemDescription);
        }

        if (item.getAvailable() == null) {
            item.setAvailable(itemAvailable);
        }

        items.put(item.getId(), item);

        return item;

    }

    @Override
    public Item deleteItem(Long itemId) {

        if (!items.containsKey(itemId)) {

            throw new EntityNotFoundException("Вещь не существует");
        }

        if (itemId == null) {

            throw new ValidationException("Неправильный аргумент");
        }

        return items.remove(itemId);
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {

        if (!items.containsKey(itemId)) {

            throw new EntityNotFoundException("Вещь не существует");
        }

        if (itemId == null) {

            throw new ValidationException("Неправильный аргумент");
        }

        return Optional.of(items.get(itemId));
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
