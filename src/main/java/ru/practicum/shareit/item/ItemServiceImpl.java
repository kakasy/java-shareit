package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public ItemShortDto createItemDto(ItemShortDto itemShortDto, Long ownerId) {

        User itemOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + ownerId + " не существует"));

        Item createdItem = itemRepository.save(ItemMapper.toItem(itemShortDto, itemOwner));

        return ItemMapper.toItemShortDto(createdItem);

    }

    @Override
    public ItemShortDto updateItemDto(ItemShortDto itemShortDto, Long ownerId, Long itemId) {

        User itemOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + ownerId + " не существует"));

        Item itemToUpdate = itemRepository.findByIdWithUser(itemId, ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с id=" + itemId +  " не найдена"));


        if (!itemToUpdate.getOwner().getId().equals(ownerId)) {
            throw new EntityNotFoundException("У пользователя такой вещи не существует");
        }

        if (itemShortDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemShortDto.getAvailable());
        }
        if (itemShortDto.getName() != null && !itemShortDto.getName().isBlank()) {
            itemToUpdate.setName(itemShortDto.getName());
        }
        if (itemShortDto.getDescription() != null && !itemShortDto.getDescription().isBlank()) {
            itemToUpdate.setDescription(itemShortDto.getDescription());
        }

        itemRepository.save(itemToUpdate);

        return ItemMapper.toItemShortDto(itemToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemDtoById(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с id= " + itemId + " не найдена"));

        List<CommentDto> comments = commentRepository
                .findAllByItemId(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemResponseDto(item, comments);
        }
        item.setNextBooking(null);
        item.setLastBooking(null);

        return ItemMapper.toItemResponseDto(item, comments);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllOwnerItems(Long ownerId) {

        Map<Long, Item> itemsMap = itemRepository.findAllItemsByOwnerId(ownerId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));


        Map<Long, List<Comment>> commentsMap = commentRepository.findAllByItemIdIn(new ArrayList<>(itemsMap.keySet()))
                .stream()
                .collect(Collectors.groupingBy(Comment::getItemId));

        return itemsMap.values()
                .stream()
                .map(item -> addComments(item, commentsMap.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemShortDto> getItemsBySearchQuery(String text) {

        if (!text.isBlank()) {
            text = text.toLowerCase();
        }

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.getItemsBySearchQuery(text)
                .stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentShortDto commentShortDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не существует"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с id=" + itemId + " не существует"));

        Boolean isBookings = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (!isBookings) {
            throw new ValidationException("Нет бронирований вещей");
        }

        Comment comment = commentRepository.save(commentMapper.toComment(commentShortDto, user, itemId));

        return commentMapper.toCommentDto(comment);
    }

    private ItemResponseDto addComments(Item item, List<Comment> comments) {
        List<CommentDto> commentList = comments
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemResponseDto(item, commentList);
    }

}
