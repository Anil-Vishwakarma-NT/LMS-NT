package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseBundleInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidDTO_thenNoViolations() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 10L, 20L, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenBundleIdIsNull_thenViolation() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, null, 20L, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleId")));
    }

    @Test
    void whenBundleIdIsZero_thenViolation() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 0L, 20L, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleIdIsNegative_thenViolation() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, -5L, 20L, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCourseIdIsNull_thenViolation() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 10L, null, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenCourseIdIsZero_thenViolation() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 10L, 0L, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCourseIdIsNegative_thenViolation() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 10L, -1L, true);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenIsActiveIsFalse_thenStillValid() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 10L, 20L, false);
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        CourseBundleInDTO dto = new CourseBundleInDTO();
        dto.setCourseBundleId(2L);
        dto.setBundleId(11L);
        dto.setCourseId(22L);
        dto.setActive(true);

        assertEquals(2L, dto.getCourseBundleId());
        assertEquals(11L, dto.getBundleId());
        assertEquals(22L, dto.getCourseId());
        assertTrue(dto.isActive());
    }

    @Test
    void testNoArgsConstructor() {
        CourseBundleInDTO dto = new CourseBundleInDTO();
        assertNotNull(dto);
        assertEquals(0L, dto.getCourseBundleId());
        assertNull(dto.getBundleId());
        assertNull(dto.getCourseId());
        assertFalse(dto.isActive()); // default
    }

    @Test
    void testAllArgsConstructor() {
        CourseBundleInDTO dto = new CourseBundleInDTO(3L, 101L, 202L, true);
        assertEquals(3L, dto.getCourseBundleId());
        assertEquals(101L, dto.getBundleId());
        assertEquals(202L, dto.getCourseId());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        CourseBundleInDTO dto1 = new CourseBundleInDTO(1L, 11L, 22L, true);
        CourseBundleInDTO dto2 = new CourseBundleInDTO(1L, 11L, 22L, true);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        CourseBundleInDTO dto1 = new CourseBundleInDTO(1L, 11L, 22L, true);
        CourseBundleInDTO dto2 = new CourseBundleInDTO(2L, 99L, 88L, false);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        CourseBundleInDTO dto = new CourseBundleInDTO(1L, 10L, 20L, true);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "some string");
    }
}

