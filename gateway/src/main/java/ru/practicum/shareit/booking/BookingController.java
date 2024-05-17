package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    private static final String USER_ID = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                     @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {

        log.info("POST-запрос '/bookings' на создание запроса на бронирование: {} пользователем с id: {}",
                bookingDtoRequest, userId);
        return bookingClient.bookItem(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID) Long userId, @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {

        log.info("PATCH-запрос '/bookings/{bookingId}' " +
                "на подтверждение/отклонение бронирования: {} пользователем с id: {}", bookingId, userId);

        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID) Long userId, @PathVariable Long bookingId) {

        log.info("GET-запрос '/bookings/{bookingId}' на получение данных " +
                "о бронировании с bookingId: {} пользователем с id: {}", bookingId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByCurrentUser(
            @RequestHeader(USER_ID) Long userId, @RequestParam(defaultValue = "all") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingException("Unknown state: " + stateParam));

        log.info("GET-запрос '/bookings' бронирований пользователя с id:{}, state:{}", userId, stateParam);

        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader(USER_ID) Long ownerId, @RequestParam(defaultValue = "all") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingException("Unknown state: " + stateParam));

        log.info("GET-запрос '/bookings/owner' ownerID:{}, state:{},", ownerId, stateParam);

        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }
}
