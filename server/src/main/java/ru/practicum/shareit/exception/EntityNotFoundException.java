package ru.practicum.shareit.exception;

public class EntityNotFoundException  extends RuntimeException {//extends IllegalArgumentException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
