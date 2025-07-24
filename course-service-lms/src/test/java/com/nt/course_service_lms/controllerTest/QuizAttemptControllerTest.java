package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.QuizAttemptController;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.service.QuizAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizAttemptController.class)
@Import(QuizAttemptControllerTest.TestConfig.class)
public class QuizAttemptControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public QuizAttemptService quizAttemptService() {
            return Mockito.mock(QuizAttemptService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizAttemptService quizAttemptService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuizAttemptOutDTO sampleAttempt;

    @BeforeEach
    void setup() {
        sampleAttempt = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .quizId(100L)
                .userId(200L)
                .attempt(1L)
                .attemptsLeft(2L)
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now().plusMinutes(5))
                .scoreDetails("{\"score\": 80}")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateQuizAttempt() throws Exception {
        QuizAttemptCreateInDTO input = new QuizAttemptCreateInDTO(100L, 200L);

        Mockito.when(quizAttemptService.createQuizAttempt(any())).thenReturn(sampleAttempt);

        mockMvc.perform(post("/quiz-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quizAttemptId").value(sampleAttempt.getQuizAttemptId()));
    }

    @Test
    void testCreateQuizAttemptValidationError() throws Exception {
        QuizAttemptCreateInDTO input = new QuizAttemptCreateInDTO(null, -5L);

        mockMvc.perform(post("/quiz-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateQuizAttempt() throws Exception {
        QuizAttemptUpdateInDTO input = QuizAttemptUpdateInDTO.builder()
                .status("COMPLETED")
                .scoreDetails("{\"score\": 90}")
                .finishedAt(LocalDateTime.now())
                .build();

        Mockito.when(quizAttemptService.updateQuizAttempt(eq(1L), any())).thenReturn(sampleAttempt);

        mockMvc.perform(put("/quiz-attempts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testGetQuizAttemptById() throws Exception {
        Mockito.when(quizAttemptService.getQuizAttemptById(1L)).thenReturn(Optional.of(sampleAttempt));

        mockMvc.perform(get("/quiz-attempts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(sampleAttempt.getQuizId()));
    }

    @Test
    void testGetQuizAttemptByIdNotFound() throws Exception {
        Mockito.when(quizAttemptService.getQuizAttemptById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/quiz-attempts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllQuizAttempts() throws Exception {
        Page<QuizAttemptOutDTO> page = new PageImpl<>(Arrays.asList(sampleAttempt));
        Mockito.when(quizAttemptService.getAllQuizAttempts(any())).thenReturn(page);

        mockMvc.perform(get("/quiz-attempts?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quizAttemptId").value(sampleAttempt.getQuizAttemptId()));
    }

    @Test
    void testGetByUserId() throws Exception {
        Mockito.when(quizAttemptService.getQuizAttemptsByUserId(200L)).thenReturn(Arrays.asList(sampleAttempt));

        mockMvc.perform(get("/quiz-attempts/user/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(200L));
    }

    @Test
    void testGetByQuizId() throws Exception {
        Mockito.when(quizAttemptService.getQuizAttemptsByQuizId(100L)).thenReturn(Arrays.asList(sampleAttempt));

        mockMvc.perform(get("/quiz-attempts/quiz/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quizId").value(100L));
    }

    @Test
    void testGetByUserAndQuiz() throws Exception {
        Mockito.when(quizAttemptService.getQuizAttemptsByUserAndQuiz(200L, 100L)).thenReturn(Arrays.asList(sampleAttempt));

        mockMvc.perform(get("/quiz-attempts/user/200/quiz/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quizAttemptId").value(1L));
    }

    @Test
    void testGetByStatus() throws Exception {
        Mockito.when(quizAttemptService.getQuizAttemptsByStatus("COMPLETED")).thenReturn(Arrays.asList(sampleAttempt));

        mockMvc.perform(get("/quiz-attempts/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void testGetLatestAttempt() throws Exception {
        Mockito.when(quizAttemptService.getLatestAttemptByUserAndQuiz(200L, 100L)).thenReturn(Optional.of(sampleAttempt));

        mockMvc.perform(get("/quiz-attempts/latest")
                        .param("userId", "200")
                        .param("quizId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L));
    }

    @Test
    void testGetLatestAttemptNotFound() throws Exception {
        Mockito.when(quizAttemptService.getLatestAttemptByUserAndQuiz(200L, 999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/quiz-attempts/latest")
                        .param("userId", "200")
                        .param("quizId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteQuizAttempt() throws Exception {
        mockMvc.perform(delete("/quiz-attempts/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(quizAttemptService).deleteQuizAttempt(1L);
    }

    @Test
    void testCompleteAttempt() throws Exception {
        Mockito.when(quizAttemptService.completeAttempt(eq(1L), anyString())).thenReturn(sampleAttempt);

        mockMvc.perform(put("/quiz-attempts/1/complete")
                        .param("scoreDetails", "{\"score\": 90}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testAbandonAttempt() throws Exception {
        Mockito.when(quizAttemptService.abandonAttempt(1L)).thenReturn(sampleAttempt);

        mockMvc.perform(put("/quiz-attempts/1/abandon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testTimeoutAttempt() throws Exception {
        Mockito.when(quizAttemptService.timeOutAttempt(1L)).thenReturn(sampleAttempt);

        mockMvc.perform(put("/quiz-attempts/1/timeout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}


