package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.QuizController;
import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.QuizService;
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

public class QuizControllerTest {

    @InjectMocks
    private QuizController quizController;

    @Mock
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQuiz() {
        QuizCreateInDTO input = new QuizCreateInDTO();
        input.setTitle("Quiz 1");

        QuizOutDTO output = new QuizOutDTO();
        output.setTitle("Quiz 1");

        when(quizService.createQuiz(input)).thenReturn(output);

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = quizController.createQuiz(input);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Quiz 1", response.getBody().getData().getTitle());
    }

    @Test
    void testGetAllQuizzes() {
        QuizOutDTO q1 = new QuizOutDTO(); q1.setTitle("Q1");
        QuizOutDTO q2 = new QuizOutDTO(); q2.setTitle("Q2");

        when(quizService.getAllQuizzes()).thenReturn(Arrays.asList(q1, q2));

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = quizController.getAllQuizzes();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllQuizzesEmpty() {
        when(quizService.getAllQuizzes()).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = quizController.getAllQuizzes();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetQuizById() {
        Long id = 1L;
        QuizOutDTO dto = new QuizOutDTO();
        dto.setQuizId(id);

        when(quizService.getQuizById(id)).thenReturn(dto);

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = quizController.getQuizById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(id, response.getBody().getData().getQuizId());
    }

    @Test
    void testGetQuizzesByCourse() {
        Long courseId = 5L;
        QuizOutDTO dto = new QuizOutDTO();
        dto.setTitle("Course Quiz");

        when(quizService.getQuizzesByCourse(courseId)).thenReturn(Collections.singletonList(dto));

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = quizController.getQuizzesByCourse(courseId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Course Quiz", response.getBody().getData().get(0).getTitle());
    }

    @Test
    void testGetQuizzesByCourseContent() {
        Long contentId = 10L;
        QuizOutDTO dto = new QuizOutDTO();
        dto.setTitle("Content Quiz");

        when(quizService.getQuizzesByCourseContent(contentId)).thenReturn(Collections.singletonList(dto));

        ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> response = quizController.getQuizzesByCourseContent(contentId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Content Quiz", response.getBody().getData().get(0).getTitle());
    }

    @Test
    void testUpdateQuiz() {
        Long id = 3L;
        QuizUpdateInDTO update = new QuizUpdateInDTO();
        update.setTitle("Updated Title");

        QuizOutDTO updated = new QuizOutDTO();
        updated.setTitle("Updated Title");

        when(quizService.updateQuiz(id, update)).thenReturn(updated);

        ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> response = quizController.updateQuiz(id, update);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Title", response.getBody().getData().getTitle());
    }

    @Test
    void testDeleteQuiz() {
        Long id = 9L;

        doNothing().when(quizService).deleteQuiz(id);

        ResponseEntity<StandardResponseOutDTO<String>> response = quizController.deleteQuiz(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Quiz deleted successfully", response.getBody().getData());
        verify(quizService, times(1)).deleteQuiz(id);
    }
}
