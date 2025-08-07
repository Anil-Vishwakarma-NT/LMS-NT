package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserResponseUpdateInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserResponseUpdateInDTO createValidDTO() {
        return new UserResponseUpdateInDTO(
                "{\"answer\": \"true\"}",
                true,
                new BigDecimal("95.25"),
                LocalDateTime.now()
        );
    }

    @Test
    void testValidDTO() {
        UserResponseUpdateInDTO dto = createValidDTO();
        Set<ConstraintViolation<UserResponseUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    void testUserAnswerBlank() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setUserAnswer("   ");
        assertViolation(dto, "User answer cannot be blank");
    }


    @Test
    void testIsCorrectNull() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setIsCorrect(null);
        assertViolation(dto, "Correct status is required");
    }

    @Test
    void testPointsEarnedNull() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setPointsEarned(null);
        assertViolation(dto, "Points earned is required");
    }

    @Test
    void testPointsEarnedNegative() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setPointsEarned(new BigDecimal("-1.0"));
        assertViolation(dto, "Points earned cannot be negative");
    }

    @Test
    void testPointsEarnedTooLarge() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setPointsEarned(new BigDecimal("1000.00"));
        assertViolation(dto, "Points earned cannot exceed 999.99");
    }

    @Test
    void testPointsEarnedTooManyDecimalPlaces() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setPointsEarned(new BigDecimal("10.123"));
        assertViolation(dto, "Points earned must have at most 3 integer digits and 2 decimal places");
    }

    @Test
    void testBuilderCreatesValidDTO() {
        UserResponseUpdateInDTO dto = UserResponseUpdateInDTO.builder()
                .userAnswer("{\"value\": true}")
                .isCorrect(true)
                .pointsEarned(new BigDecimal("10.00"))
                .answeredAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<UserResponseUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO created with builder should be valid");
    }

    @Test
    void testPointsEarnedAtMinBoundary() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setPointsEarned(new BigDecimal("0.00"));

        Set<ConstraintViolation<UserResponseUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "0.00 should be a valid pointsEarned value");
    }

    @Test
    void testPointsEarnedAtMaxBoundary() {
        UserResponseUpdateInDTO dto = createValidDTO();
        dto.setPointsEarned(new BigDecimal("999.99"));

        Set<ConstraintViolation<UserResponseUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "999.99 should be a valid pointsEarned value");
    }

    @Test
    void testEqualsAndHashCode() {
        UserResponseUpdateInDTO dto1 = createValidDTO();
        UserResponseUpdateInDTO dto2 = createValidDTO();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime time = LocalDateTime.now();
        UserResponseUpdateInDTO dto = new UserResponseUpdateInDTO(
                "Answer",
                false,
                new BigDecimal("5.75"),
                time
        );

        assertEquals("Answer", dto.getUserAnswer());
        assertEquals(false, dto.getIsCorrect());
        assertEquals(new BigDecimal("5.75"), dto.getPointsEarned());
        assertEquals(time, dto.getAnsweredAt());
    }

    private void assertViolation(UserResponseUpdateInDTO dto, String expectedMessage) {
        Set<ConstraintViolation<UserResponseUpdateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected validation errors");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedMessage)),
                "Expected violation message: " + expectedMessage);
    }
}

