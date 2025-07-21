package com.nt.course_service_lms.converterTest;

import com.nt.course_service_lms.converters.QuizConverter;
import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.entity.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizConverterTest {

    private QuizConverter quizConverter;

    @BeforeEach
    void setUp() {
        quizConverter = new QuizConverter();
    }

    @Test
    void testToEntity_AllFields() {
        QuizCreateInDTO dto = new QuizCreateInDTO();
        dto.setParentType("course");
        dto.setParentId(1L);
        dto.setTitle("Java Quiz");
        dto.setDescription("Basic Java Quiz");
        dto.setTimeLimit(30);
        dto.setAttemptsAllowed(2);
        dto.setPassingScore(new BigDecimal("70.50"));
        dto.setRandomizeQuestions(true);
        dto.setShowResults(true);
        dto.setIsActive(true);
        dto.setCreatedBy(101);

        Quiz result = quizConverter.toEntity(dto);

        assertNotNull(result);
        assertEquals("course", result.getParentType());
        assertEquals(1L, result.getParentId());
        assertEquals("Java Quiz", result.getTitle());
        assertEquals("Basic Java Quiz", result.getDescription());
        assertEquals(30, result.getTimeLimit());
        assertEquals(2, result.getAttemptsAllowed());
        assertEquals(new BigDecimal("70.50"), result.getPassingScore());
        assertTrue(result.getRandomizeQuestions());
        assertTrue(result.getShowResults());
        assertTrue(result.getIsActive());
        assertEquals(101, result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testToEntity_NullDTO() {
        Quiz result = quizConverter.toEntity(null);
        assertNull(result);
    }

    @Test
    void testUpdateEntity_AllFields() {
        Quiz existing = new Quiz();
        existing.setTitle("Old Title");

        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setTitle("New Title");
        dto.setDescription("Updated Description");
        dto.setTimeLimit(45);
        dto.setAttemptsAllowed(3);
        dto.setPassingScore(new BigDecimal("80.00"));
        dto.setRandomizeQuestions(false);
        dto.setShowResults(false);
        dto.setIsActive(false);

        Quiz updated = quizConverter.updateEntity(existing, dto);

        assertEquals("New Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(45, updated.getTimeLimit());
        assertEquals(3, updated.getAttemptsAllowed());
        assertEquals(new BigDecimal("80.00"), updated.getPassingScore());
        assertFalse(updated.getRandomizeQuestions());
        assertFalse(updated.getShowResults());
        assertFalse(updated.getIsActive());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testUpdateEntity_PartialFields() {
        Quiz existing = new Quiz();
        existing.setTitle("Original Title");
        existing.setDescription("Original Desc");

        QuizUpdateInDTO dto = new QuizUpdateInDTO(); // all fields null

        Quiz result = quizConverter.updateEntity(existing, dto);

        assertEquals("Original Title", result.getTitle());
        assertEquals("Original Desc", result.getDescription());
    }

    @Test
    void testUpdateEntity_NullEntity() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        assertNull(quizConverter.updateEntity(null, dto));
    }

    @Test
    void testUpdateEntity_NullDTO() {
        Quiz quiz = new Quiz();
        Quiz result = quizConverter.updateEntity(quiz, null);
        assertEquals(quiz, result);
    }

    @Test
    void testToOutDTO_FullFields() {
        Quiz quiz = new Quiz();
        quiz.setQuizId(100L);
        quiz.setParentType("bundle");
        quiz.setParentId(5L);
        quiz.setTitle("Quiz Title");
        quiz.setDescription("Quiz Description");
        quiz.setTimeLimit(25);
        quiz.setAttemptsAllowed(2);
        quiz.setPassingScore(new BigDecimal("75.00"));
        quiz.setRandomizeQuestions(true);
        quiz.setShowResults(false);
        quiz.setIsActive(true);
        quiz.setCreatedBy(200);
        quiz.setCreatedAt(LocalDateTime.now().minusDays(1));
        quiz.setUpdatedAt(LocalDateTime.now());

        QuizOutDTO dto = quizConverter.toOutDTO(quiz);

        assertEquals(100L, dto.getQuizId());
        assertEquals("bundle", dto.getParentType());
        assertEquals(5L, dto.getParentId());
        assertEquals("Quiz Title", dto.getTitle());
        assertEquals("Quiz Description", dto.getDescription());
        assertEquals(25, dto.getTimeLimit());
        assertEquals(2, dto.getAttemptsAllowed());
        assertEquals(new BigDecimal("75.00"), dto.getPassingScore());
        assertTrue(dto.getRandomizeQuestions());
        assertFalse(dto.getShowResults());
        assertTrue(dto.getIsActive());
        assertEquals(200, dto.getCreatedBy());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    void testToOutDTO_Null() {
        assertNull(quizConverter.toOutDTO(null));
    }
}
