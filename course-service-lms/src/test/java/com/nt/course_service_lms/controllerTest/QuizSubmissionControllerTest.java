package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestAuthenticationFilter;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.QuizSubmissionController;
import com.nt.course_service_lms.dto.inDTO.QuizSubmissionInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizSubmissionResultOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.service.serviceImpl.QuizSubmissionService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizSubmissionController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class QuizSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizSubmissionService quizSubmissionService;

    @MockitoBean
    private TestAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private QuizSubmissionInDTO quizSubmissionInDTO;
    private QuizSubmissionResultOutDTO quizSubmissionResultOutDTO;
    private UserResponseInDTO userResponseInDTO;
    private UserResponseOutDTO userResponseOutDTO;
    private QuizAttemptOutDTO quizAttemptOutDTO;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter to DO NOTHING but continue the chain
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(serviceAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        // Initialize test data
        testDateTime = LocalDateTime.now();

        userResponseInDTO = UserResponseInDTO.builder()
                .userId(1L)
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("[\"a\"]")
                .answeredAt(testDateTime)
                .build();

        userResponseOutDTO = UserResponseOutDTO.builder()
                .responseId(1L)
                .userId(1L)
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("[\"a\"]")
                .isCorrect(true)
                .pointsEarned(BigDecimal.valueOf(10.0))
                .answeredAt(testDateTime)
                .build();

        quizAttemptOutDTO = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .userId(1L)
                .quizId(1L)
                .attempt(1L)
                .status("COMPLETED")
                .startedAt(testDateTime.minusMinutes(30))
                .finishedAt(testDateTime)
                .scoreDetails("{\"totalScore\":85.0}")
                .build();

        quizSubmissionInDTO = QuizSubmissionInDTO.builder()
                .userResponses(Arrays.asList(userResponseInDTO))
                .notes("Test submission")
                .timeSpent(1800L) // 30 minutes
                .build();

        quizSubmissionResultOutDTO = QuizSubmissionResultOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .userResponses(Arrays.asList(userResponseOutDTO))
                .totalScore(BigDecimal.valueOf(85.0))
                .maxPossibleScore(BigDecimal.valueOf(100.0))
                .correctAnswers(8L)
                .totalQuestions(10L)
                .percentageScore(BigDecimal.valueOf(85.0))
                .submissionType("MANUAL")
                .submittedAt(testDateTime)
                .build();
    }

    // SUBMIT QUIZ ON TIMEOUT TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuizOnTimeout_ShouldReturnResult_WhenValidInput() throws Exception {
        // Given
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenReturn(quizSubmissionResultOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz submitted automatically due to timeout"))
                .andExpect(jsonPath("$.data.quizAttempt.quizAttemptId").value(1L))
                .andExpect(jsonPath("$.data.totalScore").value(85.0))
                .andExpect(jsonPath("$.data.submissionType").value("MANUAL"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void submitQuizOnTimeout_ShouldReturnResult_WhenEmployeeRole() throws Exception {
        // Given
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenReturn(quizSubmissionResultOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz submitted automatically due to timeout"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuizOnTimeout_ShouldReturnResult_WhenEmptyResponses() throws Exception {
        // Given
        QuizSubmissionInDTO emptySubmission = QuizSubmissionInDTO.builder()
                .userResponses(Collections.emptyList())
                .notes("Timeout with no responses")
                .timeSpent(300L)
                .build();

        QuizSubmissionResultOutDTO emptyResult = QuizSubmissionResultOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .userResponses(Collections.emptyList())
                .totalScore(BigDecimal.ZERO)
                .maxPossibleScore(BigDecimal.valueOf(100.0))
                .correctAnswers(0L)
                .totalQuestions(10L)
                .percentageScore(BigDecimal.ZERO)
                .submissionType("AUTO_TIMEOUT")
                .submittedAt(testDateTime)
                .build();

        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenReturn(emptyResult);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptySubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.totalScore").value(0))
                .andExpect(jsonPath("$.data.correctAnswers").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuizOnTimeout_ShouldReturnNotFound_WhenQuizAttemptNotExists() throws Exception {
        // Given
        when(quizSubmissionService.submitQuizOnTimeout(eq(999L), anyList()))
                .thenThrow(new ResourceNotFoundException("Quiz attempt not found with ID: 999"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuizOnTimeout_ShouldReturnBadRequest_WhenQuizAttemptNotInProgress() throws Exception {
        // Given
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenThrow(new ResourceNotValidException("Quiz attempt is not in progress"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void submitQuizOnTimeout_ShouldReturnForbidden_WhenInsufficientRole() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isForbidden());
    }

    // SUBMIT QUIZ TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnResult_WhenValidInput() throws Exception {
        // Given
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL")))
                .thenReturn(quizSubmissionResultOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz submitted successfully"))
                .andExpect(jsonPath("$.data.quizAttempt.quizAttemptId").value(1L))
                .andExpect(jsonPath("$.data.totalScore").value(85.0))
                .andExpect(jsonPath("$.data.percentageScore").value(85.0));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void submitQuiz_ShouldReturnResult_WhenEmployeeRole() throws Exception {
        // Given
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL")))
                .thenReturn(quizSubmissionResultOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnResult_WhenCustomSubmissionType() throws Exception {
        // Given
        QuizSubmissionResultOutDTO customResult = QuizSubmissionResultOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .userResponses(Arrays.asList(userResponseOutDTO))
                .totalScore(BigDecimal.valueOf(75.0))
                .maxPossibleScore(BigDecimal.valueOf(100.0))
                .correctAnswers(7L)
                .totalQuestions(10L)
                .percentageScore(BigDecimal.valueOf(75.0))
                .submissionType("AUTO_SAVE")
                .submittedAt(testDateTime)
                .build();

        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("AUTO_SAVE")))
                .thenReturn(customResult);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .param("submissionType", "AUTO_SAVE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.submissionType").value("AUTO_SAVE"))
                .andExpect(jsonPath("$.data.totalScore").value(75.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnResult_WhenDefaultSubmissionType() throws Exception {
        // Given - No submissionType param, should default to "MANUAL"
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL")))
                .thenReturn(quizSubmissionResultOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.submissionType").value("MANUAL"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid input with null userResponses in nested UserResponseInDTO
        UserResponseInDTO invalidUserResponse = UserResponseInDTO.builder()
                .userId(null) // Invalid - null userId
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("[\"a\"]")
                .build();

        QuizSubmissionInDTO invalidSubmission = QuizSubmissionInDTO.builder()
                .userResponses(Arrays.asList(invalidUserResponse))
                .notes("Test submission")
                .timeSpent(1800L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSubmission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnBadRequest_WhenBlankUserAnswer() throws Exception {
        // Given - Invalid input with blank userAnswer
        UserResponseInDTO invalidUserResponse = UserResponseInDTO.builder()
                .userId(1L)
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("") // Invalid - blank answer
                .build();

        QuizSubmissionInDTO invalidSubmission = QuizSubmissionInDTO.builder()
                .userResponses(Arrays.asList(invalidUserResponse))
                .notes("Test submission")
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSubmission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnNotFound_WhenQuizAttemptNotExists() throws Exception {
        // Given
        when(quizSubmissionService.submitQuiz(eq(999L), anyList(), anyString()))
                .thenThrow(new ResourceNotFoundException("Quiz attempt not found with ID: 999"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnBadRequest_WhenQuizAttemptNotInProgress() throws Exception {
        // Given
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), anyString()))
                .thenThrow(new ResourceNotValidException("Quiz attempt is not in progress. Current status: COMPLETED"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void submitQuiz_ShouldReturnForbidden_WhenInsufficientRole() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnResult_WhenEmptyUserResponses() throws Exception {
        // Given - Empty user responses (partial submission)
        QuizSubmissionInDTO emptySubmission = QuizSubmissionInDTO.builder()
                .userResponses(Collections.emptyList())
                .notes("Partial submission")
                .timeSpent(600L)
                .build();

        QuizSubmissionResultOutDTO partialResult = QuizSubmissionResultOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .userResponses(Collections.emptyList())
                .totalScore(BigDecimal.ZERO)
                .maxPossibleScore(BigDecimal.valueOf(100.0))
                .correctAnswers(0L)
                .totalQuestions(10L)
                .percentageScore(BigDecimal.ZERO)
                .submissionType("MANUAL")
                .submittedAt(testDateTime)
                .build();

        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL")))
                .thenReturn(partialResult);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptySubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.totalScore").value(0))
                .andExpect(jsonPath("$.data.userResponses").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnResult_WhenNullUserResponses() throws Exception {
        // Given - Null user responses
        QuizSubmissionInDTO nullSubmission = QuizSubmissionInDTO.builder()
                .userResponses(null)
                .notes("Submission without responses")
                .timeSpent(120L)
                .build();

        when(quizSubmissionService.submitQuiz(eq(1L), any(), eq("MANUAL")))
                .thenReturn(quizSubmissionResultOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullSubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // UNAUTHORIZED ACCESS TESTS

    @Test
    void submitQuizOnTimeout_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submitQuiz_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isUnauthorized());
    }

    // ERROR HANDLING TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        // Given
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuizOnTimeout_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        // Given
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isInternalServerError());
    }

    // MALFORMED REQUEST TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuiz_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitQuizOnTimeout_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed: json, }"))
                .andExpect(status().isBadRequest());
    }
}