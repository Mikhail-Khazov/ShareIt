package ru.practicum.shareit.booking.dto.constraintAnnotation;

import ru.practicum.shareit.booking.dto.BookingDtoSave;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartEndConstraintValidatorSave implements ConstraintValidator<StartBeforeEnd, BookingDtoSave> {
    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingDtoSave bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (null != bookingDto.getStart() && null != bookingDto.getEnd())
            return bookingDto.getStart().isBefore(bookingDto.getEnd());
        return false;
    }

}
