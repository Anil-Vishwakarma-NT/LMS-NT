package com.nt.user_service_lms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for EnrollmentDTO.
 * Ensures that either userId or groupId, and either courseId or bundleId are specified.
 */
@Documented
@Constraint(validatedBy = EnrollmentValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateEnrollmentDTO {
    /**
     * The error message to be returned when validation fails.
     *
     * @return the error message
     */
    String message() default "Invalid enrollment update: specify either userId"
            + " or groupId, and either courseId or bundleId";

    /**
     * Allows specification of validation groups.
     *
     * @return the groups for validation
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients to assign custom payload objects to a constraint.
     *
     * @return the payload type for clients to specify
     */
    Class<? extends Payload>[] payload() default {};
}
