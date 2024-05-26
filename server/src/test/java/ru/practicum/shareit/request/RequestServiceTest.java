
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private ItemRequest itemRequest;

    private final Pageable pageable =
            Pagination.withSort(0, 10, Sort.by(Sort.Direction.DESC, "created"));

    private User user;


    @BeforeEach
    void startUp() {

        user = User.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("item request")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
    }

    @Test
    void createRequest_whenUserExists_thenReturnItemRequest() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestResponseDto actualItemRequest = requestService.createRequest(user.getId(), new ItemRequestDto());

        assertEquals(RequestMapper.toItemRequestResponseDto(itemRequest, List.of()), actualItemRequest);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void createRequest_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.createRequest(userId, new ItemRequestDto()));


        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, never()).save(itemRequest);
    }

    @Test
    void getRequestsByOwner_whenUserValid_thenReturnItemRequestsList() {

        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorId(1L, pageable)).thenReturn(itemRequests);

        List<ItemRequestResponseDto> actualList = requestService.getRequestsByOwner(user.getId(), 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(RequestMapper.toItemRequestResponseDto(itemRequest, List.of()), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
        verify(requestRepository, times(1)).findAllByRequestorId(user.getId(), pageable);
    }

    @Test
    void getRequestsByOwner_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestsByOwner(userId, 0, 10));

        verify(requestRepository, never()).findAllByRequestorId(userId, pageable);
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void getAllRequests_whenValidUserId_thenReturnRequestsList() {

        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdNot(1L, pageable)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequestIdIn(anySet())).thenReturn(List.of());

        List<ItemRequestResponseDto> actualList = requestService.getAllRequests(user.getId(), 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(RequestMapper.toItemRequestResponseDto(itemRequest, List.of()), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
        verify(requestRepository, times(1)).findAllByRequestorIdNot(user.getId(), pageable);
    }

    @Test
    void getAllRequests_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getAllRequests(userId, 0, 10));


        verify(requestRepository, never()).findAllByRequestorIdNot(userId, pageable);
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void getRequestsById_whenValidUserId_thenReturnItemReq() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemRequestResponseDto actualItemReq = requestService.getRequestsById(user.getId(), itemRequest.getId());

        assertEquals(RequestMapper.toItemRequestResponseDto(itemRequest, List.of()), actualItemReq);
        verify(userRepository, times(1)).findById(user.getId());
        verify(requestRepository, times(1)).findById(itemRequest.getId());
    }

    @Test
    void getRequestsById_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestsById(user.getId(), itemRequest.getId()));

        verify(requestRepository, never()).findById(itemRequest.getId());
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void getRequestsById_whenInvalidItemReqId_thenExceptionThrown() {

        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestsById(user.getId(), itemRequest.getId()));


        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(itemRequest.getId());
    }

}
