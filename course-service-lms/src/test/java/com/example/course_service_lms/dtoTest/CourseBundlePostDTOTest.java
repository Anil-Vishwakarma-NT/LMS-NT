package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.inDTO.CourseBundleInDTO;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseBundleInDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidDTO() {
        // Given
        CourseBundleInDTO postDTO = new CourseBundleInDTO(1L, 101L, 201L);

        // When
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(postDTO);

        // Then
        assertTrue(violations.isEmpty(), "Valid DTO should not trigger any constraint violations");
    }

    @Test
    void testInvalidBundleId() {
        // Given
        CourseBundleInDTO postDTO = new CourseBundleInDTO(1L, -101L, 201L);

        // When
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(postDTO);

        // Then
        assertFalse(violations.isEmpty(), "Negative BundleId should trigger a violation");
    }

    @Test
    void testInvalidCourseId() {
        // Given
        CourseBundleInDTO postDTO = new CourseBundleInDTO(1L, 101L, -201L);

        // When
        Set<ConstraintViolation<CourseBundleInDTO>> violations = validator.validate(postDTO);

        // Then
        assertFalse(violations.isEmpty(), "Negative CourseId should trigger a violation");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        CourseBundleInDTO dto1 = new CourseBundleInDTO(1L, 101L, 201L);
        CourseBundleInDTO dto2 = new CourseBundleInDTO(1L, 101L, 201L);
        CourseBundleInDTO dto3 = new CourseBundleInDTO(2L, 102L, 202L);

        // Then
        assertEquals(dto1, dto2, "DTO objects with the same attributes should be equal");
        assertNotEquals(dto1, dto3, "DTO objects with different attributes should not be equal");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Equal DTO objects should have the same hash code");
        assertNotEquals(dto1.hashCode(), dto3.hashCode(), "Unequal DTO objects should have different hash codes");
    }

    @Test
    void testNoArgsConstructor() {
        // Given
        CourseBundleInDTO postDTO = new CourseBundleInDTO();

        // Then
        assertNotNull(postDTO, "No-args constructor should create a non-null object");
    }
}
