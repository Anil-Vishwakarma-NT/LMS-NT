package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class QuizCreateInDTOTest {

    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenPassValidation() {
        QuizCreateInDTO dto = new QuizCreateInDTO(
                "course",
                1L,
                "My Quiz",
                "Descriptions",
                30,
                3,
                BigDecimal.valueOf(99.99),
                true,
                true,
                true,
                10
        );
        Set<ConstraintViolation<QuizCreateInDTO>> v = validator.validate(dto);
        assertTrue(v.isEmpty());
    }

    @Test
    void whenParentTypeInvalid_thenViolation() {
        var dto = new QuizCreateInDTO("invalid", 1L, "T", "", 0, 0, BigDecimal.valueOf(-1), null, null, null, -1);
        Set<ConstraintViolation<QuizCreateInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
        assertEquals(6, v.size()); // parentType, timeLimit, attemptsAllowed, passingScore, isActive, createdBy
    }

    @Test
    void whenParentIdNotPositive_thenViolation() {
        var dto = new QuizCreateInDTO("course", 0L, "T", "", null, 1, null, false, false, true, 1);
        Set<ConstraintViolation<QuizCreateInDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
        assertTrue(hasViolation(v, "parentId"));
    }

    @Test
    void whenTitleBlankOrLong_thenViolation() {
        var dto = new QuizCreateInDTO("bundle", 1L, "", "", null, 1, null, false, false, true, 1);
        Set<ConstraintViolation<QuizCreateInDTO>> v1 = validator.validate(dto);
        assertTrue(hasViolation(v1, "title"));

        String longTitle = new String(new char[256]).replace('\0', 'A');
        dto.setTitle(longTitle);
        Set<ConstraintViolation<QuizCreateInDTO>> v2 = validator.validate(dto);
        assertTrue(hasViolation(v2, "title"));
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        String longDesc = new String(new char[1001]).replace('\0', 'D');
        var dto = new QuizCreateInDTO("bundle", 1L, "T", longDesc, null, 1, null, false, false, true, 1);
        Set<ConstraintViolation<QuizCreateInDTO>> v = validator.validate(dto);
        assertTrue(hasViolation(v, "description"));
    }

    @Test
    void whenTimeLimitInvalid_thenViolation() {
        var dto = new QuizCreateInDTO("course", 1L, "T", "", 0, 1, null, false, false, true, 1);
        assertTrue(hasViolation(validator.validate(dto), "timeLimit"));

        dto.setTimeLimit(601);
        assertTrue(hasViolation(validator.validate(dto), "timeLimit"));
    }

    @Test
    void whenAttemptsAllowedInvalid_thenViolation() {
        var dto = new QuizCreateInDTO("course", 1L, "T", "", 10, 0, null, false, false, true, 1);
        assertTrue(hasViolation(validator.validate(dto), "attemptsAllowed"));

        dto.setAttemptsAllowed(11);
        assertTrue(hasViolation(validator.validate(dto), "attemptsAllowed"));
    }

    @Test
    void whenPassingScoreInvalid_thenViolation() {
        var dto = new QuizCreateInDTO("course", 1L, "T", "", 10, 1, BigDecimal.valueOf(-0.01), false, false, true, 1);
        assertTrue(hasViolation(validator.validate(dto), "passingScore"));

        dto.setPassingScore(BigDecimal.valueOf(10000));
        assertTrue(hasViolation(validator.validate(dto), "passingScore"));
    }

    @Test
    void whenIsActiveOrCreatedByInvalid_thenViolation() {
        var dto = new QuizCreateInDTO("course", 1L, "Title", "", 10, 1, null, false, false, null, 0);
        Set<ConstraintViolation<QuizCreateInDTO>> v = validator.validate(dto);
        assertTrue(hasViolation(v, "isActive"));
        assertTrue(hasViolation(v, "createdBy"));
    }

    @Test
    void testDefaultConstructorSetters() {
        QuizCreateInDTO dto = new QuizCreateInDTO();
        dto.setParentType("bundle");
        dto.setParentId(5L);
        dto.setTitle("Quiz");
        dto.setDescription("Desc");
        dto.setTimeLimit(5);
        dto.setAttemptsAllowed(2);
        dto.setPassingScore(BigDecimal.valueOf(50));
        dto.setRandomizeQuestions(true);
        dto.setShowResults(true);
        dto.setIsActive(false);
        dto.setCreatedBy(99);

        assertEquals("bundle", dto.getParentType());
        assertEquals(5L, dto.getParentId());
        assertEquals("Quiz", dto.getTitle());
        assertEquals("Desc", dto.getDescription());
        assertEquals(5, dto.getTimeLimit());
        assertEquals(2, dto.getAttemptsAllowed());
        assertEquals(BigDecimal.valueOf(50), dto.getPassingScore());
        assertTrue(dto.getRandomizeQuestions());
        assertTrue(dto.getShowResults());
        assertFalse(dto.getIsActive());
        assertEquals(99, dto.getCreatedBy());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizCreateInDTO a = new QuizCreateInDTO("course", 1L, "Q", "D", 10, 1, BigDecimal.ONE, false, false, true, 1);
        QuizCreateInDTO b = new QuizCreateInDTO("course", 1L, "Q", "D", 10, 1, BigDecimal.ONE, false, false, true, 1);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        QuizCreateInDTO c = new QuizCreateInDTO("bundle", 2L, "X", "Y", 20, 2, BigDecimal.TEN, true, true, false, 2);
        assertNotEquals(a, c);
    }

    @Test
    void testEqualsWithNullAndOtherClass() {
        QuizCreateInDTO dto = new QuizCreateInDTO("course", 1L, "Q", "D", 10, 1, BigDecimal.ONE, false, false, true, 1);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    /**
     * Utility to simplify violation-checking
     */
    private boolean hasViolation(Set<ConstraintViolation<QuizCreateInDTO>> set, String property) {
        return set.stream().anyMatch(v -> v.getPropertyPath().toString().equals(property));
    }
}

