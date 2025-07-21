package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.QuizQuestionController;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.QuizQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuizQuestionControllerTest {

    @InjectMocks
    private QuizQuestionController quizQuestionController;

    @Mock
    private QuizQuestionService quizQuestionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQuestion() {
        QuizQuestionInDTO input = new QuizQuestionInDTO();
        input.setQuizId(1L);

        QuizQuestionOutDTO output = new QuizQuestionOutDTO();
        output.setQuestionId(100L);

        when(quizQuestionService.createQuestion(input)).thenReturn(output);

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = quizQuestionController.createQuestion(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(100L, response.getBody().getData().getQuestionId());
    }

    @Test
    void testGetAllQuestionsByQuizId() {
        Long quizId = 1L;
        QuizQuestionOutDTO q1 = new QuizQuestionOutDTO(); q1.setQuestionId(1L);
        QuizQuestionOutDTO q2 = new QuizQuestionOutDTO(); q2.setQuestionId(2L);

        when(quizQuestionService.getQuestionsByQuizId(quizId)).thenReturn(Arrays.asList(q1, q2));

        ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> response = quizQuestionController.getAllQuestionsByQuizId(quizId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllQuestionsByQuizId_Empty() {
        Long quizId = 2L;
        when(quizQuestionService.getQuestionsByQuizId(quizId)).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> response = quizQuestionController.getAllQuestionsByQuizId(quizId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetQuestionById() {
        Long questionId = 5L;
        QuizQuestionOutDTO dto = new QuizQuestionOutDTO();
        dto.setQuestionId(questionId);

        when(quizQuestionService.getQuestionById(questionId)).thenReturn(dto);

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = quizQuestionController.getQuestionById(questionId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(questionId, response.getBody().getData().getQuestionId());
    }

    @Test
    void testUpdateQuestion() {
        Long questionId = 10L;
        UpdateQuizQuestionInDTO updateDTO = new UpdateQuizQuestionInDTO();

        QuizQuestionOutDTO updated = new QuizQuestionOutDTO();
        updated.setQuestionId(questionId);

        when(quizQuestionService.updateQuestion(questionId, updateDTO)).thenReturn(updated);

        ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> response = quizQuestionController.updateQuestion(questionId, updateDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(questionId, response.getBody().getData().getQuestionId());
    }

    @Test
    void testDeleteQuestion() {
        Long questionId = 20L;
        doNothing().when(quizQuestionService).deleteQuestion(questionId);

        ResponseEntity<StandardResponseOutDTO<Void>> response = quizQuestionController.deleteQuestion(questionId);

        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody().getData());
        verify(quizQuestionService, times(1)).deleteQuestion(questionId);
    }
}

