package ru.practicum.shareit.booking.dto.constraintAnnotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {StartEndConstraintValidator.class, StartEndConstraintValidatorSave.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartBeforeEnd {
    String message() default "Start date cannot be after end";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
