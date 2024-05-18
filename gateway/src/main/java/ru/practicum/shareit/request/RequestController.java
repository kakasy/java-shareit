package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID) Long userId,
                                                    @Valid @RequestBody ItemRequestDto requestDto) {

        log.info("POST-запрос: '/requests' на создание запроса пользователем с id={}", userId);

        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID) Long userId,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос: '/requests' на получение запросов пользователем с id={}", userId);

        return requestClient.getRequestsByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос: '/requests/all' на получение всех запросов " +
                "пользователем с id={} по {} запросов на странице", userId, size);

        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsById(@RequestHeader(USER_ID) Long userId,
                                                  @PathVariable Long requestId) {

        log.info("GET-запрос: '/requests/{requestId}' на получение запроса с id:{}  пользователем с id={}",
                requestId, userId);

        return requestClient.getRequestsById(userId, requestId);
    }

}
