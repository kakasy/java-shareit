package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) Long ownerId) {

        log.info("POST-запрос: '/items' на создание вещи владельцем с id={}", ownerId);

        return itemService.createItemDto(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId, @RequestHeader(OWNER_ID) Long ownerId) {

        log.info("PATCH-запрос: '/items/{itemId}' на обновление вещи с id={}", itemId);

        return itemService.updateItemDto(itemDto, ownerId, itemId);
    }


    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable Long itemId) {

        log.info("GET-запрос: '/items/{itemId}' на получение вещи с id={}", itemId);

        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(OWNER_ID) Long ownerId) {

        log.info("GET-запрос: '/items' на получение всех вещей владельца с id={}", ownerId);

        return itemService.getOwnerItems(ownerId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItemDto(@PathVariable Long itemId, @RequestHeader(OWNER_ID) Long ownerId) {

        log.info("DELETE-запрос: '/items/{itemId}' на удаление вещи с id={}", itemId);

        return itemService.deleteItemDto(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("GET-запрос: '/items/search' на поиск вещи с текстом={}", text);
        return itemService.getItemsBySearchQuery(text);
    }
}
