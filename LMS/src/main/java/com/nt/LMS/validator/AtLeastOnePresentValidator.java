package com.nt.LMS.validator;

import com.nt.LMS.dto.EnrollmentDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOnePresentValidator implements ConstraintValidator<AtLeastOnePresent, EnrollmentDTO> {

    @Override
    public boolean isValid(EnrollmentDTO dto, ConstraintValidatorContext context) {
        return dto.getCourseId() != null || dto.getBundleId() != null;
    }
}
