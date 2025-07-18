package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizSubmissionInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizSubmissionInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        UserResponseInDTO validResponse = new UserResponseInDTO(
                1L, 1L, 1L, 1L, "{\"selected\":\"A\"}", LocalDateTime.now()
        );

        QuizSubmissionInDTO dto = new QuizSubmissionInDTO(
                Arrays.asList(validResponse),
                "Some notes",
                300L
        );

        Set<ConstraintViolation<QuizSubmissionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUserResponseInvalid_thenViolationsTriggeredDueToValidAnnotation() {
        UserResponseInDTO invalidResponse = new UserResponseInDTO(
                null,  // userId missing
                null,  // quizId missing
                null,  // questionId missing
                null,  // attempt missing
                "",    // userAnswer blank
                null   // answeredAt is optional
        );

        QuizSubmissionInDTO dto = new QuizSubmissionInDTO(
                Arrays.asList(invalidResponse),
                "Some notes",
                150L
        );

        Set<ConstraintViolation<QuizSubmissionInDTO>> violations = validator.validate(dto);

        // Expect violations for each invalid field inside UserResponseInDTO
        assertFalse(violations.isEmpty());
        assertEquals(5, violations.size()); // answeredAt is optional, others are required
        assertTrue(violations.stream().allMatch(v -> v.getPropertyPath().toString().contains("userResponses")));
    }

    @Test
    void whenUserResponsesNull_thenNoViolationsBecauseNotAnnotatedWithNotNull() {
        QuizSubmissionInDTO dto = new QuizSubmissionInDTO(
                null,
                "Some notes",
                150L
        );

        Set<ConstraintViolation<QuizSubmissionInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "List is allowed to be null unless @NotNull is used");
    }

    @Test
    void testBuilderCreatesValidQuizSubmission() {
        UserResponseInDTO response = new UserResponseInDTO(
                1L, 2L, 3L, 4L, "{\"response\":\"C\"}", LocalDateTime.now()
        );

        QuizSubmissionInDTO dto = QuizSubmissionInDTO.builder()
                .userResponses(Arrays.asList(response))
                .notes("Attempt via builder")
                .timeSpent(120L)
                .build();

        assertEquals("Attempt via builder", dto.getNotes());
        assertEquals(120L, dto.getTimeSpent());
        assertEquals(1, dto.getUserResponses().size());
        assertEquals(response, dto.getUserResponses().get(0));
    }

    @Test
    void testDefaultConstructorAndSetters() {
        QuizSubmissionInDTO dto = new QuizSubmissionInDTO();

        List<UserResponseInDTO> responses = Arrays.asList(new UserResponseInDTO(
                1L, 1L, 1L, 1L, "{\"ans\":\"B\"}", LocalDateTime.now()
        ));

        dto.setUserResponses(responses);
        dto.setNotes("Final remarks");
        dto.setTimeSpent(400L);

        assertEquals(responses, dto.getUserResponses());
        assertEquals("Final remarks", dto.getNotes());
        assertEquals(400L, dto.getTimeSpent());
    }

    @Test
    void testEqualsAndHashCode() {
        List<UserResponseInDTO> responses = Arrays.asList(new UserResponseInDTO(
                1L, 1L, 1L, 1L, "{\"ans\":\"A\"}", LocalDateTime.now()
        ));

        QuizSubmissionInDTO dto1 = new QuizSubmissionInDTO(responses, "Note", 200L);
        QuizSubmissionInDTO dto2 = new QuizSubmissionInDTO(responses, "Note", 200L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        List<UserResponseInDTO> responses1 = Arrays.asList(new UserResponseInDTO(
                1L, 1L, 1L, 1L, "{\"ans\":\"A\"}", LocalDateTime.now()
        ));
        List<UserResponseInDTO> responses2 = Arrays.asList(new UserResponseInDTO(
                2L, 2L, 2L, 2L, "{\"ans\":\"B\"}", LocalDateTime.now()
        ));

        QuizSubmissionInDTO dto1 = new QuizSubmissionInDTO(responses1, "Note", 200L);
        QuizSubmissionInDTO dto2 = new QuizSubmissionInDTO(responses2, "Different", 100L);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "String");
    }
}

