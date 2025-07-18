package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizAttemptUpdateInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidStatus_thenNoViolations() {
        QuizAttemptUpdateInDTO dto = new QuizAttemptUpdateInDTO(
                LocalDateTime.now(),
                "Score: 80%",
                "COMPLETED"
        );
        Set<ConstraintViolation<QuizAttemptUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }


    @Test
    void whenInvalidStatus_thenValidationFails() {
        QuizAttemptUpdateInDTO dto = new QuizAttemptUpdateInDTO(
                LocalDateTime.now(),
                "Score: 60%",
                "FINISHED"
        );
        Set<ConstraintViolation<QuizAttemptUpdateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }

    @Test
    void whenStatusIsNull_thenNoViolation() {
        QuizAttemptUpdateInDTO dto = new QuizAttemptUpdateInDTO(
                LocalDateTime.now(),
                "Score: 100%",
                null
        );
        Set<ConstraintViolation<QuizAttemptUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBuilderCreatesValidObject() {
        LocalDateTime now = LocalDateTime.now();
        QuizAttemptUpdateInDTO dto = QuizAttemptUpdateInDTO.builder()
                .finishedAt(now)
                .scoreDetails("75%")
                .status("COMPLETED")
                .build();

        assertEquals(now, dto.getFinishedAt());
        assertEquals("75%", dto.getScoreDetails());
        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        LocalDateTime now = LocalDateTime.now();
        QuizAttemptUpdateInDTO dto1 = new QuizAttemptUpdateInDTO(now, "Passed", "COMPLETED");
        QuizAttemptUpdateInDTO dto2 = new QuizAttemptUpdateInDTO(now, "Passed", "COMPLETED");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        LocalDateTime now = LocalDateTime.now();
        QuizAttemptUpdateInDTO dto1 = new QuizAttemptUpdateInDTO(now, "Passed", "COMPLETED");
        QuizAttemptUpdateInDTO dto2 = new QuizAttemptUpdateInDTO(now, "Failed", "ABANDONED");

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsWithNullAndOtherType() {
        QuizAttemptUpdateInDTO dto = new QuizAttemptUpdateInDTO(LocalDateTime.now(), "x", "COMPLETED");

        assertNotEquals(dto, null);
        assertNotEquals(dto, "Not a DTO");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        QuizAttemptUpdateInDTO dto = new QuizAttemptUpdateInDTO();
        dto.setFinishedAt(now);
        dto.setScoreDetails("90%");
        dto.setStatus("IN_PROGRESS");

        assertEquals(now, dto.getFinishedAt());
        assertEquals("90%", dto.getScoreDetails());
        assertEquals("IN_PROGRESS", dto.getStatus());
    }
}

