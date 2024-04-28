package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@RequestHeader(USER_HEADER) Long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {

        log.info("POST-запрос: '/requests' на создание запроса пользователем с id={}", userId);

        return requestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getRequestsByOwner(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(name = "from", defaultValue = "0")
                                                       @PositiveOrZero Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос: '/requests' на получение запросов пользователем с id={}", userId);

        return requestService.getRequestsByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос: '/requests/all' на получение всех запросов " +
                "пользователем с id={} по {} запросов на странице", userId, size);

        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/requestId")
    public ItemRequestResponseDto getRequestById(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long requestId) {

        log.info("GET-запрос: '/requests/{requestId}' на получение запроса с id:{}  пользователем с id={}",
                requestId, userId);

        return requestService.getRequestById(userId, requestId);
    }

}
