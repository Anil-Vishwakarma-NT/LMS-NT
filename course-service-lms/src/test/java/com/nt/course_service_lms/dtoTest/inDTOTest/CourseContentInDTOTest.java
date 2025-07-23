package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseContentInDTOTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidDTO_thenNoViolations() {
        CourseContentInDTO dto = new CourseContentInDTO(
                1L,
                "Java Basics",
                "This is a basic Java course",
                "https://resource-link.com",
                true
        );
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCourseIdIsNull_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO();
        dto.setCourseId(-1);  // Since it's a primitive, no @NotNull triggers, but @Min will
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setResourceLink("https://link.com");
        dto.setActive(true);

        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenCourseIdIsNegative_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(-5L, "Title", "Description", "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenTitleIsNull_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, null, "Description", "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenTitleIsBlank_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "  ", "Description", "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenTitleTooLong_thenViolation() {
        String longTitle = new String(new char[101]).replace('\0', 'A');
        CourseContentInDTO dto = new CourseContentInDTO(1L, longTitle, "Description", "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenDescriptionIsNull_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", null, "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenDescriptionIsBlank_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", "   ", "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        String longDesc = new String(new char[1001]).replace('\0', 'D');
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", longDesc, "https://link.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenResourceLinkIsInvalid_thenViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", "Desc", "invalid-url", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("resourceLink")));
    }

    @Test
    void whenResourceLinkIsValid_thenNoViolation() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", "Desc", "http://valid.com", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenResourceLinkIsEmpty_thenValid() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", "Desc", "", true);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenIsActiveFalse_thenStillValid() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", "Description", "http://valid.com", false);
        Set<ConstraintViolation<CourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        CourseContentInDTO dto = new CourseContentInDTO();
        dto.setCourseId(100L);
        dto.setTitle("Sample");
        dto.setDescription("Some description");
        dto.setResourceLink("https://test.com");
        dto.setActive(true);

        assertEquals(100L, dto.getCourseId());
        assertEquals("Sample", dto.getTitle());
        assertEquals("Some description", dto.getDescription());
        assertEquals("https://test.com", dto.getResourceLink());
        assertTrue(dto.isActive());
    }

    @Test
    void testNoArgsConstructor() {
        CourseContentInDTO dto = new CourseContentInDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "Title", "Desc", "https://link.com", true);
        assertEquals(1L, dto.getCourseId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Desc", dto.getDescription());
        assertEquals("https://link.com", dto.getResourceLink());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        CourseContentInDTO dto1 = new CourseContentInDTO(1L, "T", "D", "http://a.com", true);
        CourseContentInDTO dto2 = new CourseContentInDTO(1L, "T", "D", "http://a.com", true);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        CourseContentInDTO dto1 = new CourseContentInDTO(1L, "T1", "D1", "http://a.com", true);
        CourseContentInDTO dto2 = new CourseContentInDTO(2L, "T2", "D2", "http://b.com", false);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEquals_differentTypeAndNull() {
        CourseContentInDTO dto = new CourseContentInDTO(1L, "T", "D", "http://a.com", true);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }
}

