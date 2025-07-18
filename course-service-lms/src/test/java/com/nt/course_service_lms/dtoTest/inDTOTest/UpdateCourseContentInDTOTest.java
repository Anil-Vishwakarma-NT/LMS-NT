package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCourseContentInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidInput_thenNoViolations() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Intro to Java",
                "Comprehensive overview of Java programming.",
                "https://example.com/java-course",
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCourseIdIsNegative_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                -1L,
                "Valid Title",
                "Valid Description",
                null,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenTitleIsBlank_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "   ",
                "Valid Description",
                null,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenTitleExceedsMax_thenViolation() {
        String longTitle = new String(new char[101]).replace('\0', 'T');
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                longTitle,
                "Valid Description",
                null,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenDescriptionIsBlank_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                " ",
                null,
                false
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        String longDesc = new String(new char[1001]).replace('\0', 'A');
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                longDesc,
                null,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenInvalidResourceLink_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                "invalid-url",
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("resourceLink")));
    }

    @Test
    void whenEmptyResourceLink_thenValid() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                "",
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBuilderCreatesValidObject() {
        UpdateCourseContentInDTO dto = UpdateCourseContentInDTO.builder()
                .courseId(1L)
                .title("Builder Title")
                .description("Builder Description")
                .resourceLink("https://builder.com")
                .isActive(true)
                .build();

        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals("Builder Title", dto.getTitle());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO();
        dto.setCourseId(42L);
        dto.setTitle("Course Title");
        dto.setDescription("Some description");
        dto.setResourceLink("https://valid.com");
        dto.setActive(true);

        assertEquals(42L, dto.getCourseId());
        assertEquals("Course Title", dto.getTitle());
        assertEquals("Some description", dto.getDescription());
        assertEquals("https://valid.com", dto.getResourceLink());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateCourseContentInDTO dto1 = new UpdateCourseContentInDTO(1L, "Title", "Desc", "", true);
        UpdateCourseContentInDTO dto2 = new UpdateCourseContentInDTO(1L, "Title", "Desc", "", true);
        UpdateCourseContentInDTO dto3 = new UpdateCourseContentInDTO(2L, "Other", "Other", "", false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "random string");
    }
}

