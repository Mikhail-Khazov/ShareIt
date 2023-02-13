package ru.practicum.shareit.exceptions;

public class CommentValidationException extends RuntimeException {
    public CommentValidationException() {
        super();
    }

    public CommentValidationException(String message) {
        super(message);
    }

    public CommentValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
