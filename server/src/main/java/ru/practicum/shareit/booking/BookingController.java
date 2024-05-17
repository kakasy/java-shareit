package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;


    @PostMapping
    public BookingDtoResponse create(@RequestHeader(USER_HEADER) Long userId,
                                     @RequestBody BookingDtoRequest bookingDtoRequest) {

        log.info("POST-запрос '/bookings' на создание запроса на бронирование: {} пользователем с id: {}",
                bookingDtoRequest, userId);
        return bookingService.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {

        log.info("PATCH-запрос '/bookings/{bookingId}' " +
                "на подтверждение/отклонение бронирования: {} пользователем с id: {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId) {

        log.info("GET-запрос '/bookings/{bookingId}' на получение данных " +
                "о бронировании с bookingId: {} пользователем с id: {}", bookingId, userId);

        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookingsByCurrentUser(
            @RequestHeader(USER_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GET-запрос '/bookings' бронирований пользователя с id:{}, state:{}", userId, state);

        return bookingService.getBookingsByCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingsFortUserItems(
            @RequestHeader(USER_HEADER) Long ownerId, @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GET-запрос '/bookings/owner' ownerID:{}, state:{},", ownerId, state);

        return bookingService.getBookingsForUserItems(ownerId, state, from, size);
    }
}
