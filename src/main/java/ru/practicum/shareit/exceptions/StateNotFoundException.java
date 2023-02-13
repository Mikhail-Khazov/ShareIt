package ru.practicum.shareit.exceptions;

public class StateNotFoundException extends RuntimeException {
    public StateNotFoundException() {
        super();
    }

    public StateNotFoundException(String message) {
        super(message);
    }

    public StateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
