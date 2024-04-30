package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void startUp() {

        errorHandler = new ErrorHandler();
    }

    @Test
    void handleEntityNotFoundException() {

        EntityNotFoundException notFoundException = new EntityNotFoundException("Entity not found");

        ErrorResponse response = errorHandler.handleEntityNotFoundException(notFoundException);

        assertEquals(notFoundException.getMessage(), response.getError());
    }

    @Test
    void handleIllegalArgumentException() {

        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("invalid argument");

        ErrorResponse response = errorHandler.handleUserAlreadyExistException(illegalArgumentException);

        assertEquals(illegalArgumentException.getMessage(), response.getError());

    }

    @Test
    void handleValidationException() {

        ValidationException validationException = new ValidationException("not valid");

        ErrorResponse response = errorHandler.handleValidationException(validationException);

        assertEquals(validationException.getMessage(), response.getError());
    }


    @Test
    void handleBookingException() {

        BookingException bookingException = new BookingException("booking not booking");

        ErrorResponse response = errorHandler.handleBookingException(bookingException);

        assertEquals(bookingException.getMessage(), response.getError());
    }

    @Test
    void handleOthersExceptions() {

        Throwable throwable = new Throwable("something wrong");

        ErrorResponse response = errorHandler.handleOtherExceptions(throwable);

        assertEquals(throwable.getMessage(), response.getError());
    }
}
