package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.QuizQuestionController;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.service.QuizQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizQuestionController.class)
@Import(QuizQuestionControllerTest.TestConfig.class)
class QuizQuestionControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public QuizQuestionService quizQuestionService() {
            return Mockito.mock(QuizQuestionService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizQuestionService quizQuestionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuizQuestionOutDTO sampleOutDTO;

    @BeforeEach
    void setUp() {
        sampleOutDTO = QuizQuestionOutDTO.builder()
                .questionId(1L)
                .quizId(101L)
                .questionText("What is 2 + 2?")
                .questionType("MCQ_SINGLE")
                .options("[\"2\",\"3\",\"4\"]")
                .correctAnswer("4")
                .points(new BigDecimal("5.0"))
                .explanation("Simple math")
                .required(true)
                .position(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createQuestion_success() throws Exception {
        QuizQuestionInDTO input = QuizQuestionInDTO.builder()
                .quizId(101L)
                .questionText("What is 2 + 2?")
                .questionType("MCQ_SINGLE")
                .options("[\"2\",\"3\",\"4\"]")
                .correctAnswer("4")
                .points(new BigDecimal("5.0"))
                .explanation("Simple math")
                .required(true)
                .build();

        when(quizQuestionService.createQuestion(any())).thenReturn(sampleOutDTO);

        mockMvc.perform(post("/quiz-questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(sampleOutDTO.getQuestionId()));
    }

    @Test
    void createQuestion_validationError() throws Exception {
        QuizQuestionInDTO invalidInput = new QuizQuestionInDTO(); // Missing required fields

        mockMvc.perform(post("/quiz-questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllQuestions_success() throws Exception {
        when(quizQuestionService.getAllQuestions()).thenReturn(Arrays.asList(sampleOutDTO));

        mockMvc.perform(get("/quiz-questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionId").value(sampleOutDTO.getQuestionId()));
    }

    @Test
    void getQuestionById_success() throws Exception {
        when(quizQuestionService.getQuestionById(1L)).thenReturn(sampleOutDTO);

        mockMvc.perform(get("/quiz-questions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("What is 2 + 2?"));
    }

    @Test
    void getQuestionById_notFound() throws Exception {
        when(quizQuestionService.getQuestionById(999L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/quiz-questions/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getQuestionsByQuizId_success() throws Exception {
        when(quizQuestionService.getQuestionsByQuizId(101L)).thenReturn(Arrays.asList(sampleOutDTO));

        mockMvc.perform(get("/quiz-questions/quiz/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quizId").value(101L));
    }

    @Test
    void updateQuestion_success() throws Exception {
        QuizQuestionUpdateInDTO updateDTO = QuizQuestionUpdateInDTO.builder()
                .questionText("Updated question?")
                .questionType("MCQ_SINGLE")
                .points(new BigDecimal("10.0"))
                .required(true)
                .explanation("Updated explanation")
                .position(1)
                .build();

        when(quizQuestionService.updateQuestion(eq(1L), any())).thenReturn(sampleOutDTO);

        mockMvc.perform(put("/quiz-questions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(1L));
    }

    @Test
    void updateQuestion_validationError() throws Exception {
        QuizQuestionUpdateInDTO invalidDTO = new QuizQuestionUpdateInDTO(); // Missing required fields

        mockMvc.perform(put("/quiz-questions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteQuestion_success() throws Exception {
        mockMvc.perform(delete("/quiz-questions/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteQuestion_internalError() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(quizQuestionService).deleteQuestion(999L);

        mockMvc.perform(delete("/quiz-questions/999"))
                .andExpect(status().isInternalServerError());
    }
}


