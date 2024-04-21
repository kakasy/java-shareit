package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemShortDto create(@RequestHeader(USER_HEADER) Long ownerId,
                               @Validated(Create.class) @RequestBody ItemShortDto itemDto) {

        log.info("POST-запрос: '/items' на создание вещи владельцем с id={}", ownerId);

        return itemService.createItemDto(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto update(@RequestHeader(USER_HEADER) Long ownerId,
                               @Validated(Update.class) @RequestBody ItemShortDto itemDto,
                               @PathVariable Long itemId) {

        log.info("PATCH-запрос: '/items/{itemId}' на обновление вещи с id={}", itemId);

        return itemService.updateItemDto(itemDto, ownerId, itemId);
    }


    @GetMapping("/{itemId}")
    public ItemResponseDto getItemDtoById(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long itemId) {

        log.info("GET-запрос: '/items/{itemId}' на получение вещи с id={}", itemId);

        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getOwnerItems(@RequestHeader(USER_HEADER) Long ownerId) {

        log.info("GET-запрос: '/items' на получение всех вещей владельца с id={}", ownerId);

        return itemService.getAllOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemShortDto> getItemsBySearchQuery(@RequestParam String text) {

        log.info("GET-запрос: '/items/search' на поиск вещи с текстом={}", text);

        return itemService.getItemsBySearchQuery(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentShortDto commentShortDto) {

        log.info("POST-запрос: '/{itemId}/comment' на создание комментария" +
                " пользователем с id={} для вещи с id={}, текст комментария:{}", userId, itemId, commentShortDto);

        return itemService.createComment(userId, itemId, commentShortDto);
    }
}
