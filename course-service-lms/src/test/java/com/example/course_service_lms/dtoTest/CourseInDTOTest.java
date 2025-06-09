package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.inDTO.CourseInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CourseInDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCourseDTO() {
        CourseInDTO dto = new CourseInDTO("Java", 1L, "Learn Java", "BEGINNER", "image.png");

        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testTitleTooShort() {
        CourseInDTO dto = new CourseInDTO("J", 1L, "Valid Description", "BEGINNER", "img.jpg");

        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void testBlankTitle() {
        CourseInDTO dto = new CourseInDTO(" ", 1L, "Valid Description", "BEGINNER", "img.jpg");

        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankDescription() {
        CourseInDTO dto = new CourseInDTO("Valid Title", 1L, "", "BEGINNER", "img.jpg");

        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void testNullCourseLevel() {
        CourseInDTO dto = new CourseInDTO("Valid Title", 1L, "Valid Description", null, "img.jpg");

        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseLevel")));
    }

    @Test
    void testEqualsAndHashCode() {
        CourseInDTO dto1 = new CourseInDTO("Java", 1L, "Learn Java", "BEGINNER", "img1.png");
        CourseInDTO dto2 = new CourseInDTO("Java", 1L, "Learn Java", "BEGINNER", "img1.png");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
