package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizSubmissionController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class QuizSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizSubmissionService quizSubmissionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private QuizSubmissionInDTO quizSubmissionInDTO;
    private QuizSubmissionResultOutDTO quizSubmissionResultOutDTO;
    private UserResponseInDTO userResponseInDTO;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        userResponseInDTO = UserResponseInDTO.builder()
                .userId(1L)
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("[\"a\"]")
                .answeredAt(testDateTime)
                .build();

        UserResponseOutDTO userResponseOutDTO = UserResponseOutDTO.builder()
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

        QuizAttemptOutDTO quizAttemptOutDTO = QuizAttemptOutDTO.builder()
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
                .userResponses(Collections.singletonList(userResponseInDTO))
                .notes("Test submission")
                .timeSpent(1800L)
                .build();

        quizSubmissionResultOutDTO = QuizSubmissionResultOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .userResponses(Collections.singletonList(userResponseOutDTO))
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
    void submitQuizOnTimeout_ShouldReturnResult_WhenValidInput() throws Exception {
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList())).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quizAttempt.quizAttemptId").value(1L))
                .andExpect(jsonPath("$.data.totalScore").value(85.0));
    }

    @Test
    void submitQuizOnTimeout_ShouldReturnResult_WhenEmployeeRole() throws Exception {
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList())).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void submitQuizOnTimeout_ShouldReturnResult_WhenEmptyResponses() throws Exception {
        QuizSubmissionInDTO emptySubmission = QuizSubmissionInDTO.builder().userResponses(Collections.emptyList()).build();
        quizSubmissionResultOutDTO.setTotalScore(BigDecimal.ZERO);
        quizSubmissionResultOutDTO.setCorrectAnswers(0L);

        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList())).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptySubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalScore").value(0));
    }

    @Test
    void submitQuizOnTimeout_ShouldReturnNotFound_WhenQuizAttemptNotExists() throws Exception {
        when(quizSubmissionService.submitQuizOnTimeout(eq(999L), anyList()))
                .thenThrow(new ResourceNotFoundException("Quiz attempt not found"));

        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/999")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitQuizOnTimeout_ShouldReturnBadRequest_WhenQuizAttemptNotInProgress() throws Exception {
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenThrow(new ResourceNotValidException("Quiz attempt is not in progress"));

        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isBadRequest());
    }


    // SUBMIT QUIZ TESTS
    @Test
    void submitQuiz_ShouldReturnResult_WhenValidInput() throws Exception {
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL"))).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalScore").value(85.0));
    }

    @Test
    void submitQuiz_ShouldReturnResult_WhenEmployeeRole() throws Exception {
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL"))).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void submitQuiz_ShouldReturnResult_WhenCustomSubmissionType() throws Exception {
        quizSubmissionResultOutDTO.setSubmissionType("AUTO_SAVE");
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("AUTO_SAVE"))).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .param("submissionType", "AUTO_SAVE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.submissionType").value("AUTO_SAVE"));
    }

    @Test
    void submitQuiz_ShouldReturnResult_WhenDefaultSubmissionType() throws Exception {
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL"))).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.submissionType").value("MANUAL"));
    }

    @Test
    void submitQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        UserResponseInDTO invalidUserResponse = UserResponseInDTO.builder().userId(null).build();
        QuizSubmissionInDTO invalidSubmission = QuizSubmissionInDTO.builder()
                .userResponses(Arrays.asList(invalidUserResponse)).build();

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSubmission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitQuiz_ShouldReturnBadRequest_WhenBlankUserAnswer() throws Exception {
        userResponseInDTO.setUserAnswer("");
        QuizSubmissionInDTO invalidSubmission = QuizSubmissionInDTO.builder()
                .userResponses(Arrays.asList(userResponseInDTO)).build();

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSubmission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitQuiz_ShouldReturnNotFound_WhenQuizAttemptNotExists() throws Exception {
        when(quizSubmissionService.submitQuiz(eq(999L), anyList(), anyString()))
                .thenThrow(new ResourceNotFoundException("Quiz attempt not found"));

        mockMvc.perform(post("/api/service-api/quiz-submissions/999")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitQuiz_ShouldReturnBadRequest_WhenQuizAttemptNotInProgress() throws Exception {
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), anyString()))
                .thenThrow(new ResourceNotValidException("Quiz attempt is not in progress"));

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void submitQuiz_ShouldReturnResult_WhenEmptyUserResponses() throws Exception {
        QuizSubmissionInDTO emptySubmission = QuizSubmissionInDTO.builder().userResponses(Collections.emptyList()).build();
        quizSubmissionResultOutDTO.setTotalScore(BigDecimal.ZERO);

        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), eq("MANUAL"))).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptySubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalScore").value(0));
    }

    @Test
    void submitQuiz_ShouldReturnResult_WhenNullUserResponses() throws Exception {
        QuizSubmissionInDTO nullSubmission = QuizSubmissionInDTO.builder().userResponses(null).build();
        when(quizSubmissionService.submitQuiz(eq(1L), any(), eq("MANUAL"))).thenReturn(quizSubmissionResultOutDTO);

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullSubmission)))
                .andExpect(status().isOk());
    }

    // UNAUTHORIZED ACCESS TESTS
    @Test
    void submitQuizOnTimeout_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isUnauthorized());
    }

    // ERROR HANDLING TESTS
    @Test
    void submitQuiz_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        when(quizSubmissionService.submitQuiz(eq(1L), anyList(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void submitQuizOnTimeout_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        when(quizSubmissionService.submitQuizOnTimeout(eq(1L), anyList()))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizSubmissionInDTO)))
                .andExpect(status().isInternalServerError());
    }

    // MALFORMED REQUEST TESTS
    @Test
    void submitQuiz_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        mockMvc.perform(post("/api/service-api/quiz-submissions/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitQuizOnTimeout_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        mockMvc.perform(post("/api/service-api/quiz-submissions/timeout/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed: json, }"))
                .andExpect(status().isBadRequest());
    }
}