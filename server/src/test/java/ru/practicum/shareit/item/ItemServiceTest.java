package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    RequestRepository requestRepository;

    @Captor
    private ArgumentCaptor<Item> argumentCaptor;

    private Pageable pageable;

    private User user;

    private Item item;

    private final ItemShortDto itemShortDto = new ItemShortDto();


    @BeforeEach
    void startUp() {
        pageable = Pagination.withoutSort(0, 10);

        user = User.builder()
                .id(1L)
                .name("testName")
                .email("meh@mail.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("ordinary item for human beings")
                .owner(user)
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(null)
                .build();

    }

    @SneakyThrows
    @Test
    void createItem_whenUserValidAndItemRequestValid_thenReturnItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(ItemMapper.toItem(itemShortDto, user))).thenReturn(item);

        ItemShortDto actual = itemService.createItem(new ItemShortDto(), user.getId());

        assertEquals(ItemMapper.toItemShortDto(item), actual);
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(ItemMapper.toItem(itemShortDto, user));
    }

    @SneakyThrows
    @Test
    void createItem_whenInvalidUserId_thenExceptionThrown() {

        final Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(itemShortDto, userId));
        verify(itemRepository, never()).save(ItemMapper.toItem(itemShortDto, user));
        verify(requestRepository, never()).findById(itemShortDto.getRequestId());
        verify(userRepository, times(1)).findById(userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemRequestIdInvalid_thenExceptionThrown() {

        ItemShortDto itemShortDto1 = new ItemShortDto();
        itemShortDto1.setRequestId(99L);

        final Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(itemShortDto1, userId));
        verify(itemRepository, never()).save(ItemMapper.toItem(itemShortDto1, user));
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(itemShortDto1.getRequestId());
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemValid_thenReturnItem() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemShortDto itemShortDto1 = ItemShortDto.builder()
                .id(1L)
                .name("another item")
                .description("another ordinary item for human beings")
                .requestId(null)
                .available(true)
                .build();

        ItemShortDto actual = itemService.updateItem(itemShortDto1, user.getId(), item.getId());

        assertEquals(itemShortDto1, actual);
        verify(itemRepository).save(argumentCaptor.capture());

        Item actualItem = argumentCaptor.getValue();

        assertEquals("another item", actualItem.getName());
        assertEquals("another ordinary item for human beings", actualItem.getDescription());

    }

    @SneakyThrows
    @Test
    void updateItem_whenInvalidItem_thenExceptionThrown() {

        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(itemShortDto, user.getId(), item.getId()));

        verify(itemRepository, never()).save(ItemMapper.toItem(itemShortDto, user));
        verify(requestRepository, never()).findById(itemShortDto.getRequestId());
    }

    @SneakyThrows
    @Test
    void updateItem_whenNameAndDescriptionAreEmptyAndAvailableIsNull_thenReturnOldItem() {

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemShortDto newItem = ItemShortDto.builder()
                .id(1L)
                .name("")
                .description("")
                .requestId(null)
                .available(null)
                .build();

        ItemShortDto actualItemDto = itemService.updateItem(newItem, user.getId(), item.getId());
        assertNotEquals(newItem, actualItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item actualItem = argumentCaptor.getValue();

        assertEquals("item", actualItem.getName());
        assertEquals("ordinary item for human beings", actualItem.getDescription());
    }

    @SneakyThrows
    @Test
    void getItem_whenValidItemIdAndOwnerId_thenReturnOwnersItem() {

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());

        ItemResponseDto actualItem = itemService.getItemById(item.getId(), user.getId());

        assertEquals(ItemMapper.toItemResponseDto(item, List.of()), actualItem);
        verify(itemRepository, times(1)).findById(user.getId());
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
    }

    @SneakyThrows
    @Test
    void getItem_whenValidItemId_thenReturnItem() {

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());

        ItemResponseDto actualItem = itemService.getItemById(item.getId(), 99L);

        assertEquals(ItemMapper.toItemResponseDto(item, List.of()), actualItem);
        verify(itemRepository, times(1)).findById(user.getId());
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
    }

    @SneakyThrows
    @Test
    void getItemsByUser_whenValidUserId_thenReturnItemList() {

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findAllItemsByOwnerId(user.getId(), pageable)).thenReturn(items);

        when(commentRepository.findAllByItemIdIn(anySet())).thenReturn(Set.of());

        List<ItemResponseDto> actualList = itemService.getItemsByUser(user.getId(), 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(ItemMapper.toItemResponseDto(item, List.of()), actualList.get(0));
        verify(itemRepository, times(1)).findAllItemsByOwnerId(user.getId(), pageable);
    }

    @SneakyThrows
    @Test
    void getItem_whenInvalidItemId_thenExceptionThrown() {

        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(item.getId(), user.getId()));


        verify(itemRepository, times(1)).findById(item.getId());
        verify(commentRepository, never()).findAllByItemId(item.getId());
    }

    @SneakyThrows
    @Test
    void searchItems_whenTextIsValid_thenReturnItemsList() {

        String text = "item";

        when(itemRepository.search("item", pageable)).thenReturn(List.of(item));

        List<ItemShortDto> actualList = itemService.getItemsBySearchQuery(text, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(ItemMapper.toItemShortDto(item), actualList.get(0));
        verify(itemRepository, times(1)).search(text, pageable);
    }

    @SneakyThrows
    @Test
    void createComment_whenValidUserId_thenReturnComment() {

        LocalDateTime dateTime = LocalDateTime.now();
        Comment testComment = Comment.builder()
                .id(1L)
                .author(user)
                .itemId(item.getId())
                .text("ordinary comment for ordinary item from ordinary user")
                .created(dateTime)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(),
                any(LocalDateTime.class))).thenReturn(true);

        CommentDto actualComment = itemService.createComment(user.getId(), item.getId(), new CommentShortDto());

        assertEquals(CommentMapper.toCommentDto(testComment), actualComment);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @SneakyThrows
    @Test
    void createComment_whenInvalidUserId_thenReturnException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(user.getId(), item.getId(), new CommentShortDto()));

        verify(commentRepository, never()).save(new Comment());
        verify(bookingRepository, never()).existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(userRepository, times(1)).findById(user.getId());

    }
}
