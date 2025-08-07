package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizQuestionInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        QuizQuestionInDTO dto = new QuizQuestionInDTO(
                1L,
                "What is Java?",
                "MCQ_SINGLE",
                "[\"OOP\", \"Functional\"]",
                "[\"OOP\"]",
                BigDecimal.valueOf(10.5),
                "Java is an OOP language.",
                true
        );

        Set<ConstraintViolation<QuizQuestionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNullFields_thenExpectedViolations() {
        QuizQuestionInDTO dto = new QuizQuestionInDTO(
                null, "", "", "", "", null, "", null
        );

        Set<ConstraintViolation<QuizQuestionInDTO>> violations = validator.validate(dto);
        assertEquals(7, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quizId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionText")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionType")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correctAnswer")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("required")));
    }

    @Test
    void whenValuesExceedLimits_thenValidationFails() {
        String longText = new String(new char[5001]).replace('\0', 'A');
        String longType = new String(new char[36]).replace('\0', 'M');
        String longOptions = new String(new char[10001]).replace('\0', 'O');
        String longAnswer = new String(new char[5001]).replace('\0', 'C');
        String longExplanation = new String(new char[5001]).replace('\0', 'E');

        QuizQuestionInDTO dto = new QuizQuestionInDTO(
                -1L,
                longText,
                longType,
                longOptions,
                longAnswer,
                BigDecimal.valueOf(1000), // exceeds max
                longExplanation,
                true
        );

        Set<ConstraintViolation<QuizQuestionInDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quizId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionText")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionType")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("options")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correctAnswer")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
    }

    @Test
    void whenPointsNegative_thenFailsDecimalMinValidation() {
        QuizQuestionInDTO dto = new QuizQuestionInDTO(
                1L, "Q", "MCQ_SINGLE", "[]", "[]",
                BigDecimal.valueOf(-1), null, true
        );

        Set<ConstraintViolation<QuizQuestionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
    }

    @Test
    void whenQuestionTypeInvalid_thenPatternViolation() {
        QuizQuestionInDTO dto = new QuizQuestionInDTO(
                1L, "Q", "ESSAY", "[]", "[]",
                BigDecimal.ONE, null, true
        );

        Set<ConstraintViolation<QuizQuestionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionType")));
    }

    @Test
    void testBuilderCreatesValidObject() {
        QuizQuestionInDTO dto = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("Explain JVM")
                .questionType("SHORT_ANSWER")
                .options(null)
                .correctAnswer("['Java Virtual Machine']")
                .points(BigDecimal.valueOf(5))
                .explanation("The JVM runs bytecode.")
                .required(true)
                .build();

        assertEquals(1L, dto.getQuizId());
        assertEquals("Explain JVM", dto.getQuestionText());
        assertEquals("SHORT_ANSWER", dto.getQuestionType());
        assertEquals("['Java Virtual Machine']", dto.getCorrectAnswer());
        assertEquals(BigDecimal.valueOf(5), dto.getPoints());
        assertEquals("The JVM runs bytecode.", dto.getExplanation());
        assertTrue(dto.getRequired());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizQuestionInDTO q1 = new QuizQuestionInDTO(
                1L, "Q", "MCQ_SINGLE", "[]", "[\"A\"]",
                BigDecimal.ONE, "explanation", true
        );
        QuizQuestionInDTO q2 = new QuizQuestionInDTO(
                1L, "Q", "MCQ_SINGLE", "[]", "[\"A\"]",
                BigDecimal.ONE, "explanation", true
        );

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void testNotEqualsAndNull() {
        QuizQuestionInDTO q1 = new QuizQuestionInDTO(
                1L, "Q", "MCQ_SINGLE", "[]", "[\"A\"]",
                BigDecimal.ONE, "explanation", true
        );
        QuizQuestionInDTO q2 = new QuizQuestionInDTO(
                2L, "Different", "SHORT_ANSWER", "[]", "[\"B\"]",
                BigDecimal.TEN, "diff", false
        );

        assertNotEquals(q1, q2);
        assertNotEquals(q1, null);
        assertNotEquals(q1, "String");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        QuizQuestionInDTO dto = new QuizQuestionInDTO();
        dto.setQuizId(2L);
        dto.setQuestionText("What is AI?");
        dto.setQuestionType("MCQ_SINGLE");
        dto.setOptions("[\"A\",\"B\"]");
        dto.setCorrectAnswer("[\"A\"]");
        dto.setPoints(BigDecimal.valueOf(5.0));
        dto.setExplanation("Explanation");
        dto.setRequired(false);

        assertEquals(2L, dto.getQuizId());
        assertEquals("What is AI?", dto.getQuestionText());
        assertEquals("MCQ_SINGLE", dto.getQuestionType());
        assertEquals("[\"A\"]", dto.getCorrectAnswer());
        assertEquals(BigDecimal.valueOf(5.0), dto.getPoints());
        assertEquals("Explanation", dto.getExplanation());
        assertFalse(dto.getRequired());
    }
}

