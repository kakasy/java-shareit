package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemShortDto createItem(@RequestHeader(USER_HEADER) Long ownerId, @RequestBody ItemShortDto item) {

        log.info("POST-запрос: '/items' на создание вещи владельцем с id={}", ownerId);

        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto updateItem(@RequestHeader(USER_HEADER) Long ownerId, @PathVariable Long itemId,
                                   @RequestBody ItemShortDto item) {

        log.info("PATCH-запрос: '/items/{itemId}' на обновление вещи с id={}", itemId);

        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long itemId) {

        log.info("GET-запрос: '/items/{itemId}' на получение вещи с id={}", itemId);

        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByUser(@RequestHeader(USER_HEADER) Long ownerId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GET-запрос: '/items' на получение всех вещей владельца с id={}", ownerId);

        return itemService.getItemsByUser(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemShortDto> searchItems(@RequestParam String text,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GET-запрос: '/items/search' на поиск вещи с текстом={}", text);

        return itemService.getItemsBySearchQuery(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId,
                                            @RequestHeader(USER_HEADER) Long userId,
                                            @RequestBody CommentShortDto comment) {

        log.info("POST-запрос: '/{itemId}/comment' на создание комментария" +
                " пользователем с id={} для вещи с id={}, текст комментария:{}", userId, itemId, comment);

        return itemService.createComment(itemId, userId, comment);
    }
}
