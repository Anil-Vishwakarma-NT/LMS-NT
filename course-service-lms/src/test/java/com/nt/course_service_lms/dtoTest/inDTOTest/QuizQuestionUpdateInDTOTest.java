package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
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

class QuizQuestionUpdateInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        QuizQuestionUpdateInDTO dto = new QuizQuestionUpdateInDTO(
                "What is Java?",
                "MCQ_SINGLE",
                BigDecimal.valueOf(10.25),
                "Explanation",
                true,
                1
        );

        Set<ConstraintViolation<QuizQuestionUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations for valid DTO");
    }

    @Test
    void whenMissingRequiredFields_thenViolationsOccur() {
        QuizQuestionUpdateInDTO dto = new QuizQuestionUpdateInDTO(
                "", "", null, "Explanation", null, null
        );

        Set<ConstraintViolation<QuizQuestionUpdateInDTO>> violations = validator.validate(dto);
        assertEquals(5, violations.size(), "Expected 5 violations for missing required fields");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionText")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionType")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("required")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("position")));
    }

    @Test
    void whenInvalidValues_thenViolationsOccur() {
        QuizQuestionUpdateInDTO dto = new QuizQuestionUpdateInDTO(
                generateLongString("Q", 10001), // exceeds max 10000
                generateLongString("Type", 6),   // exceeds max 20
                BigDecimal.valueOf(0.99),        // less than min 1.00
                "Explanation",
                true,
                -1                                // not positive
        );

        Set<ConstraintViolation<QuizQuestionUpdateInDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "Expected 4 violations for invalid field values");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionText")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionType")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("position")));
    }

    @Test
    void whenPointsTooLargeOrInvalidDigits_thenViolationsOccur() {
        QuizQuestionUpdateInDTO dto = new QuizQuestionUpdateInDTO(
                "What is Java?",
                "MCQ_SINGLE",
                new BigDecimal("12345.123"), // invalid: more than 4 integer digits + 2 decimal
                "Explanation",
                true,
                1
        );

        Set<ConstraintViolation<QuizQuestionUpdateInDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "Expected 2 violations for digits and max value");

        assertTrue(violations.stream().allMatch(v -> v.getPropertyPath().toString().equals("points")));
    }

    @Test
    void testBuilderCreatesValidObject() {
        QuizQuestionUpdateInDTO dto = QuizQuestionUpdateInDTO.builder()
                .questionText("Describe OOP")
                .questionType("MCQ_SINGLE")
                .points(BigDecimal.valueOf(15.0))
                .explanation("Used in Java")
                .required(true)
                .position(3)
                .build();

        assertEquals("Describe OOP", dto.getQuestionText());
        assertEquals("MCQ_SINGLE", dto.getQuestionType());
        assertEquals(BigDecimal.valueOf(15.0), dto.getPoints());
        assertEquals("Used in Java", dto.getExplanation());
        assertTrue(dto.getRequired());
        assertEquals(3, dto.getPosition());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizQuestionUpdateInDTO dto1 = new QuizQuestionUpdateInDTO(
                "Q", "MCQ_SINGLE", BigDecimal.TEN, "Exp", true, 1
        );
        QuizQuestionUpdateInDTO dto2 = new QuizQuestionUpdateInDTO(
                "Q", "MCQ_SINGLE", BigDecimal.TEN, "Exp", true, 1
        );

        assertEquals(dto1, dto2, "Objects with same fields should be equal");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Hash codes should match for equal objects");
    }

    @Test
    void testNotEqualsWithDifferentDataOrNullOrOtherType() {
        QuizQuestionUpdateInDTO dto1 = new QuizQuestionUpdateInDTO(
                "Q", "MCQ_SINGLE", BigDecimal.ONE, "Exp", true, 1
        );
        QuizQuestionUpdateInDTO dto2 = new QuizQuestionUpdateInDTO(
                "Different", "SHORT_ANSWER", BigDecimal.TEN, "Diff", false, 2
        );

        assertNotEquals(dto1, dto2, "DTOs with different fields must not be equal");
        assertNotEquals(dto1, null, "DTO should not be equal to null");
        assertNotEquals(dto1, "Some String", "DTO should not be equal to another type");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        QuizQuestionUpdateInDTO dto = new QuizQuestionUpdateInDTO();

        dto.setQuestionText("Text");
        dto.setQuestionType("MCQ_SINGLE");
        dto.setPoints(BigDecimal.valueOf(5.5));
        dto.setExplanation("Some explanation");
        dto.setRequired(false);
        dto.setPosition(2);

        assertEquals("Text", dto.getQuestionText());
        assertEquals("MCQ_SINGLE", dto.getQuestionType());
        assertEquals(BigDecimal.valueOf(5.5), dto.getPoints());
        assertEquals("Some explanation", dto.getExplanation());
        assertFalse(dto.getRequired());
        assertEquals(2, dto.getPosition());
    }

    private String generateLongString(String base, int count) {
        StringBuilder builder = new StringBuilder(base.length() * count);
        for (int i = 0; i < count; i++) {
            builder.append(base);
        }
        return builder.toString();
    }
}
