package ru.practicum.shareit.booking.dto.constraintAnnotation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartEndConstraintValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {
    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (null != bookingDto.getStart() && null != bookingDto.getEnd())
            return bookingDto.getStart().isBefore(bookingDto.getEnd());
        return false;
    }
}
