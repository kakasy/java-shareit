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

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    @Override
    public ItemRequestResponseDto createRequest(Long userId, ItemRequestDto itemRequestDto) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        ItemRequest itemRequest = requestRepository.save(RequestMapper.toItemRequest(itemRequestDto, requester));

        return RequestMapper.toItemRequestResponseDto(itemRequest, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getRequestsByOwner(Long ownerId, Integer from, Integer size) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + ownerId + " не найден"));

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorId(ownerId,
                Pagination.withSort(from, size, CREATED_DESC));

        return convertItemRequestsToItemRequestResponseDto(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId,
                Pagination.withSort(from, size, CREATED_DESC));

        return convertItemRequestsToItemRequestResponseDto(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getRequestsById(Long userId, Long requestId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос с id=" + requestId + " не найден"));

        List<ItemForRequestDto> items = itemRepository.findAllByRequestId(requestId);

        return RequestMapper.toItemRequestResponseDto(itemRequest, items);

    }

    private List<ItemRequestResponseDto> convertItemRequestsToItemRequestResponseDto(List<ItemRequest> itemRequests) {

        Map<Long, ItemRequest> requests = itemRequests
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<ItemForRequestDto>> items =
                itemRepository.findAllByRequestIdIn(requests.keySet())
                        .stream()
                        .collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));

        return requests.values()
                .stream()
                .map(itemRequest -> RequestMapper.toItemRequestResponseDto(itemRequest, items.getOrDefault(itemRequest.getId(),
                        List.of())))
                .collect(Collectors.toList());
    }
}
