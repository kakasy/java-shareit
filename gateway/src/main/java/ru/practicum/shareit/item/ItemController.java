package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID) Long userId,
                                             @Validated(Create.class) @RequestBody ItemShortDto item) {

        log.info("POST-запрос: '/items' на создание вещи владельцем с id={}", userId);

        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @Validated(Update.class) @RequestBody ItemShortDto item) {

        log.info("PATCH-запрос: '/items/{itemId}' на обновление вещи с id={}", itemId);

        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) Long userId,
                                          @PathVariable Long itemId) {

        log.info("GET-запрос: '/items/{itemId}' на получение вещи с id={}", itemId);

        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос: '/items' на получение всех вещей владельца с id={}", userId);

        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        if (text.isBlank()) {
            log.info("Получен список из 0 вещей по запросу '{}'", text);
            return ResponseEntity.ok(Collections.emptyList());
        }

        log.info("GET-запрос: '/items/search' на поиск вещи с текстом={}", text);

        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @RequestHeader(USER_ID) Long userId,
                                                @Valid @RequestBody CommentShortDto comment) {

        log.info("POST-запрос: '/{itemId}/comment' на создание комментария" +
                " пользователем с id={} для вещи с id={}, текст комментария:{}", userId, itemId, comment);

        return itemClient.createComment(itemId, userId, comment);
    }

}
