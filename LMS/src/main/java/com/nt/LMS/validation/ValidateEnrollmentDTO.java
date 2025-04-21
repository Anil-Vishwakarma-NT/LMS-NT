package com.nt.LMS.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnrollmentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateEnrollmentDTO {
    String message() default "Invalid enrollment update: specify either userId or groupId, and either courseId or bundleId";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
