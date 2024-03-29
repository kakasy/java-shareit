package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItemDto(ItemDto itemDto, Long ownerId);
    ItemDto updateItemDto(ItemDto itemDto, Long ownerId, Long itemId);
    ItemDto deleteItemDto(Long itemId, Long ownerId);
    ItemDto getItemDtoById(Long itemId);
    List<ItemDto> getOwnerItems(Long ownerId);
    List<ItemDto> getItemsBySearchQuery(String text);
    void deleteOwnerItems(Long ownerId);

}
