package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateQuizQuestionInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UpdateQuizQuestionInDTO createValidDTO() {
        return new UpdateQuizQuestionInDTO(
                "What is Java?",
                "MCQ_SINGLE",
                "[\"OOP\", \"Functional\"]",
                "\"OOP\"",
                BigDecimal.valueOf(10.5),
                "Basic Java question",
                true,
                1
        );
    }

    @Test
    void testValidDTO() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        Set<ConstraintViolation<UpdateQuizQuestionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testQuestionTextBlank() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setQuestionText(" ");
        assertViolation(dto, "Question text is required");
    }

    @Test
    void testQuestionTextTooLong() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setQuestionText(new String(new char[5001]).replace('\0', 'A'));
        assertViolation(dto, "Question text cannot exceed 5000 characters");
    }

    @Test
    void testQuestionTypeBlank() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setQuestionType("");
        assertViolation(dto, "Question type is required");
    }

    @Test
    void testQuestionTypeInvalidPattern() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setQuestionType("ESSAY");
        assertViolation(dto, "Question type must be one of: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY, FILL_IN_BLANK");
    }

    @Test
    void testCorrectAnswerBlank() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setCorrectAnswer(" ");
        assertViolation(dto, "Correct answer is required");
    }

    @Test
    void testCorrectAnswerTooLong() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setCorrectAnswer(new String(new char[5001]).replace('\0', 'A'));
        assertViolation(dto, "Correct answer cannot exceed 5000 characters");
    }

    @Test
    void testOptionsTooLong() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setOptions(new String(new char[10001]).replace('\0', 'A'));
        assertViolation(dto, "Options cannot exceed 10000 characters");
    }

    @Test
    void testPointsNull() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setPoints(null);
        assertViolation(dto, "Points are required");
    }

    @Test
    void testPointsNegative() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setPoints(BigDecimal.valueOf(-1));
        assertViolation(dto, "Points cannot be negative");
    }

    @Test
    void testPointsTooHigh() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setPoints(BigDecimal.valueOf(1000.00));
        assertViolation(dto, "Points cannot exceed 999.99");
    }

    @Test
    void testExplanationTooLong() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setExplanation(new String(new char[5001]).replace('\0', 'A'));
        assertViolation(dto, "Explanation cannot exceed 5000 characters");
    }

    @Test
    void testRequiredNull() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setRequired(null);
        assertViolation(dto, "Required field must be specified");
    }

    @Test
    void testBuilderCreatesValidDTO() {
        UpdateQuizQuestionInDTO dto = UpdateQuizQuestionInDTO.builder()
                .questionText("What is Java?")
                .questionType("MCQ_SINGLE")
                .options("[\"OOP\", \"Procedural\"]")
                .correctAnswer("\"OOP\"")
                .points(BigDecimal.valueOf(10))
                .explanation("Basic question")
                .required(true)
                .position(1)
                .build();

        Set<ConstraintViolation<UpdateQuizQuestionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testPositionNull() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setPosition(null);
        assertViolation(dto, "Position is required");
    }

    @Test
    void testPositionNegative() {
        UpdateQuizQuestionInDTO dto = createValidDTO();
        dto.setPosition(-1);
        assertViolation(dto, "Position must be positive");
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateQuizQuestionInDTO dto1 = createValidDTO();
        UpdateQuizQuestionInDTO dto2 = createValidDTO();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        UpdateQuizQuestionInDTO dto = new UpdateQuizQuestionInDTO();
        assertNotNull(dto);
    }

    private void assertViolation(UpdateQuizQuestionInDTO dto, String expectedMessage) {
        Set<ConstraintViolation<UpdateQuizQuestionInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedMessage)),
                "Expected violation: " + expectedMessage);
    }
}

