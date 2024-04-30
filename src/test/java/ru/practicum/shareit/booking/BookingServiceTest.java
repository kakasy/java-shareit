package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private User user;

    private User owner;

    private Item item;

    private Booking booking;

    private BookingDtoRequest bookingRequest;

    @BeforeEach
    void startUp() {

        user = User.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();

        userRepository.save(user);

        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        userRepository.save(owner);

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("ordinary item")
                .owner(owner)
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .build();

        bookingRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2025,1,1,11,20,0))
                .end(LocalDateTime.of(2025,1,15,13,20,0))
                .build();

        booking = new Booking(1L,LocalDateTime.of(2025,1,1,11,20,0),
                LocalDateTime.of(2025,1,15,13,20,0),
                item, user, BookingStatus.WAITING);
    }

    @Test
    void createBooking_whenValidUserIdAndItemIsAvailable_thenReturnBooking() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse actualBooking = bookingService.createBooking(user.getId(), bookingRequest);
        actualBooking.setId(1L);

        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualBooking);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void createBooking_whenInvalidUserId_thenExceptionThrown() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(user.getId(), bookingRequest));

        verify(itemRepository, never()).findById(item.getId());
        verify(bookingRepository, never()).save(booking);
        verify(userRepository, times(1)).findById(user.getId());

    }

    @Test
    void createBooking_whenInvalidUserIsOwner_thenExceptionThrown() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(BookingException.class, () -> bookingService.createBooking(owner.getId(), bookingRequest));

        verify(bookingRepository, never()).save(booking);
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).findById(item.getId());

    }

    @Test
    void createBooking_whenInvalidItemId_thenExceptionThrown() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(user.getId(), bookingRequest));


        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenExceptionThrown() {

        item.setAvailable(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(user.getId(), bookingRequest));

        verify(bookingRepository, never()).save(booking);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());

    }

    @Test
    void approvedBooking_whenOwnerAndBookingIsNotWaiting_thenReturnBooking() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse actualBookingDto = bookingService
                .approveBooking(owner.getId(),  booking.getId(), true);

        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualBookingDto);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking actualBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void approvedBooking_whenInvalidBookingId_thenExceptionThrown() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
        verify(bookingRepository, times(1)).findById(booking.getId());

    }

    @Test
    void approvedBooking_whenBookingStatusIsApproved_thenExceptionThrown() {

        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(owner.getId(),  booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
        verify(bookingRepository, times(1)).findById(booking.getId());

    }

    @Test
    void approvedBooking_whenUserIsNotOwner_thenExceptionThrown() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BookingException.class,
                () -> bookingService.approveBooking(user.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(booking);
        verify(bookingRepository, times(1)).findById(booking.getId());

    }

    @Test
    void getBookingById_whenOwnerOrBookerAndBookingExist_thenReturnBooking() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDtoResponse actualBooking = bookingService.getBookingById(owner.getId(), booking.getId());

        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualBooking);
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void getBookingById_whenInvalidUserId_thenExceptionThrown() {

        Long userId = 100L;
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BookingException.class,
                () -> bookingService.getBookingById(userId, booking.getId()));

        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void getBookingById_whenInvalidBookingId_thenExceptionThrown() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(owner.getId(), booking.getId()));

        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusAll_thenReturnBookingList() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findAllByBookerId(anyLong(), any(PageRequest.class))).thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsByCurrentUser(user.getId(), BookingState.ALL, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findAllByBookerId(user.getId(),
                Pagination.withSort(0, 10, Sort.by(Sort.Direction.DESC, "start")));
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusCurrent_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsByCurrentUser(user.getId(), BookingState.CURRENT, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusPast_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsByCurrentUser(user.getId(), BookingState.PAST, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());


    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusFuture_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsByCurrentUser(user.getId(), BookingState.FUTURE, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusWaiting_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsByCurrentUser(user.getId(), BookingState.WAITING, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusRejected_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsByCurrentUser(user.getId(), BookingState.REJECTED, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getSortBookingByUser_whenInvalidStatus_thenExceptionThrown() {

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingsByCurrentUser(user.getId(),
                        BookingState.UNSUPPORTED_STATUS, 0, 10));


        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getSortBookingByUser_whenInvalidUserId_thenReturnException() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingsByCurrentUser(user.getId(), BookingState.REJECTED, 0, 10));


        verify(userRepository, times(1)).findById(user.getId());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndBookingExist_thenReturnBooking() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItemOwnerId(anyLong(),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.ALL, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerId(owner.getId(),
                Pagination.withSort(0, 10, Sort.by(Sort.Direction.DESC, "start")));
    }

    @Test
    void getSortBookingByOwner_whenInvalidUserId_thenExceptionThrown() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingsByCurrentUser(owner.getId(), BookingState.REJECTED, 0, 10));


        verify(userRepository, times(1)).findById(owner.getId());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusAll_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        when(bookingRepository.findAllByItemOwnerId(anyLong(),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.ALL, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerId(owner.getId(),
                Pagination.withSort(0, 10, Sort.by(Sort.Direction.DESC, "start")));
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusCurrent_thenReturnBookingList() {
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.CURRENT, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusPast_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.PAST, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusFuture_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.FUTURE, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusWaiting_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.WAITING, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusRejected_thenReturnBookingList() {

        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class),
                any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList =
                bookingService.getBookingsForUserItems(owner.getId(), BookingState.REJECTED, 0, 10);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(booking), actualList.get(0));
        verify(userRepository, times(1)).findById(owner.getId());
    }

    @Test
    void getSortBookingByOwner_whenInvalidStatus_thenExceptionThrown() {

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingsForUserItems(owner.getId(),
                BookingState.UNSUPPORTED_STATUS, 0, 10));

        verifyNoInteractions(bookingRepository);
    }
}
