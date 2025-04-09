package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.dto.CourseContentDTO;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseContentDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCourseContentDTO() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle("Valid Title");
        courseContentDTO.setDescription("This is a valid description.");
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink("https://example.com/resource");

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidCourseId() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(-1L); // Invalid Course ID
        courseContentDTO.setTitle("Valid Title");
        courseContentDTO.setDescription("Valid description.");
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink("https://example.com/resource");

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Valid Course ID required")));
    }

    @Test
    void testBlankTitle() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle(""); // Blank title
        courseContentDTO.setDescription("Valid description.");
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink("https://example.com/resource");

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title cannot be blank")));
    }

    @Test
    void testExceedingTitleLength() {
        // Given
        String longTitle = "A".repeat(101); // Title with 101 characters
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle(longTitle);
        courseContentDTO.setDescription("Valid description.");
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink("https://example.com/resource");

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title cannot exceed 100 characters")));
    }

    @Test
    void testBlankDescription() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle("Valid Title");
        courseContentDTO.setDescription(""); // Blank description
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink("https://example.com/resource");

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description cannot be blank")));
    }

    @Test
    void testInvalidVideoLink() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle("Valid Title");
        courseContentDTO.setDescription("Valid description.");
        courseContentDTO.setVideoLink("invalid-video-link"); // Invalid video link
        courseContentDTO.setResourceLink("https://example.com/resource");

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Video link must be a valid URL")));
    }

    @Test
    void testInvalidResourceLink() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle("Valid Title");
        courseContentDTO.setDescription("Valid description.");
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink("invalid-resource-link"); // Invalid resource link

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Resource link must be a valid URL")));
    }

    @Test
    void testNullResourceLink() {
        // Given
        CourseContentDTO courseContentDTO = new CourseContentDTO();
        courseContentDTO.setCourseId(1L);
        courseContentDTO.setTitle("Valid Title");
        courseContentDTO.setDescription("Valid description.");
        courseContentDTO.setVideoLink("https://example.com/video");
        courseContentDTO.setResourceLink(""); // Null or empty resource link is allowed

        // When
        Set<ConstraintViolation<CourseContentDTO>> violations = validator.validate(courseContentDTO);

        // Then
        assertTrue(violations.isEmpty());
    }
}
