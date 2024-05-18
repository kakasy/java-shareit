package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(USER_HEADER) Long userId,
                                                    @RequestBody ItemRequestDto requestDto) {

        log.info("POST-запрос: '/requests' на создание запроса пользователем с id={}", userId);

        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getRequests(@RequestHeader(USER_HEADER) Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GET-запрос: '/requests' на получение запросов пользователем с id={}", userId);

        return requestService.getRequestsByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GET-запрос: '/requests/all' на получение всех запросов " +
                "пользователем с id={} по {} запросов на странице", userId, size);

        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestsById(@RequestHeader(USER_HEADER) Long userId,
                                                  @PathVariable Long requestId) {

        log.info("GET-запрос: '/requests/{requestId}' на получение запроса с id:{}  пользователем с id={}",
                requestId, userId);

        return requestService.getRequestsById(userId, requestId);
    }

}
