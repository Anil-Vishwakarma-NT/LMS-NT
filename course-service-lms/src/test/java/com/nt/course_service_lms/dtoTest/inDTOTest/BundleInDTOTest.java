package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.BundleInDTO;
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

class BundleInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidDTO_thenNoViolations() {
        BundleInDTO dto = new BundleInDTO("JavaCourse2024", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void whenBundleNameIsNull_thenViolation() {
        BundleInDTO dto = new BundleInDTO(null, true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleName")));
    }

    @Test
    void whenBundleNameIsEmpty_thenViolation() {
        BundleInDTO dto = new BundleInDTO("", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameIsWhitespaceOnly_thenViolation() {
        BundleInDTO dto = new BundleInDTO("   ", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameIsTooShort_thenViolation() {
        BundleInDTO dto = new BundleInDTO("ab", true); // less than 3 chars
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameIsMinLength_thenNoViolation() {
        BundleInDTO dto = new BundleInDTO("abc", true); // exactly 3 chars
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenBundleNameStartsWithDigit_thenViolation() {
        BundleInDTO dto = new BundleInDTO("1Java", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameStartsWithSpace_thenViolation() {
        BundleInDTO dto = new BundleInDTO(" Java", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameEndsWithSpace_thenViolation() {
        BundleInDTO dto = new BundleInDTO("Java ", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameContainsSpecialChars_thenViolation() {
        BundleInDTO dto = new BundleInDTO("Java_Course", true); // underscore not allowed
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBundleNameIsAlphanumericOnly_thenNoViolation() {
        BundleInDTO dto = new BundleInDTO("Java123", true);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenIsActiveIsFalse_thenStillValid() {
        BundleInDTO dto = new BundleInDTO("ValidName", false);
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBuilderCreatesValidDTO() {
        BundleInDTO dto = BundleInDTO.builder()
                .bundleName("BuiltCourse")
                .isActive(true)
                .build();

        assertEquals("BuiltCourse", dto.getBundleName());
        assertTrue(dto.isActive());
    }

    @Test
    void testSettersAndGetters() {
        BundleInDTO dto = new BundleInDTO();
        dto.setBundleName("SpringBootCourse");
        dto.setActive(true);

        assertEquals("SpringBootCourse", dto.getBundleName());
        assertTrue(dto.isActive());
    }

    @Test
    void testAllArgsConstructor() {
        BundleInDTO dto = new BundleInDTO("MyCourse", true);
        assertEquals("MyCourse", dto.getBundleName());
        assertTrue(dto.isActive());
    }

    @Test
    void testNoArgsConstructor() {
        BundleInDTO dto = new BundleInDTO();
        assertNotNull(dto);
        assertNull(dto.getBundleName());
        assertFalse(dto.isActive()); // default boolean value is false
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        BundleInDTO dto1 = new BundleInDTO("TestCourse", true);
        BundleInDTO dto2 = new BundleInDTO("TestCourse", true);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEquals_DifferentValues() {
        BundleInDTO dto1 = new BundleInDTO("Course1", true);
        BundleInDTO dto2 = new BundleInDTO("Course2", false);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        BundleInDTO dto = new BundleInDTO("X", true);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "SomeString");
    }
}

