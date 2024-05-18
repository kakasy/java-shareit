package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;


    @Override
    public ItemShortDto createItem(ItemShortDto item, Long userId) {

        User user = checkUserId(userId);
        if (item.getRequestId() != null) {
            checkRequestId(item.getRequestId());
        }
        Item createdItem = itemRepository.save(ItemMapper.toItem(item, user));
        log.info("Пользователь с id {} создал вещь {}", userId, createdItem);
        return ItemMapper.toItemShortDto(createdItem);

    }

    @Override
    public ItemShortDto updateItem(ItemShortDto item, Long ownerId, Long itemId) {

        Item expectedItem = checkItemId(itemId);
        if (!expectedItem.getOwner().getId().equals(ownerId)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с id %d не является владельцем вещи с id %d", ownerId, itemId));
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            expectedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            expectedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            expectedItem.setAvailable(item.getAvailable());
        }
        itemRepository.save(expectedItem);
        log.info("Пользователь с id {} обновил вещь с id {}", ownerId, itemId);

        return ItemMapper.toItemShortDto(expectedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long itemId, Long userId) {

        Item item = checkItemId(itemId);
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        if (item.getOwner().getId().equals(userId)) {
            log.info("Получена вещь с id {}", itemId);
            return ItemMapper.toItemResponseDto(item, comments);
        }
        item.setLastBooking(null);
        item.setNextBooking(null);
        log.info("Получена вещь с id {}", itemId);

        return ItemMapper.toItemResponseDto(item, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getItemsByUser(Long ownerId, Integer from, Integer size) {

        Map<Long, Item> itemsMap = itemRepository.findAllItemsByOwnerId(ownerId, Pagination.withoutSort(from, size))
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<Comment>> commentsMap = commentRepository.findAllByItemIdIn(itemsMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(Comment::getItemId));

        return itemsMap.values()
                .stream()
                .map(item -> addComments(item, commentsMap.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemShortDto> getItemsBySearchQuery(String query, Integer from, Integer size) {

        List<ItemShortDto> foundItems = itemRepository.search(query, Pagination.withoutSort(from, size))
                .stream()
                .map(ItemMapper::toItemShortDto).collect(Collectors.toList());

        log.info("Получен список из {} вещей по запросу '{}'", foundItems.size(), query);

        return foundItems;
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentShortDto commentDto) {

        User user = checkUserId(userId);
        if (!bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())) {
            throw new BookingException("Нельзя оставить комментарий");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, itemId));
        log.info("Получен комментарий от пользователя {}", userId);

        return CommentMapper.toCommentDto(comment);
    }

    private User checkUserId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private Item checkItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id %d не существует", itemId)));
    }

    private ItemResponseDto addComments(Item item, List<Comment> comments) {

        List<CommentDto> commentList = comments
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());

        return ItemMapper.toItemResponseDto(item, commentList);
    }

    private ItemRequest checkRequestId(Long requestId) {

        return requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Запрос с id %d не существует", requestId)));
    }

}
