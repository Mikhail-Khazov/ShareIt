package ru.practicum.shareit.exceptions;

public class WrongStateException extends RuntimeException {
    public WrongStateException() {
        super();
    }

    public WrongStateException(String message) {
        super(message);
    }

    public WrongStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
