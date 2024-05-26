package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    @Override
    public ItemRequestResponseDto createRequest(Long userId, ItemRequestDto requestDto) {

        User user = checkUserId(userId);
        ItemRequest requestDtoResponse = requestRepository.save(RequestMapper.toItemRequest(requestDto, user));
        log.info("Пользователь {} создал запрос {}", userId, requestDtoResponse.getId());

        return RequestMapper.toItemRequestResponseDto(requestDtoResponse, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getRequestsByOwner(Long ownerId, Integer from, Integer size) {

        checkUserId(ownerId);

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorId(ownerId,
                Pagination.withSort(from, size, CREATED_DESC));
        List<ItemRequestResponseDto> itemsRequest = joinItemsToItemRequest(itemRequests);
        log.info("Пользователь {} получил список своих запросов из {} элементов", ownerId, itemsRequest.size());

        return itemsRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {

        checkUserId(userId);

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdNot(userId,
                Pagination.withSort(from, size, CREATED_DESC));
        List<ItemRequestResponseDto> itemsRequest = joinItemsToItemRequest(itemRequests);
        log.info("Пользователь {} получил список всех запросов из {} элементов", userId, itemsRequest.size());

        return itemsRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getRequestsById(Long userId, Long requestId) {

        checkUserId(userId);
        ItemRequest reqItem = checkRequestId(requestId);
        List<ItemForRequestDto> items = itemRepository.findAllByRequestId(requestId);

        log.info("Пользователь {} получил запрос с id {}", userId, requestId);
        return RequestMapper.toItemRequestResponseDto(reqItem, items);
    }

    private List<ItemRequestResponseDto> joinItemsToItemRequest(List<ItemRequest> itemRequests) {
        Map<Long, ItemRequest> requests = itemRequests
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<ItemForRequestDto>> items = itemRepository.findAllByRequestIdIn(requests.keySet())
                .stream()
                .collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));

        return requests.values()
                .stream()
                .map(i -> RequestMapper.toItemRequestResponseDto(i, items.getOrDefault(i.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private User checkUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private ItemRequest checkRequestId(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Запрос с id %d не существует", requestId)));
    }
}
