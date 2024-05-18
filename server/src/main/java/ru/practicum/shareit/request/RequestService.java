package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface RequestService {

    ItemRequestResponseDto createRequest(Long userId, ItemRequestDto requestDto);

    List<ItemRequestResponseDto> getRequestsByOwner(Long ownerId, Integer from, Integer size);

    List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestResponseDto getRequestsById(Long userId, Long requestId);

}
