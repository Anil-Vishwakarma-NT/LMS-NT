package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCourseBundleInDTOTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidInput_thenNoViolations() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(1L, 2L, true);
        Set<ConstraintViolation<UpdateCourseBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenBundleIdIsNull_thenViolation() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(null, 1L, false);
        Set<ConstraintViolation<UpdateCourseBundleInDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleId")));
    }

    @Test
    void whenCourseIdIsNull_thenViolation() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(1L, null, true);
        Set<ConstraintViolation<UpdateCourseBundleInDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenBundleIdNotPositive_thenViolation() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(0L, 2L, true);
        Set<ConstraintViolation<UpdateCourseBundleInDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleId")));
    }

    @Test
    void whenCourseIdNotPositive_thenViolation() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(1L, 0L, false);
        Set<ConstraintViolation<UpdateCourseBundleInDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenBothFieldsInvalid_thenMultipleViolations() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(null, -5L, true);
        Set<ConstraintViolation<UpdateCourseBundleInDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void testBuilderCreatesValidObject() {
        UpdateCourseBundleInDTO dto = UpdateCourseBundleInDTO.builder()
                .bundleId(1L)
                .courseId(2L)
                .isActive(true)
                .build();

        assertEquals(1L, dto.getBundleId());
        assertEquals(2L, dto.getCourseId());
        assertTrue(dto.isActive());
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO();
        dto.setBundleId(10L);
        dto.setCourseId(20L);
        dto.setActive(true);

        assertEquals(10L, dto.getBundleId());
        assertEquals(20L, dto.getCourseId());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateCourseBundleInDTO dto1 = new UpdateCourseBundleInDTO(1L, 2L, true);
        UpdateCourseBundleInDTO dto2 = new UpdateCourseBundleInDTO(1L, 2L, true);
        UpdateCourseBundleInDTO dto3 = new UpdateCourseBundleInDTO(1L, 2L, false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
    }
}

