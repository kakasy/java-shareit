package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BookingException;

public enum BookingState {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState toState(String state) {
        switch (state) {
            case "ALL":
                return BookingState.ALL;
            case "CURRENT":
                return BookingState.CURRENT;
            case "PAST":
                return BookingState.PAST;
            case "FUTURE":
                return BookingState.FUTURE;
            case "WAITING":
                return BookingState.WAITING;
            case "REJECTED":
                return BookingState.REJECTED;
            default:
                throw new BookingException(String.format("Unknown state: %s", state));
        }
    }
}
