package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.dto.CourseBundleDTO;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CourseBundleDTOTest {

    @Test
    void testConstructorInitialization() {
        // Given
        long courseBundleId = 1L;
        Long bundleId = 2L;
        String bundleName = "Beginner Programming Bundle";
        Long courseId = 3L;
        String courseName = "Java Basics";

        // When
        CourseBundleDTO dto = new CourseBundleDTO(courseBundleId, bundleId, bundleName, courseId, courseName);

        // Then
        assertThat(dto.getCourseBundleId()).isEqualTo(courseBundleId);
        assertThat(dto.getBundleId()).isEqualTo(bundleId);
        assertThat(dto.getBundleName()).isEqualTo(bundleName);
        assertThat(dto.getCourseId()).isEqualTo(courseId);
        assertThat(dto.getCourseName()).isEqualTo(courseName);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        CourseBundleDTO dto = new CourseBundleDTO();
        long courseBundleId = 5L;
        Long bundleId = 10L;
        String bundleName = "Advanced Programming";
        Long courseId = 15L;
        String courseName = "Spring Framework";

        // When
        dto.setCourseBundleId(courseBundleId);
        dto.setBundleId(bundleId);
        dto.setBundleName(bundleName);
        dto.setCourseId(courseId);
        dto.setCourseName(courseName);

        // Then
        assertThat(dto.getCourseBundleId()).isEqualTo(courseBundleId);
        assertThat(dto.getBundleId()).isEqualTo(bundleId);
        assertThat(dto.getBundleName()).isEqualTo(bundleName);
        assertThat(dto.getCourseId()).isEqualTo(courseId);
        assertThat(dto.getCourseName()).isEqualTo(courseName);
    }

    @Test
    void testEquals() {
        // Given
        CourseBundleDTO dto1 = new CourseBundleDTO(1L, 2L, "Beginner Programming Bundle", 3L, "Java Basics");
        CourseBundleDTO dto2 = new CourseBundleDTO(1L, 2L, "Beginner Programming Bundle", 3L, "Java Basics");
        CourseBundleDTO dto3 = new CourseBundleDTO(4L, 5L, "Intermediate Programming", 6L, "Spring Boot");

        // Then
        assertThat(dto1).isEqualTo(dto2); // Equal objects
        assertThat(dto1).isNotEqualTo(dto3); // Non-equal objects
        assertThat(dto1).isNotEqualTo(null); // Not equal to null
    }

    @Test
    void testHashCode() {
        // Given
        CourseBundleDTO dto1 = new CourseBundleDTO(1L, 2L, "Beginner Programming Bundle", 3L, "Java Basics");
        CourseBundleDTO dto2 = new CourseBundleDTO(1L, 2L, "Beginner Programming Bundle", 3L, "Java Basics");
        CourseBundleDTO dto3 = new CourseBundleDTO(4L, 5L, "Intermediate Programming", 6L, "Spring Boot");

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode()); // Hash codes match
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode()); // Hash codes differ
    }

    @Test
    void testNoArgsConstructor() {
        // When
        CourseBundleDTO dto = new CourseBundleDTO();

        // Then
        assertThat(dto.getCourseBundleId()).isEqualTo(0L);
        assertThat(dto.getBundleId()).isNull();
        assertThat(dto.getBundleName()).isNull();
        assertThat(dto.getCourseId()).isNull();
        assertThat(dto.getCourseName()).isNull();
    }

    @Test
    void testValidationConstraints() {
        // Given
        CourseBundleDTO dto = new CourseBundleDTO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // When
        Set<ConstraintViolation<CourseBundleDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty(); // DTO has violations because fields are null

        // Verify specific violations
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Bundle ID cannot be null"));
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Course ID cannot be null"));
    }

    @Test
    void testValidationForPositiveNumbers() {
        // Given
        CourseBundleDTO dto = new CourseBundleDTO();
        dto.setBundleId(-1L);
        dto.setCourseId(-1L);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // When
        Set<ConstraintViolation<CourseBundleDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Bundle ID must be a positive number"));
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Course ID must be a positive number"));
    }
}