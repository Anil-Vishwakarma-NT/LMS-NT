package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.QuizAttemptController;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.QuizAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizAttemptController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class QuizAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizAttemptService quizAttemptService;

    private QuizAttemptCreateInDTO quizAttemptCreateInDTO;
    private QuizAttemptUpdateInDTO quizAttemptUpdateInDTO;
    private QuizAttemptOutDTO quizAttemptOutDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        quizAttemptCreateInDTO = QuizAttemptCreateInDTO.builder()
                .quizId(1L)
                .userId(100L)
                .build();

        quizAttemptUpdateInDTO = QuizAttemptUpdateInDTO.builder()
                .status("COMPLETED")
                .finishedAt(LocalDateTime.now())
                .scoreDetails("{\"totalScore\": 85, \"maxScore\": 100}")
                .build();

        quizAttemptOutDTO = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .attempt(1L)
                .quizId(1L)
                .userId(100L)
                .startedAt(LocalDateTime.now().minusHours(1))
                .finishedAt(LocalDateTime.now())
                .scoreDetails("{\"totalScore\": 85, \"maxScore\": 100}")
                .status("COMPLETED")
                .attemptsLeft(2L)
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // CREATE QUIZ ATTEMPT TESTS
    @Test
    void createQuizAttempt_ShouldReturnCreatedAttempt_WhenAdminAccess() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class))).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L));
    }

    @Test
    void createQuizAttempt_ShouldReturnCreatedAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class))).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L));
    }

    // UPDATE QUIZ ATTEMPT TESTS
    @Test
    void updateQuizAttempt_ShouldReturnUpdatedAttempt_WhenAdminAccess() throws Exception {
        // Given
        when(quizAttemptService.updateQuizAttempt(anyLong(), any(QuizAttemptUpdateInDTO.class))).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptUpdateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateQuizAttempt_ShouldReturnUpdatedAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.updateQuizAttempt(anyLong(), any(QuizAttemptUpdateInDTO.class))).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptUpdateInDTO)))
                .andExpect(status().isOk());
    }

    // GET QUIZ ATTEMPT BY ID TESTS
    @Test
    void getQuizAttemptById_ShouldReturnAttempt_WhenAttemptExists() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptById(1L)).thenReturn(Optional.of(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L));
    }

    // GET ALL QUIZ ATTEMPTS TESTS
    @Test
    void getAllQuizAttempts_ShouldReturnPagedAttempts_WhenAdminAccess() throws Exception {
        // Given
        Page<QuizAttemptOutDTO> page = new PageImpl<>(Collections.singletonList(quizAttemptOutDTO));
        when(quizAttemptService.getAllQuizAttempts(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quizAttemptId").value(1L));
    }

    @Test
    void getAllQuizAttempts_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // GET QUIZ ATTEMPTS BY USER ID TESTS
    @Test
    void getQuizAttemptsByUserId_ShouldReturnAttempts_WhenAdminAccess() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptsByUserId(100L)).thenReturn(Collections.singletonList(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(100L));
    }

    // GET QUIZ ATTEMPTS BY QUIZ ID TESTS
    @Test
    void getQuizAttemptsByQuizId_ShouldReturnAttempts_WhenAdminAccess() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptsByQuizId(1L)).thenReturn(Collections.singletonList(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quizId").value(1L));
    }

    @Test
    void getQuizAttemptsByQuizId_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // GET QUIZ ATTEMPTS BY USER AND QUIZ TESTS
    @Test
    void getQuizAttemptsByUserAndQuiz_ShouldReturnAttempts_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptsByUserAndQuiz(100L, 1L)).thenReturn(Collections.singletonList(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    // GET QUIZ ATTEMPTS BY STATUS TESTS
    @Test
    void getQuizAttemptsByStatus_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/status/COMPLETED")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // DELETE QUIZ ATTEMPT TESTS
    @Test
    void deleteQuizAttempt_ShouldReturnNoContent_WhenAdminAccess() throws Exception {
        // Given
        doNothing().when(quizAttemptService).deleteQuizAttempt(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-attempt/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteQuizAttempt_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-attempt/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // COMPLETE ATTEMPT TESTS
    @Test
    void completeAttempt_ShouldReturnCompletedAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.completeAttempt(1L, null)).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/complete")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    // ABANDON ATTEMPT TESTS
    @Test
    void abandonAttempt_ShouldReturnAbandonedAttempt_WhenValidRequest() throws Exception {
        // Given
        quizAttemptOutDTO.setStatus("ABANDONED");
        when(quizAttemptService.abandonAttempt(1L)).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/abandon")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABANDONED"));
    }

    // TIMEOUT ATTEMPT TESTS
    @Test
    void timeOutAttempt_ShouldReturnTimedOutAttempt_WhenValidRequest() throws Exception {
        // Given
        quizAttemptOutDTO.setStatus("TIMED_OUT");
        when(quizAttemptService.timeOutAttempt(1L)).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/timeout")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TIMED_OUT"));
    }

    // CHECK QUIZ ATTEMPT EXISTS TESTS
    @Test
    void checkQuizAttemptExists_ShouldReturnOk_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(head("/api/service-api/quiz-attempt/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    // COUNT ATTEMPTS BY USER AND QUIZ TESTS
    @Test
    void countAttemptsByUserAndQuiz_ShouldReturnCount_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.countAttemptsByUserAndQuiz(100L, 1L)).thenReturn(2L);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1/count")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    // GET QUIZ ATTEMPT DETAILS BY COURSE ID TESTS
    @Test
    void getQuizAttemptDetailsByCourseId_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz-attempt-details/course/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // ERROR HANDLING TESTS
    @Test
    void createQuizAttempt_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given
        QuizAttemptCreateInDTO invalidInput = QuizAttemptCreateInDTO.builder().userId(100L).quizId(null).build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuizAttempt_ShouldReturnNotFound_WhenQuizNotFound() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quiz Not Found"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createQuizAttempt_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isInternalServerError());
    }
}