package ru.practicum.shareit.exceptions;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException() {
        super();
    }

    public ItemNotAvailableException(String message) {
        super(message);
    }

    public ItemNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
