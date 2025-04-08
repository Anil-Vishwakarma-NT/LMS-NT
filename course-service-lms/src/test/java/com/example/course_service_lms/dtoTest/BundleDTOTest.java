package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.dto.BundleDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BundleDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBundleName() {
        BundleDTO dto = new BundleDTO("JavaBundle");
        Set<ConstraintViolation<BundleDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testNoArgsConstructor() {
        BundleDTO bundleDTO = new BundleDTO();
        assertNotNull(bundleDTO);
        assertNull(bundleDTO.getBundleName());
    }

    @Test
    void testAllArgsConstructor() {
        String name = "JavaBundle";
        BundleDTO bundleDTO = new BundleDTO(name);

        assertNotNull(bundleDTO);
        assertEquals(name, bundleDTO.getBundleName());
    }



    @Test
    void testTooShortBundleName() {
        BundleDTO dto = new BundleDTO("AB");
        Set<ConstraintViolation<BundleDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name must be at least 3 characters long", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPatternStartingWithNumber() {
        BundleDTO dto = new BundleDTO("1Java");
        Set<ConstraintViolation<BundleDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name invalid", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPatternStartingWithSpace() {
        BundleDTO dto = new BundleDTO(" Java");
        Set<ConstraintViolation<BundleDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name invalid", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPatternEndingWithSpace() {
        BundleDTO dto = new BundleDTO("Java ");
        Set<ConstraintViolation<BundleDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Bundle Name invalid", violations.iterator().next().getMessage());
    }
}
