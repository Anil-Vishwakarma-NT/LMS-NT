package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCourseInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDTO_shouldHaveNoViolations() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "Java Basics",
                1L,
                "This course covers Java fundamentals.",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void titleBlank_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "   ",
                1L,
                "Valid description",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void titleTooShort_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "ab",
                1L,
                "Valid description",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void ownerIdNull_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "Valid Title",
                null,
                "Valid description",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ownerId")));
    }

    @Test
    void ownerIdNegative_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "Valid Title",
                -1L,
                "Valid description",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ownerId")));
    }

    @Test
    void descriptionBlank_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "Valid Title",
                1L,
                " ",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void descriptionTooShort_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "Valid Title",
                1L,
                "ab",
                "BEGINNER",
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
    @Test
    void builderShouldCreateValidDTO() {
        UpdateCourseInDTO dto = UpdateCourseInDTO.builder()
                .title("Built Title")
                .ownerId(10L)
                .description("Built description")
                .courseLevel("INTERMEDIATE")
                .Active(true)
                .build();

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }


    @Test
    void courseLevelNull_shouldFailValidation() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO(
                "Valid Title",
                1L,
                "Valid description",
                null,
                true
        );

        Set<ConstraintViolation<UpdateCourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseLevel")));
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateCourseInDTO dto1 = new UpdateCourseInDTO("Title", 1L, "Desc", "BEGINNER", true);
        UpdateCourseInDTO dto2 = new UpdateCourseInDTO("Title", 1L, "Desc", "BEGINNER", true);
        UpdateCourseInDTO dto3 = new UpdateCourseInDTO("Another", 2L, "Other", "ADVANCED", false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "Some String");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        UpdateCourseInDTO dto = new UpdateCourseInDTO();
        dto.setTitle("Spring Boot Course");
        dto.setOwnerId(99L);
        dto.setDescription("Spring Boot for beginners.");
        dto.setCourseLevel("INTERMEDIATE");
        dto.setActive(false);

        assertEquals("Spring Boot Course", dto.getTitle());
        assertEquals(99L, dto.getOwnerId());
        assertEquals("Spring Boot for beginners.", dto.getDescription());
        assertEquals("INTERMEDIATE", dto.getCourseLevel());
        assertFalse(dto.isActive());
    }
}

