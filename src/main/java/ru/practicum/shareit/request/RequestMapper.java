package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RequestMapper {


    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {

        return ItemRequest.builder()
                .requestor(user)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request, List<ItemForRequestDto> items) {

        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requesterId(request.getRequestor().getId())
                .items(items)
                .build();
    }
}