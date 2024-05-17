package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id=%d не существует", userId)));

        Item bookingItem = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Вещь с id=%d не существует", bookingDtoRequest.getItemId())));

        if (!bookingItem.getAvailable()) {
            throw new BookingException(String.format("Вещь с id=%d недоступна для бронирования", bookingItem.getId()));
        }

        if (bookingItem.getOwner().getId().equals(booker.getId())) {
            throw new EntityNotFoundException("Нельзя забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingDtoRequest, bookingItem, booker, BookingStatus.WAITING);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean isApproved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Нельзя подтверждать бронирование чужой вещи");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingException("Бронирование уже подтверждено");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingException(String.format("Бронирование уже %s", booking.getStatus()));
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));


        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Только владелец вещи или создатель запроса может посмотреть запрос");
        }

        return BookingMapper.toBookingDtoResponse(booking);

    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsByCurrentUser(Long userId, String stateString, Integer from,
                                                             Integer size) {

        BookingState state = BookingState.toState(stateString);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id=%d не найден", userId)));

        List<Booking> bookings = null;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, Pagination.withSort(from, size, DESC_SORT));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING,
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
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());

    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsForUserItems(Long ownerId, String stateString, Integer from,
                                                            Integer size) {

        BookingState state = BookingState.toState(stateString);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id=%d не найден", ownerId)));

        List<Booking> bookings = null;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(ownerId, Pagination.withSort(from, size, DESC_SORT));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING,
                        Pagination.withSort(from, size, DESC_SORT));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED,
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
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
