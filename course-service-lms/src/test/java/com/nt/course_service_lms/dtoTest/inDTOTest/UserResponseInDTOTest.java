package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserResponseInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserResponseInDTO createValidDTO() {
        return new UserResponseInDTO(
                1L,
                1L,
                1L,
                1L,
                "{\"answer\": \"A\"}",
                LocalDateTime.now()
        );
    }

    @Test
    void testValidDTO() {
        UserResponseInDTO dto = createValidDTO();
        Set<ConstraintViolation<UserResponseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    void testUserIdNull() {
        UserResponseInDTO dto = createValidDTO();
        dto.setUserId(null);
        assertViolation(dto, "User ID is required");
    }

    @Test
    void testUserIdNegative() {
        UserResponseInDTO dto = createValidDTO();
        dto.setUserId(-5L);
        assertViolation(dto, "User ID must be a positive number");
    }

    @Test
    void testQuizIdNull() {
        UserResponseInDTO dto = createValidDTO();
        dto.setQuizId(null);
        assertViolation(dto, "Quiz ID is required");
    }

    @Test
    void testQuizIdNegative() {
        UserResponseInDTO dto = createValidDTO();
        dto.setQuizId(0L);
        assertViolation(dto, "Quiz ID must be a positive number");
    }

    @Test
    void testQuestionIdNull() {
        UserResponseInDTO dto = createValidDTO();
        dto.setQuestionId(null);
        assertViolation(dto, "Question ID is required");
    }

    @Test
    void testQuestionIdNegative() {
        UserResponseInDTO dto = createValidDTO();
        dto.setQuestionId(-2L);
        assertViolation(dto, "Question ID must be a positive number");
    }

    @Test
    void testAttemptNull() {
        UserResponseInDTO dto = createValidDTO();
        dto.setAttempt(null);
        assertViolation(dto, "Attempt number is required");
    }

    @Test
    void testAttemptZero() {
        UserResponseInDTO dto = createValidDTO();
        dto.setAttempt(0L);
        assertViolation(dto, "Attempt number must be a positive number");
    }

    @Test
    void testUserAnswerBlank() {
        UserResponseInDTO dto = createValidDTO();
        dto.setUserAnswer("   ");
        assertViolation(dto, "User answer is required");
    }

    @Test
    void testBuilderCreatesValidDTO() {
        UserResponseInDTO dto = UserResponseInDTO.builder()
                .userId(1L)
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("{\"answer\":\"A\"}")
                .answeredAt(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<UserResponseInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO built with builder should be valid");
    }


    @Test
    void testEqualsAndHashCode() {
        UserResponseInDTO dto1 = createValidDTO();
        UserResponseInDTO dto2 = createValidDTO();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        UserResponseInDTO dto = new UserResponseInDTO(
                10L,
                20L,
                30L,
                1L,
                "{\"answer\": \"A\"}",
                now
        );

        assertEquals(10L, dto.getUserId());
        assertEquals(20L, dto.getQuizId());
        assertEquals(30L, dto.getQuestionId());
        assertEquals(1L, dto.getAttempt());
        assertEquals("{\"answer\": \"A\"}", dto.getUserAnswer());
        assertEquals(now, dto.getAnsweredAt());
    }

    private void assertViolation(UserResponseInDTO dto, String expectedMessage) {
        Set<ConstraintViolation<UserResponseInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals(expectedMessage)),
                "Expected message: " + expectedMessage
        );
    }
}

