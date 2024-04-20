package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {


    BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingDtoResponse getBookingById(Long userId, Long bookingId);

    List<BookingDtoResponse> getBookingsByCurrentUser(Long userId, BookingState bookingState);

    List<BookingDtoResponse> getBookingsForUserItems(Long ownerId, BookingState bookingState);

}
