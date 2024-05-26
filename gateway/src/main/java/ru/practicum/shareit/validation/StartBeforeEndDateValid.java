package ru.practicum.shareit.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface StartBeforeEndDateValid {
    String message() default "Начало бронирования не может быть позже его окончания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}