package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.inDTO.BundleInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BundleInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBundleName() {
        BundleInDTO dto = new BundleInDTO("JavaBundle");
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testNoArgsConstructor() {
        BundleInDTO bundleInDTO = new BundleInDTO();
        assertNotNull(bundleInDTO);
        assertNull(bundleInDTO.getBundleName());
    }

    @Test
    void testAllArgsConstructor() {
        String name = "JavaBundle";
        BundleInDTO bundleInDTO = new BundleInDTO(name);

        assertNotNull(bundleInDTO);
        assertEquals(name, bundleInDTO.getBundleName());
    }



    @Test
    void testTooShortBundleName() {
        BundleInDTO dto = new BundleInDTO("AB");
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name must be at least 3 characters long", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPatternStartingWithNumber() {
        BundleInDTO dto = new BundleInDTO("1Java");
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name invalid", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPatternStartingWithSpace() {
        BundleInDTO dto = new BundleInDTO(" Java");
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name invalid", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPatternEndingWithSpace() {
        BundleInDTO dto = new BundleInDTO("Java ");
        Set<ConstraintViolation<BundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name invalid", violations.iterator().next().getMessage());
    }
}
