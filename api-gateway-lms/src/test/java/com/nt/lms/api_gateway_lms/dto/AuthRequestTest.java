package com.nt.lms.api_gateway_lms.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidAuthRequest() {
        AuthRequest request = new AuthRequest("user@nucleusteq.com", "validPassword");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testBlankPassword() {
        AuthRequest request = new AuthRequest("user@nucleusteq.com", "");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Expected 1 validation error for blank password");
    }
    @Test
    void testBlankEmail() {
        AuthRequest request = new AuthRequest("", "somePassword");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size(), "Expected 2 validation error for blank email");
    }

    @Test
    void testBothFieldsBlank() {
        AuthRequest request = new AuthRequest("", "");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        assertEquals(3, violations.size(), "Expected 3 validation errors for blank email and password");
    }

    @Test
    void testInvalidEmailFormat() {
        AuthRequest request = new AuthRequest("user-at-nucleusteq.com", "password");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size(), "Expected 2 validation errors for invalid email format");
    }

    @Test
    void testWrongEmailDomain() {
        AuthRequest request = new AuthRequest("user@gmail.com", "password");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Expected 1 validation error for wrong email domain");
    }

    @Test
    void testEmailDoesNotStartWithAlphabet() {
        AuthRequest request = new AuthRequest("1user@nucleusteq.com", "password");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Expected 1 validation error for email not starting with alphabet");
    }
}
