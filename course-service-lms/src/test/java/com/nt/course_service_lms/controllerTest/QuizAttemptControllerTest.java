package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.QuizAttemptController;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.service.QuizAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuizAttemptControllerTest {

    @InjectMocks
    private QuizAttemptController controller;

    @Mock
    private QuizAttemptService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQuizAttempt() {
        QuizAttemptCreateInDTO dto = new QuizAttemptCreateInDTO();
        dto.setUserId(1L);
        dto.setQuizId(100L);

        QuizAttemptOutDTO out = new QuizAttemptOutDTO();
        when(service.createQuizAttempt(dto)).thenReturn(out);

        ResponseEntity<QuizAttemptOutDTO> response = controller.createQuizAttempt(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateQuizAttempt() {
        Long id = 1L;
        QuizAttemptUpdateInDTO dto = new QuizAttemptUpdateInDTO();
        QuizAttemptOutDTO out = new QuizAttemptOutDTO();

        when(service.updateQuizAttempt(id, dto)).thenReturn(out);

        ResponseEntity<QuizAttemptOutDTO> response = controller.updateQuizAttempt(id, dto);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetQuizAttemptById_Found() {
        Long id = 1L;
        QuizAttemptOutDTO out = new QuizAttemptOutDTO();
        when(service.getQuizAttemptById(id)).thenReturn(Optional.of(out));

        ResponseEntity<QuizAttemptOutDTO> response = controller.getQuizAttemptById(id);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetQuizAttemptById_NotFound() {
        when(service.getQuizAttemptById(100L)).thenReturn(Optional.empty());

        ResponseEntity<QuizAttemptOutDTO> response = controller.getQuizAttemptById(100L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetAllQuizAttempts() {
        Page<QuizAttemptOutDTO> page = new PageImpl<>(Collections.singletonList(new QuizAttemptOutDTO()));
        when(service.getAllQuizAttempts(any())).thenReturn(page);

        ResponseEntity<Page<QuizAttemptOutDTO>> response = controller.getAllQuizAttempts(PageRequest.of(0, 20));
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetQuizAttemptsByUserId() {
        when(service.getQuizAttemptsByUserId(1L)).thenReturn(Collections.singletonList(new QuizAttemptOutDTO()));
        ResponseEntity<List<QuizAttemptOutDTO>> response = controller.getQuizAttemptsByUserId(1L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetQuizAttemptsByQuizId() {
        when(service.getQuizAttemptsByQuizId(100L)).thenReturn(Collections.singletonList(new QuizAttemptOutDTO()));
        ResponseEntity<List<QuizAttemptOutDTO>> response = controller.getQuizAttemptsByQuizId(100L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetQuizAttemptsByUserAndQuiz() {
        when(service.getQuizAttemptsByUserAndQuiz(1L, 100L)).thenReturn(Collections.singletonList(new QuizAttemptOutDTO()));
        ResponseEntity<List<QuizAttemptOutDTO>> response = controller.getQuizAttemptsByUserAndQuiz(1L, 100L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetQuizAttemptsByStatus() {
        when(service.getQuizAttemptsByStatus("COMPLETED")).thenReturn(Collections.singletonList(new QuizAttemptOutDTO()));
        ResponseEntity<List<QuizAttemptOutDTO>> response = controller.getQuizAttemptsByStatus("COMPLETED");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetLatestAttemptByUserAndQuiz_Found() {
        when(service.getLatestAttemptByUserAndQuiz(1L, 100L)).thenReturn(Optional.of(new QuizAttemptOutDTO()));
        ResponseEntity<QuizAttemptOutDTO> response = controller.getLatestAttemptByUserAndQuiz(1L, 100L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetLatestAttemptByUserAndQuiz_NotFound() {
        when(service.getLatestAttemptByUserAndQuiz(1L, 100L)).thenReturn(Optional.empty());
        ResponseEntity<QuizAttemptOutDTO> response = controller.getLatestAttemptByUserAndQuiz(1L, 100L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteQuizAttempt() {
        ResponseEntity<Void> response = controller.deleteQuizAttempt(1L);
        verify(service).deleteQuizAttempt(1L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testCompleteAttempt() {
        QuizAttemptOutDTO dto = new QuizAttemptOutDTO();
        when(service.completeAttempt(1L, "details")).thenReturn(dto);
        ResponseEntity<QuizAttemptOutDTO> response = controller.completeAttempt(1L, "details");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testAbandonAttempt() {
        when(service.abandonAttempt(1L)).thenReturn(new QuizAttemptOutDTO());
        ResponseEntity<QuizAttemptOutDTO> response = controller.abandonAttempt(1L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testTimeOutAttempt() {
        when(service.timeOutAttempt(1L)).thenReturn(new QuizAttemptOutDTO());
        ResponseEntity<QuizAttemptOutDTO> response = controller.timeOutAttempt(1L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCheckQuizAttemptExists_True() {
        when(service.existsById(1L)).thenReturn(true);
        ResponseEntity<Void> response = controller.checkQuizAttemptExists(1L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCheckQuizAttemptExists_False() {
        when(service.existsById(999L)).thenReturn(false);
        ResponseEntity<Void> response = controller.checkQuizAttemptExists(999L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCountAttemptsByUserAndQuiz() {
        when(service.countAttemptsByUserAndQuiz(1L, 100L)).thenReturn(3L);
        ResponseEntity<Long> response = controller.countAttemptsByUserAndQuiz(1L, 100L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3L, response.getBody());
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<String> response = controller.healthCheck();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("QuizAttempt Controller is healthy", response.getBody());
    }
}
