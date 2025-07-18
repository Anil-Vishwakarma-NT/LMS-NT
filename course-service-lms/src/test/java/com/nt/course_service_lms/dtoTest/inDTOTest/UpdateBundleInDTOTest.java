package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UpdateBundleInDTO;
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

class UpdateBundleInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("Bundle123", true);
        Set<ConstraintViolation<UpdateBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenBundleNameIsBlank_thenViolation() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("   ", true);
        Set<ConstraintViolation<UpdateBundleInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleName")));
    }

    @Test
    void whenBundleNameTooShort_thenViolation() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("AB", true); // min = 3
        Set<ConstraintViolation<UpdateBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleName")));
    }

    @Test
    void whenBundleNameStartsWithDigit_thenViolation() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("1Bundle", false);
        Set<ConstraintViolation<UpdateBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleName")));
    }

    @Test
    void whenBundleNameStartsWithSpace_thenViolation() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO(" BundleName", false);
        Set<ConstraintViolation<UpdateBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleName")));
    }

    @Test
    void whenBundleNameEndsWithSpace_thenViolation() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO("BundleName ", false);
        Set<ConstraintViolation<UpdateBundleInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("bundleName")));
    }

    @Test
    void testBuilderCreatesValidObject() {
        UpdateBundleInDTO dto = UpdateBundleInDTO.builder()
                .bundleName("BuilderBundle")
                .isActive(true)
                .build();

        assertEquals("BuilderBundle", dto.getBundleName());
        assertTrue(dto.isActive());
        assertTrue(validator.validate(dto).isEmpty());
    }


    @Test
    void testDefaultConstructorAndSetters() {
        UpdateBundleInDTO dto = new UpdateBundleInDTO();
        dto.setBundleName("TestBundle");
        dto.setActive(true);

        assertEquals("TestBundle", dto.getBundleName());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateBundleInDTO dto1 = new UpdateBundleInDTO("BundleX", true);
        UpdateBundleInDTO dto2 = new UpdateBundleInDTO("BundleX", true);
        UpdateBundleInDTO dto3 = new UpdateBundleInDTO("BundleY", false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
    }
}

