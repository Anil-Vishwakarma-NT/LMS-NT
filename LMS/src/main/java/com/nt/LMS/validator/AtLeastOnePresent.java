package com.nt.LMS.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOnePresentValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOnePresent {
    String message() default "Only one of courseId or bundleId must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


