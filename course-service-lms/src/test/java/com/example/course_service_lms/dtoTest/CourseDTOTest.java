package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.dto.CourseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCourseDTO() {
        CourseDTO dto = new CourseDTO("Java", 1L, "Learn Java", "BEGINNER", "image.png");

        Set<ConstraintViolation<CourseDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testTitleTooShort() {
        CourseDTO dto = new CourseDTO("J", 1L, "Valid Description", "BEGINNER", "img.jpg");

        Set<ConstraintViolation<CourseDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void testBlankTitle() {
        CourseDTO dto = new CourseDTO(" ", 1L, "Valid Description", "BEGINNER", "img.jpg");

        Set<ConstraintViolation<CourseDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankDescription() {
        CourseDTO dto = new CourseDTO("Valid Title", 1L, "", "BEGINNER", "img.jpg");

        Set<ConstraintViolation<CourseDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void testNullCourseLevel() {
        CourseDTO dto = new CourseDTO("Valid Title", 1L, "Valid Description", null, "img.jpg");

        Set<ConstraintViolation<CourseDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseLevel")));
    }

    @Test
    void testEqualsAndHashCode() {
        CourseDTO dto1 = new CourseDTO("Java", 1L, "Learn Java", "BEGINNER", "img1.png");
        CourseDTO dto2 = new CourseDTO("Java", 1L, "Learn Java", "BEGINNER", "img1.png");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
