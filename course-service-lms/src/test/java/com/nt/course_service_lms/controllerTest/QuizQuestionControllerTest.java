package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.QuizQuestionController;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.service.QuizQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizQuestionController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class QuizQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizQuestionService quizQuestionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private QuizQuestionInDTO quizQuestionInDTO;
    private UpdateQuizQuestionInDTO updateQuizQuestionInDTO;
    private QuizQuestionOutDTO quizQuestionOutDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        quizQuestionInDTO = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("What is the capital of France?")
                .questionType("MCQ_SINGLE")
                .options("[{\"id\":\"a\",\"text\":\"London\"},{\"id\":\"b\",\"text\":\"Paris\"},{\"id\":\"c\",\"text\":\"Berlin\"},{\"id\":\"d\",\"text\":\"Madrid\"}]")
                .correctAnswer("b")
                .points(new BigDecimal("10.00"))
                .explanation("Paris is the capital city of France")
                .required(true)
                .build();

        updateQuizQuestionInDTO = UpdateQuizQuestionInDTO.builder()
                .questionText("What is the capital of Germany?")
                .questionType("MCQ_SINGLE")
                .options("[{\"id\":\"a\",\"text\":\"London\"},{\"id\":\"b\",\"text\":\"Paris\"},{\"id\":\"c\",\"text\":\"Berlin\"},{\"id\":\"d\",\"text\":\"Madrid\"}]")
                .correctAnswer("c")
                .points(new BigDecimal("15.00"))
                .explanation("Berlin is the capital city of Germany")
                .required(false)
                .position(1)
                .build();

        quizQuestionOutDTO = QuizQuestionOutDTO.builder()
                .questionId(1L)
                .quizId(1L)
                .questionText("What is the capital of France?")
                .questionType("MCQ_SINGLE")
                .options("[{\"id\":\"a\",\"text\":\"London\"},{\"id\":\"b\",\"text\":\"Paris\"},{\"id\":\"c\",\"text\":\"Berlin\"},{\"id\":\"d\",\"text\":\"Madrid\"}]")
                .correctAnswer("b")
                .points(new BigDecimal("10.00"))
                .explanation("Paris is the capital city of France")
                .required(true)
                .position(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createQuestion_ShouldReturnCreatedQuestion_WhenValidInput() throws Exception {
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class))).thenReturn(quizQuestionOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.questionId").value(1L));
    }

    @Test
    void createQuestion_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        QuizQuestionInDTO invalidQuestion = QuizQuestionInDTO.builder().questionText("").build();

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuestion)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuestion_ShouldReturnNotFound_WhenQuizNotExists() throws Exception {
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quiz not found"));

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createQuestion_ShouldReturnBadRequest_WhenInvalidQuestionData() throws Exception {
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class)))
                .thenThrow(new ResourceNotValidException("Invalid data"));

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuestion_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllQuestionsByQuizId_ShouldReturnQuestions_WhenEmployeeAccess() throws Exception {
        List<QuizQuestionOutDTO> questions = Collections.singletonList(quizQuestionOutDTO);
        when(quizQuestionService.getQuestionsByQuizId(1L)).thenReturn(questions);

        mockMvc.perform(get("/api/service-api/quiz-questions/quiz/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].questionId").value(1L));
    }

    @Test
    void getAllQuestionsByQuizId_ShouldReturnNotFound_WhenNoQuestionsExist() throws Exception {
        when(quizQuestionService.getQuestionsByQuizId(999L))
                .thenThrow(new ResourceNotFoundException("No Questions Found"));

        mockMvc.perform(get("/api/service-api/quiz-questions/quiz/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuestionById_ShouldReturnQuestion_WhenEmployeeAccess() throws Exception {
        when(quizQuestionService.getQuestionById(1L)).thenReturn(quizQuestionOutDTO);

        mockMvc.perform(get("/api/service-api/quiz-questions/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionId").value(1L));
    }

    @Test
    void updateQuestion_ShouldReturnUpdatedQuestion_WhenValidInput() throws Exception {
        quizQuestionOutDTO.setQuestionText("What is the capital of Germany?");
        when(quizQuestionService.updateQuestion(anyLong(), any(UpdateQuizQuestionInDTO.class)))
                .thenReturn(quizQuestionOutDTO);

        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuizQuestionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionText").value("What is the capital of Germany?"));
    }

    @Test
    void updateQuestion_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuizQuestionInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteQuestion_ShouldReturnSuccess_WhenQuestionExists() throws Exception {
        doNothing().when(quizQuestionService).deleteQuestion(1L);

        mockMvc.perform(delete("/api/service-api/quiz-questions/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Question deleted successfully"));
    }

    @Test
    void deleteQuestion_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/service-api/quiz-questions/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createQuestion_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createQuestion_ShouldHandleShortAnswerQuestion() throws Exception {
        QuizQuestionInDTO shortAnswerQuestion = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("What is the chemical symbol for water?")
                .questionType("SHORT_ANSWER")
                .correctAnswer("H2O")
                .points(new BigDecimal("5.00"))
                .required(true)
                .build();

        QuizQuestionOutDTO shortAnswerResponse = QuizQuestionOutDTO.builder()
                .questionId(2L)
                .questionType("SHORT_ANSWER")
                .build();

        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class))).thenReturn(shortAnswerResponse);

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortAnswerQuestion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.questionType").value("SHORT_ANSWER"));
    }

    @Test
    void createQuestion_ShouldHandleMultipleChoiceMultipleQuestion() throws Exception {
        QuizQuestionInDTO mcqMultipleQuestion = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("Which of the following are programming languages?")
                .questionType("MCQ_MULTIPLE")
                .correctAnswer("[\"a\",\"b\",\"d\"]")
                .points(new BigDecimal("20.00"))
                .required(true)
                .build();

        QuizQuestionOutDTO mcqMultipleResponse = QuizQuestionOutDTO.builder()
                .questionId(3L)
                .questionType("MCQ_MULTIPLE")
                .points(new BigDecimal("20.00"))
                .build();

        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class))).thenReturn(mcqMultipleResponse);

        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mcqMultipleQuestion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.questionType").value("MCQ_MULTIPLE"))
                .andExpect(jsonPath("$.data.points").value(20.00));
    }
}