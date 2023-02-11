package ru.practicum.shareit.booking.enums;

import ru.practicum.shareit.exceptions.StateNotFoundException;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus from(String state) {
        for (BookingStatus value : BookingStatus.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return value;
            }
        }
        throw new StateNotFoundException("Не удалось установить соответствие параметра " + state + " c BookingStatus");
    }
}
