package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingStorage;
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;

    private static final Sort DESC_SORT = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {

        User booker = checkUserId(userId);
        Item item = checkItemId(bookingDtoRequest.getItemId());

        if (!item.getAvailable()) {
            throw new BookingException(String.format("Вещь с id %d не доступна для бронирования", item.getId()));
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Владелец не может бронировать свои вещи");
        }

        Booking booking = BookingMapper.toBooking(bookingDtoRequest, item, booker, BookingStatus.WAITING);
        bookingStorage.save(booking);
        log.info("Пользователь с id {} забронировал вещь с id {}", userId, bookingDtoRequest.getItemId());
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean isApproved) {

        Booking booking = checkBookingId(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format(
                    "Пользователь с id %d не является владельцем вещи %d", userId, booking.getItem().getId()));
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingException(String.format("Бронирование уже %s", booking.getStatus()));
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking savedBooking = bookingStorage.save(booking);
        log.info("Владелец изменил статус бронирования с id {} на {}", bookingId, isApproved);
        return BookingMapper.toBookingDtoResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(Long userId, Long bookingId) {

        Booking booking = checkBookingId(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format(
                    "Пользователь с id %d не относится к этому бронированию", userId));
        }
        log.info("Получены данные бронирования с id {}", bookingId);
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getSortBookingByUser(Long userId, String stateStr, Integer from, Integer size) {

        BookingState state = BookingState.toState(stateStr);
        checkUserId(userId);

        List<Booking> bookings = null;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId, Pagination.withSort(from, size, DESC_SORT));
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), Pagination.withSort(from, size, DESC_SORT));
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
        }
        log.info("Получен список бронирований");
        return bookings.stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getSortBookingByOwner(Long userId, String stateStr, Integer from, Integer size) {

        BookingState state = BookingState.toState(stateStr);
        checkUserId(userId);
        List<Booking> bookings = null;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwnerId(userId, Pagination.withSort(from, size, DESC_SORT));
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), Pagination.withSort(from, size, DESC_SORT));
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
        }
        log.info("Получен список бронирований");
        return bookings.stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
    }

    private User checkUserId(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private Item checkItemId(Long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id %d не существует", itemId)));
    }

    private Booking checkBookingId(Long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирование с id %d не существует", bookingId)));
    }

}
