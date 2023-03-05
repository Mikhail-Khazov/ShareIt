package ru.practicum.shareit.booking.dto.enums;

import ru.practicum.shareit.exceptions.StateNotFoundException;

public enum Sort {
    ASC,
    DESC;

    public static String from(String state) {
        for (Sort value : Sort.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return value.toString();
            }
        }
        throw new StateNotFoundException("Не удалось установить соответствие параметра сортировки: " + state);
    }
}
