package com.nt.LMS.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOnePresentValidator implements ConstraintValidator<AtLeastOnePresent, EnrollmentDTO> {

    @Override
    public boolean isValid(EnrollmentDTO dto, ConstraintValidatorContext context) {
        boolean isCourseIdPresent = dto.getCourseId() != null && dto.getCourseId() > 0;
        boolean isBundleIdPresent = dto.getBundleId() != null && dto.getBundleId() > 0;

        // Return true only if one of them is present, but not both
        return (isCourseIdPresent ^ isBundleIdPresent); // XOR: one true, the other false
    }
}
