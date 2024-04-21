package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemShortDto createItemDto(ItemShortDto itemShortDto, Long ownerId);

    ItemShortDto updateItemDto(ItemShortDto itemDto, Long ownerId, Long itemId);

    ItemResponseDto getItemDtoById(Long itemId, Long userId);

    List<ItemResponseDto> getAllOwnerItems(Long ownerId);

    List<ItemShortDto> getItemsBySearchQuery(String text);

    CommentDto createComment(Long userId, Long itemId, CommentShortDto commentShortDto);

}