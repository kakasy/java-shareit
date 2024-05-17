package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {

//    ALL,
//    WAITING,
//    CURRENT,
//    APPROVED,
//    CANCELED,
//    PAST,
//    FUTURE,
//    REJECTED,
//    UNSUPPORTED_STATUS;


    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
