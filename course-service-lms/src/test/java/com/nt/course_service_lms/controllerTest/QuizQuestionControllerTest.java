//package com.nt.course_service_lms.controllerTest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nt.course_service_lms.controller.QuizQuestionController;
//import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
//import com.nt.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
//import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
//import com.nt.course_service_lms.service.QuizQuestionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(QuizQuestionController.class)
//@Import(QuizQuestionControllerTest.TestConfig.class)
//class QuizQuestionControllerTest {
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public QuizQuestionService quizQuestionService() {
//            return Mockito.mock(QuizQuestionService.class);
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private QuizQuestionService quizQuestionService;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private QuizQuestionOutDTO sampleOutDTO;
//
//    @BeforeEach
//    void setUp() {
//        sampleOutDTO = QuizQuestionOutDTO.builder()
//                .questionId(1L)
//                .quizId(101L)
//                .questionText("What is 2 + 2?")
//                .questionType("MCQ_SINGLE")
//                .options("[\"2\",\"3\",\"4\"]")
//                .correctAnswer("4")
//                .points(new BigDecimal("5.0"))
//                .explanation("Simple math")
//                .required(true)
//                .position(1)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    void createQuestion_success() throws Exception {
//        QuizQuestionInDTO input = QuizQuestionInDTO.builder()
//                .quizId(101L)
//                .questionText("What is 2 + 2?")
//                .questionType("MCQ_SINGLE")
//                .options("[\"2\",\"3\",\"4\"]")
//                .correctAnswer("4")
//                .points(new BigDecimal("5.0"))
//                .explanation("Simple math")
//                .required(true)
//                .build();
//
//        when(quizQuestionService.createQuestion(any())).thenReturn(sampleOutDTO);
//
//        mockMvc.perform(post("/quiz-questions")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(input)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.questionId").value(sampleOutDTO.getQuestionId()));
//    }
//
//    @Test
//    void createQuestion_validationError() throws Exception {
//        QuizQuestionInDTO invalidInput = new QuizQuestionInDTO(); // Missing required fields
//
//        mockMvc.perform(post("/quiz-questions")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidInput)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void getAllQuestions_success() throws Exception {
//        when(quizQuestionService.getAllQuestions()).thenReturn(Arrays.asList(sampleOutDTO));
//
//        mockMvc.perform(get("/quiz-questions"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].questionId").value(sampleOutDTO.getQuestionId()));
//    }
//
//    @Test
//    void getQuestionById_success() throws Exception {
//        when(quizQuestionService.getQuestionById(1L)).thenReturn(sampleOutDTO);
//
//        mockMvc.perform(get("/quiz-questions/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.questionText").value("What is 2 + 2?"));
//    }
//
//    @Test
//    void getQuestionById_notFound() throws Exception {
//        when(quizQuestionService.getQuestionById(999L)).thenThrow(new RuntimeException("Not found"));
//
//        mockMvc.perform(get("/quiz-questions/999"))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    void getQuestionsByQuizId_success() throws Exception {
//        when(quizQuestionService.getQuestionsByQuizId(101L)).thenReturn(Arrays.asList(sampleOutDTO));
//
//        mockMvc.perform(get("/quiz-questions/quiz/101"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].quizId").value(101L));
//    }
//
//    @Test
//    void updateQuestion_success() throws Exception {
//        QuizQuestionUpdateInDTO updateDTO = QuizQuestionUpdateInDTO.builder()
//                .questionText("Updated question?")
//                .questionType("MCQ_SINGLE")
//                .points(new BigDecimal("10.0"))
//                .required(true)
//                .explanation("Updated explanation")
//                .position(1)
//                .build();
//
//        when(quizQuestionService.updateQuestion(eq(1L), any())).thenReturn(sampleOutDTO);
//
//        mockMvc.perform(put("/quiz-questions/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.questionId").value(1L));
//    }
//
//    @Test
//    void updateQuestion_validationError() throws Exception {
//        QuizQuestionUpdateInDTO invalidDTO = new QuizQuestionUpdateInDTO(); // Missing required fields
//
//        mockMvc.perform(put("/quiz-questions/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void deleteQuestion_success() throws Exception {
//        mockMvc.perform(delete("/quiz-questions/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void deleteQuestion_internalError() throws Exception {
//        doThrow(new RuntimeException("Delete failed")).when(quizQuestionService).deleteQuestion(999L);
//
//        mockMvc.perform(delete("/quiz-questions/999"))
//                .andExpect(status().isInternalServerError());
//    }
//}
//
//
package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.SecurityConfig;
import com.nt.course_service_lms.config.ServiceAuthenticationFilter;
import com.nt.course_service_lms.config.TestAuthenticationFilter;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.QuizQuestionController;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.service.QuizQuestionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizQuestionController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class QuizQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizQuestionService quizQuestionService;

    @MockitoBean
    private TestAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private QuizQuestionInDTO quizQuestionInDTO;
    private UpdateQuizQuestionInDTO updateQuizQuestionInDTO;
    private QuizQuestionOutDTO quizQuestionOutDTO;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter to DO NOTHING but continue the chain
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(serviceAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

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
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnCreatedQuestion_WhenValidInput() throws Exception {
        // Given
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class))).thenReturn(quizQuestionOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Question created successfully"))
                .andExpect(jsonPath("$.data.questionId").value(1L))
                .andExpect(jsonPath("$.data.questionText").value("What is the capital of France?"))
                .andExpect(jsonPath("$.data.questionType").value("MCQ_SINGLE"))
                .andExpect(jsonPath("$.data.points").value(10.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid question with empty question text
        QuizQuestionInDTO invalidQuestion = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("")  // Invalid: empty text
                .questionType("MCQ_SINGLE")
                .correctAnswer("b")
                .points(new BigDecimal("10.00"))
                .required(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuestion)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnNotFound_WhenQuizNotExists() throws Exception {
        // Given
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quiz not found with ID: 999"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnBadRequest_WhenInvalidQuestionData() throws Exception {
        // Given
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class)))
                .thenThrow(new ResourceNotValidException("Options are required for multiple choice questions"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createQuestion_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllQuestionsByQuizId_ShouldReturnQuestions_WhenQuestionsExist() throws Exception {
        // Given
        List<QuizQuestionOutDTO> questions = Arrays.asList(quizQuestionOutDTO);
        when(quizQuestionService.getQuestionsByQuizId(1L)).thenReturn(questions);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Retrieved 1 questions successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].questionId").value(1L))
                .andExpect(jsonPath("$.data[0].questionText").value("What is the capital of France?"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllQuestionsByQuizId_ShouldReturnQuestions_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizQuestionOutDTO> questions = Arrays.asList(quizQuestionOutDTO);
        when(quizQuestionService.getQuestionsByQuizId(1L)).thenReturn(questions);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Retrieved 1 questions successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllQuestionsByQuizId_ShouldReturnNotFound_WhenNoQuestionsExist() throws Exception {
        // Given
        when(quizQuestionService.getQuestionsByQuizId(999L))
                .thenThrow(new ResourceNotFoundException("No Questions Found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/quiz/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getAllQuestionsByQuizId_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/quiz/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuestionById_ShouldReturnQuestion_WhenQuestionExists() throws Exception {
        // Given
        when(quizQuestionService.getQuestionById(1L)).thenReturn(quizQuestionOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Question retrieved successfully"))
                .andExpect(jsonPath("$.data.questionId").value(1L))
                .andExpect(jsonPath("$.data.questionText").value("What is the capital of France?"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuestionById_ShouldReturnQuestion_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizQuestionService.getQuestionById(1L)).thenReturn(quizQuestionOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Question retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuestionById_ShouldReturnNotFound_WhenQuestionDoesNotExist() throws Exception {
        // Given
        when(quizQuestionService.getQuestionById(999L))
                .thenThrow(new ResourceNotFoundException("Question not found with ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-questions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturnUpdatedQuestion_WhenValidInput() throws Exception {
        // Given
        QuizQuestionOutDTO updatedQuestion = QuizQuestionOutDTO.builder()
                .questionId(1L)
                .quizId(1L)
                .questionText("What is the capital of Germany?")
                .questionType("MCQ_SINGLE")
                .options("[{\"id\":\"a\",\"text\":\"London\"},{\"id\":\"b\",\"text\":\"Paris\"},{\"id\":\"c\",\"text\":\"Berlin\"},{\"id\":\"d\",\"text\":\"Madrid\"}]")
                .correctAnswer("c")
                .points(new BigDecimal("15.00"))
                .explanation("Berlin is the capital city of Germany")
                .required(false)
                .position(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(quizQuestionService.updateQuestion(anyLong(), any(UpdateQuizQuestionInDTO.class)))
                .thenReturn(updatedQuestion);

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuizQuestionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Question updated successfully"))
                .andExpect(jsonPath("$.data.questionId").value(1L))
                .andExpect(jsonPath("$.data.questionText").value("What is the capital of Germany?"))
                .andExpect(jsonPath("$.data.points").value(15.00))
                .andExpect(jsonPath("$.data.required").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with too long question text
        UpdateQuizQuestionInDTO invalidUpdate = UpdateQuizQuestionInDTO.builder()
                .questionText(String.join("", java.util.Collections.nCopies(5001, "A")))   // Exceeds 5000 character limit
                .questionType("MCQ_SINGLE")
                .correctAnswer("b")
                .points(new BigDecimal("10.00"))
                .required(true)
                .position(1)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturnNotFound_WhenQuestionDoesNotExist() throws Exception {
        // Given
        when(quizQuestionService.updateQuestion(anyLong(), any(UpdateQuizQuestionInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Question not found with ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-questions/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuizQuestionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturnBadRequest_WhenInvalidPosition() throws Exception {
        // Given
        when(quizQuestionService.updateQuestion(anyLong(), any(UpdateQuizQuestionInDTO.class)))
                .thenThrow(new ResourceNotValidException("Position must be between 1 and 5 for quiz ID: 1"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuizQuestionInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateQuestion_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuizQuestionInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuestion_ShouldReturnSuccess_WhenQuestionExists() throws Exception {
        // Given
        doNothing().when(quizQuestionService).deleteQuestion(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-questions/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Question deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuestion_ShouldReturnNotFound_WhenQuestionDoesNotExist() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Question not found with ID: 999"))
                .when(quizQuestionService).deleteQuestion(999L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-questions/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteQuestion_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-questions/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuestion_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        // Given
        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizQuestionInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldHandleShortAnswerQuestion() throws Exception {
        // Given - Short answer question without options
        QuizQuestionInDTO shortAnswerQuestion = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("What is the chemical symbol for water?")
                .questionType("SHORT_ANSWER")
                .correctAnswer("H2O")
                .points(new BigDecimal("5.00"))
                .explanation("Water is H2O")
                .required(true)
                .build();

        QuizQuestionOutDTO shortAnswerResponse = QuizQuestionOutDTO.builder()
                .questionId(2L)
                .quizId(1L)
                .questionText("What is the chemical symbol for water?")
                .questionType("SHORT_ANSWER")
                .correctAnswer("H2O")
                .points(new BigDecimal("5.00"))
                .explanation("Water is H2O")
                .required(true)
                .position(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class))).thenReturn(shortAnswerResponse);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortAnswerQuestion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.questionType").value("SHORT_ANSWER"))
                .andExpect(jsonPath("$.data.options").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_ShouldHandleMultipleChoiceMultipleQuestion() throws Exception {
        // Given - Multiple choice multiple selection question
        QuizQuestionInDTO mcqMultipleQuestion = QuizQuestionInDTO.builder()
                .quizId(1L)
                .questionText("Which of the following are programming languages?")
                .questionType("MCQ_MULTIPLE")
                .options("[{\"id\":\"a\",\"text\":\"Java\"},{\"id\":\"b\",\"text\":\"Python\"},{\"id\":\"c\",\"text\":\"HTML\"},{\"id\":\"d\",\"text\":\"JavaScript\"}]")
                .correctAnswer("[\"a\",\"b\",\"d\"]")
                .points(new BigDecimal("20.00"))
                .explanation("Java, Python, and JavaScript are programming languages. HTML is a markup language.")
                .required(true)
                .build();

        QuizQuestionOutDTO mcqMultipleResponse = QuizQuestionOutDTO.builder()
                .questionId(3L)
                .quizId(1L)
                .questionText("Which of the following are programming languages?")
                .questionType("MCQ_MULTIPLE")
                .options("[{\"id\":\"a\",\"text\":\"Java\"},{\"id\":\"b\",\"text\":\"Python\"},{\"id\":\"c\",\"text\":\"HTML\"},{\"id\":\"d\",\"text\":\"JavaScript\"}]")
                .correctAnswer("[\"a\",\"b\",\"d\"]")
                .points(new BigDecimal("20.00"))
                .explanation("Java, Python, and JavaScript are programming languages. HTML is a markup language.")
                .required(true)
                .position(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(quizQuestionService.createQuestion(any(QuizQuestionInDTO.class))).thenReturn(mcqMultipleResponse);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mcqMultipleQuestion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.questionType").value("MCQ_MULTIPLE"))
                .andExpect(jsonPath("$.data.points").value(20.00));
    }
}