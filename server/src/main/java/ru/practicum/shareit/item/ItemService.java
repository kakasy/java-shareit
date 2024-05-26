package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemShortDto createItem(ItemShortDto item, Long ownerId);

    ItemShortDto updateItem(ItemShortDto item, Long ownerId, Long itemId);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItemsByUser(Long ownerId, Integer from, Integer size);

    List<ItemShortDto> getItemsBySearchQuery(String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentShortDto commentShortDto);

}