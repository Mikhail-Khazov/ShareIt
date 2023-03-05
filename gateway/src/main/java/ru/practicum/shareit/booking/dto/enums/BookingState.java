package ru.practicum.shareit.booking.dto.enums;

import java.util.Optional;

public enum BookingState {
    CURRENT,
    PAST,
    FUTURE,
    ALL,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String state) {
        for (BookingState value : BookingState.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
