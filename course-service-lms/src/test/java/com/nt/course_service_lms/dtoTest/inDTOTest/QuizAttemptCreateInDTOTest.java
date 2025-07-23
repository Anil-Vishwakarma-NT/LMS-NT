package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
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

class QuizAttemptCreateInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidFields_thenNoViolations() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO(1L, 100L);
        Set<ConstraintViolation<QuizAttemptCreateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenQuizIdIsNull_thenValidationFails() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO(null, 100L);
        Set<ConstraintViolation<QuizAttemptCreateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quizId")));
    }

    @Test
    void whenQuizIdIsNotPositive_thenValidationFails() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO(0L, 100L);
        Set<ConstraintViolation<QuizAttemptCreateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quizId")));
    }

    @Test
    void whenUserIdIsNull_thenValidationFails() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO(10L, null);
        Set<ConstraintViolation<QuizAttemptCreateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void whenUserIdIsNotPositive_thenValidationFails() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO(10L, 0L);
        Set<ConstraintViolation<QuizAttemptCreateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void testBuilderCreatesValidObject() {
        QuizAttemptCreateInDTO dto = QuizAttemptCreateInDTO.builder()
                .quizId(1L)
                .userId(2L)
                .build();

        assertEquals(1L, dto.getQuizId());
        assertEquals(2L, dto.getUserId());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        QuizAttemptCreateInDTO dto1 = new QuizAttemptCreateInDTO(5L, 50L);
        QuizAttemptCreateInDTO dto2 = new QuizAttemptCreateInDTO(5L, 50L);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        QuizAttemptCreateInDTO dto1 = new QuizAttemptCreateInDTO(5L, 50L);
        QuizAttemptCreateInDTO dto2 = new QuizAttemptCreateInDTO(6L, 60L);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEqualsWithNullAndOtherType() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO(1L, 2L);
        assertNotEquals(null, dto);
        assertNotEquals(dto, "some string");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO();
        dto.setQuizId(123L);
        dto.setUserId(456L);

        assertEquals(123L, dto.getQuizId());
        assertEquals(456L, dto.getUserId());
    }
}

