package com.nt.LMS.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class EnrollmentValidator implements ConstraintValidator<ValidateEnrollmentDTO, Object> {

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        try {
            Long userId = (Long) getProperty(dto, "userId");
            Long groupId = (Long) getProperty(dto, "groupId");
            Long courseId = (Long) getProperty(dto, "courseId");
            Long bundleId = (Long) getProperty(dto, "bundleId");
            LocalDateTime deadline = (LocalDateTime) getProperty(dto, "deadline");

            boolean isUserOrGroupValid = (userId == null) ^ (groupId == null);
            boolean isCourseOrBundleValid = (courseId == null) ^ (bundleId == null);

            boolean isValidIds = isNonNegative(userId) && isNonNegative(groupId)
                    && isNonNegative(courseId) && isNonNegative(bundleId);

            boolean isDeadlineValid = deadline == null || deadline.isAfter(LocalDateTime.now());

            return isUserOrGroupValid && isCourseOrBundleValid && isValidIds && isDeadlineValid;

        } catch (Exception e) {
            return false; // If fields are not found, mark as invalid
        }
    }

    private Object getProperty(Object object, String propertyName) throws Exception {
        String getter = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method method = object.getClass().getMethod(getter);
        return method.invoke(object);
    }

    private boolean isNonNegative(Long id) {
        return id == null || id >= 0;
    }
}
