package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long ownerId,
                                 @Validated(Create.class) @RequestBody ItemShortDto itemDto) {

        log.info("POST-запрос: '/items' на создание вещи владельцем с id={}", ownerId);

        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID) Long ownerId,
                                         @Validated(Update.class) @RequestBody ItemShortDto itemDto,
                                         @PathVariable Long itemId) {

        log.info("PATCH-запрос: '/items/{itemId}' на обновление вещи с id={}", itemId);

        return itemClient.updateItem(ownerId, itemId, itemDto);
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDtoById(@RequestHeader(USER_ID) Long userId,
                                          @PathVariable Long itemId) {

        log.info("GET-запрос: '/items/{itemId}' на получение вещи с id={}", itemId);

        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(USER_ID) Long ownerId,
                                               @RequestParam(name = "from", defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("GET-запрос: '/items' на получение всех вещей владельца с id={}", ownerId);

        return itemClient.getItemsByUser(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearchQuery(@RequestParam String text,
                                                    @RequestParam(name = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        if (text.isBlank()) {
            log.info("Получен список из 0 вещей по запросу '{}'", text);
            return ResponseEntity.ok(Collections.emptyList());
        }

        log.info("GET-запрос: '/items/search' на поиск вещи с текстом={}", text);

        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentShortDto commentShortDto) {

        log.info("POST-запрос: '/{itemId}/comment' на создание комментария" +
                " пользователем с id={} для вещи с id={}, текст комментария:{}", userId, itemId, commentShortDto);

        return itemClient.createComment(itemId, userId, commentShortDto);
    }

}
