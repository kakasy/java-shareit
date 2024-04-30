package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private static final Sort DESC_SORT = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id= " + userId + " не существует"));

        Item bookingItem = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Вещь с id=" + bookingDtoRequest.getItemId() + " не существует"));

        if (!bookingItem.getAvailable()) {
            throw new ValidationException("Вещь с id=" + bookingItem.getId() + " недоступна для бронирования");
        }

        if (bookingItem.getOwner().getId().equals(booker.getId())) {
            throw new BookingException("Нельзя забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingDtoRequest, bookingItem, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean isApproved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingException("Нельзя подтверждать бронирование чужой вещи");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking approvedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDtoResponse(approvedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование с id=" + bookingId + " не найдено"));


        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingException("Только владелец вещи или создатель запроса может посмотреть запрос");
        }

        return BookingMapper.toBookingDtoResponse(booking);

    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsByCurrentUser(Long userId, BookingState bookingState, Integer from,
                                                             Integer size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + "не найден"));

        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, Pagination.withSort(from, size, DESC_SORT));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case APPROVED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.APPROVED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), Pagination.withSort(from, size, DESC_SORT));
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", bookingState));
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());

    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForUserItems(Long ownerId, BookingState bookingState, Integer from,
                                                            Integer size) {

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + ownerId + "не найден"));

        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(ownerId, Pagination.withSort(from, size, DESC_SORT));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case APPROVED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.APPROVED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case CANCELED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.CANCELLED,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(),
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(),
                        LocalDateTime.now(), Pagination.withSort(from, size, DESC_SORT));
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", bookingState));

        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
