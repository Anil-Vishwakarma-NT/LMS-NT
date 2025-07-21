package com.nt.user_service_lms.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Validator for enrollment DTOs, ensuring that either userId or groupId is set (but not both),
 * either courseId or bundleId is set (but not both), all IDs are non-negative or null,
 * and the deadline (if present) is in the future.
 */
public final class EnrollmentValidator implements ConstraintValidator<ValidateEnrollmentDTO, Object> {

    /**
     * Validates the enrollment DTO according to the business rules.
     *
     * @param dto     the object to validate
     * @param context the constraint validator context
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isValid(final Object dto, final ConstraintValidatorContext context) {
        try {
            final Long userId = (Long) getProperty(dto, "userId");
            final Long groupId = (Long) getProperty(dto, "groupId");
            final Long courseId = (Long) getProperty(dto, "courseId");
            final Long bundleId = (Long) getProperty(dto, "bundleId");
            final LocalDateTime deadline = (LocalDateTime) getProperty(dto, "deadline");

            final boolean isUserOrGroupValid = (userId == null) ^ (groupId == null);
            final boolean isCourseOrBundleValid = (courseId == null) ^ (bundleId == null);

            final boolean isValidIds = isNonNegative(userId) && isNonNegative(groupId)
                    && isNonNegative(courseId) && isNonNegative(bundleId);

            final boolean isDeadlineValid = deadline == null || deadline.isAfter(LocalDateTime.now());

            return isUserOrGroupValid && isCourseOrBundleValid && isValidIds && isDeadlineValid;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves the value of a property using its getter method.
     *
     * @param object       the object from which to retrieve the property
     * @param propertyName the name of the property
     * @return the value of the property
     * @throws Exception if the getter method is not found or cannot be invoked
     */
    private Object getProperty(final Object object, final String propertyName) throws Exception {
        final String getter = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        final Method method = object.getClass().getMethod(getter);
        return method.invoke(object);
    }

    /**
     * Checks if the given ID is non-negative or null.
     *
     * @param id the ID to check
     * @return true if the ID is null or non-negative, false otherwise
     */
    private boolean isNonNegative(final Long id) {
        return id == null || id >= 0;
    }
}
