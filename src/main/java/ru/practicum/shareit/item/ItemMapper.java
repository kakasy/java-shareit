package ru.practicum.shareit.item;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.util.List;


@UtilityClass
public class ItemMapper {


    public Item toItem(ItemShortDto itemShortDto, User user) {

        return Item.builder()
                .name(itemShortDto.getName())
                .description(itemShortDto.getDescription())
                .owner(user)
                .available(itemShortDto.getAvailable())
                .requestId(itemShortDto.getRequestId())
                .build();
    }

    public ItemResponseDto toItemResponseDto(Item item, List<CommentDto> comments) {

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .lastBooking(BookingMapper.toBookingItemDto(item.getLastBooking()))
                .nextBooking(BookingMapper.toBookingItemDto(item.getNextBooking()))
                .comments(comments)
                .build();

    }

    public ItemShortDto toItemShortDto(Item item) {

        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public ItemBookingDto toItemBookingDto(Item item) {

        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}
