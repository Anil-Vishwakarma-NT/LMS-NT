package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
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

class CourseInDTOTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        CourseInDTO dto = new CourseInDTO(
                "Java 101",
                42L,
                "Learn Java basics",
                "BEGINNER",
                true
        );
        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenTitleNull_thenViolation() {
        CourseInDTO dto = new CourseInDTO(null, 0L, "Desc", "INTERMEDIATE", true);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
        assertTrue(v.stream().anyMatch(c -> c.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenTitleTooShort_thenViolation() {
        CourseInDTO dto = new CourseInDTO("ab", 0L, "Desc", "ADVANCED", true);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void whenOwnerIdNull_thenViolation() {
        CourseInDTO dto = new CourseInDTO("Title", null, "Desc", "INTERMEDIATE", true);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void whenOwnerIdNegative_thenViolation() {
        CourseInDTO dto = new CourseInDTO("Title", -1L, "Desc", "INTERMEDIATE", true);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void whenDescriptionNull_thenViolation() {
        CourseInDTO dto = new CourseInDTO("Title", 1L, null, "BEGINNER", true);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void whenDescriptionTooShort_thenViolation() {
        CourseInDTO dto = new CourseInDTO("Title", 1L, "ab", "BEGINNER", true);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void whenCourseLevelNull_thenViolation() {
        CourseInDTO dto = new CourseInDTO("Title", 1L, "Desc", null, false);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void whenActiveFalse_thenStillValid() {
        CourseInDTO dto = new CourseInDTO("Title", 1L, "Desc", "BEGINNER", false);
        Set<ConstraintViolation<CourseInDTO>> v = validator.validate(dto);
        assertTrue(v.isEmpty());
    }

    @Test
    void whenTitleIsWhitespace_thenViolation() {
        CourseInDTO dto = new CourseInDTO("   ", 1L, "Desc", "BEGINNER", true);
        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }


    @Test
    void testBuilderCreatesValidObject() {
        CourseInDTO dto = CourseInDTO.builder()
                .title("Spring Boot")
                .ownerId(5L)
                .description("Spring Boot for beginners")
                .courseLevel("BEGINNER")
                .isActive(true)
                .build();

        Set<ConstraintViolation<CourseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals("Spring Boot", dto.getTitle());
        assertEquals(5L, dto.getOwnerId());
    }


    @Test
    void testConstructorsGettersSetters() {
        CourseInDTO dto1 = new CourseInDTO();
        dto1.setTitle("T");
        dto1.setOwnerId(2L);
        dto1.setDescription("D");
        dto1.setCourseLevel("ADVANCED");
        dto1.setIsActive(true);

        assertEquals("T", dto1.getTitle());
        assertEquals(2L, dto1.getOwnerId());
        assertEquals("D", dto1.getDescription());
        assertEquals("ADVANCED", dto1.getCourseLevel());
        assertTrue(dto1.getIsActive());

        CourseInDTO dto2 = new CourseInDTO("X", 3L, "Y", "INTERMEDIATE", false);
        assertEquals("X", dto2.getTitle());
        assertEquals(3L, dto2.getOwnerId());
        assertEquals("Y", dto2.getDescription());
        assertEquals("INTERMEDIATE", dto2.getCourseLevel());
        assertFalse(dto2.getIsActive());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        CourseInDTO a = new CourseInDTO("T", 1L, "D", "C", true);
        CourseInDTO b = new CourseInDTO("T", 1L, "D", "C", true);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        CourseInDTO a = new CourseInDTO("T1", 1L, "D1", "C1", true);
        CourseInDTO b = new CourseInDTO("T2", 2L, "D2", "C2", false);
        assertNotEquals(a, b);
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        CourseInDTO a = new CourseInDTO("T", 1L, "D", "C", true);
        assertNotEquals(a, null);
        assertNotEquals(a, "some string");
    }

    @Test
    void testEquals_differentActiveField() {
        CourseInDTO dto1 = new CourseInDTO("Title", 1L, "Desc", "BEGINNER", true);
        CourseInDTO dto2 = new CourseInDTO("Title", 1L, "Desc", "BEGINNER", false);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testHashCode_consistency() {
        CourseInDTO dto = new CourseInDTO("Title", 1L, "Desc", "BEGINNER", true);
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

}

