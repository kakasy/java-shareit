package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto createItemDto(ItemDto itemDto, Long ownerId) {

        userStorage.getUserById(ownerId);
        return itemMapper.toItemDto(itemStorage.createItem(itemMapper.toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto updateItemDto(ItemDto itemDto, Long ownerId, Long itemId) {

        userStorage.getUserById(ownerId);

        itemDto.setId(itemId);

        Item itemToUpdate = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с id=" + itemId +  " не найдена"));

        if (!itemToUpdate.getOwnerId().equals(ownerId)) {
            throw new EntityNotFoundException("У пользователя такой вещи не существует");
        }
        return itemMapper.toItemDto(itemStorage.updateItem(itemMapper.toItem(itemDto, ownerId)));

    }

    @Override
    public ItemDto deleteItemDto(Long itemId, Long ownerId) {

        Item itemToDelete = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с id=" + itemId +  " не найдена"));

        if (!itemToDelete.getOwnerId().equals(ownerId)) {
            throw new EntityNotFoundException("У пользователя такой вещи не существует");
        }

        return itemMapper.toItemDto(itemStorage.deleteItem(itemId));
    }

    @Override
    public ItemDto getItemDtoById(Long itemId) {

        return itemMapper.toItemDto(itemStorage.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с id= " + itemId + " не найдена")));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {

        return itemStorage.getOwnerItems(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {

        return itemStorage.getItemsBySearchQuery(text.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOwnerItems(Long ownerId) {

        itemStorage.deleteOwnerItems(ownerId);
    }

}
