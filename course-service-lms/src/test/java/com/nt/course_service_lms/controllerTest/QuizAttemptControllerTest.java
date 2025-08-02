package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.TestAuthenticationFilter;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.QuizAttemptController;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByCourseIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByUserIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserQuizAttemptDetailsOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.service.QuizAttemptService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
class QuizAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizAttemptService quizAttemptService;

    @MockitoBean
    private TestAuthenticationFilter testAuthenticationFilter;

    private QuizAttemptCreateInDTO quizAttemptCreateInDTO;
    private QuizAttemptUpdateInDTO quizAttemptUpdateInDTO;
    private QuizAttemptOutDTO quizAttemptOutDTO;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(testAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

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
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnCreatedAttempt_WhenValidInput() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class))).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L))
                .andExpect(jsonPath("$.quizId").value(1L))
                .andExpect(jsonPath("$.userId").value(100L))
                .andExpect(jsonPath("$.attempt").value(1L))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createQuizAttempt_ShouldReturnCreatedAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class))).thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid input with null quiz ID
        QuizAttemptCreateInDTO invalidInput = QuizAttemptCreateInDTO.builder()
                .quizId(null)
                .userId(100L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnBadRequest_WhenQuizNotFound() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quiz Not Found"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnBadRequest_WhenMaxAttemptsExceeded() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class)))
                .thenThrow(new ResourceNotValidException("User has exceeded maximum allowed attempts for this quiz"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void createQuizAttempt_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isForbidden());
    }

    // UPDATE QUIZ ATTEMPT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuizAttempt_ShouldReturnUpdatedAttempt_WhenValidInput() throws Exception {
        // Given
        QuizAttemptOutDTO updatedAttempt = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .attempt(1L)
                .quizId(1L)
                .userId(100L)
                .status("COMPLETED")
                .finishedAt(LocalDateTime.now())
                .scoreDetails("{\"totalScore\": 85, \"maxScore\": 100}")
                .build();

        when(quizAttemptService.updateQuizAttempt(anyLong(), any(QuizAttemptUpdateInDTO.class)))
                .thenReturn(updatedAttempt);

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptUpdateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuizAttempt_ShouldReturnNotFound_WhenAttemptNotExists() throws Exception {
        // Given
        when(quizAttemptService.updateQuizAttempt(anyLong(), any(QuizAttemptUpdateInDTO.class)))
                .thenThrow(new ResourceNotFoundException("QuizAttempt not found with ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptUpdateInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuizAttempt_ShouldReturnBadRequest_WhenInvalidStatusTransition() throws Exception {
        // Given
        when(quizAttemptService.updateQuizAttempt(anyLong(), any(QuizAttemptUpdateInDTO.class)))
                .thenThrow(new ResourceNotValidException("Invalid status transition from COMPLETED to IN_PROGRESS"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptUpdateInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateQuizAttempt_ShouldReturnUpdatedAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.updateQuizAttempt(anyLong(), any(QuizAttemptUpdateInDTO.class)))
                .thenReturn(quizAttemptOutDTO);

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptUpdateInDTO)))
                .andExpect(status().isOk());
    }

    // GET QUIZ ATTEMPT BY ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptById_ShouldReturnAttempt_WhenAttemptExists() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptById(1L)).thenReturn(Optional.of(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L))
                .andExpect(jsonPath("$.quizId").value(1L))
                .andExpect(jsonPath("$.userId").value(100L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptById_ShouldReturnNotFound_WhenAttemptNotExists() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptById_ShouldReturnAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptById(1L)).thenReturn(Optional.of(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/1"))
                .andExpect(status().isOk());
    }

    // GET ALL QUIZ ATTEMPTS TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllQuizAttempts_ShouldReturnPagedAttempts_WhenAttemptsExist() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        Page<QuizAttemptOutDTO> page = new PageImpl<>(attempts, PageRequest.of(0, 20), 1);
        when(quizAttemptService.getAllQuizAttempts(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].quizAttemptId").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllQuizAttempts_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt"))
                .andExpect(status().isForbidden());
    }

    // GET QUIZ ATTEMPTS BY USER ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptsByUserId_ShouldReturnAttempts_WhenAttemptsExist() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        when(quizAttemptService.getQuizAttemptsByUserId(100L)).thenReturn(attempts);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].quizAttemptId").value(1L))
                .andExpect(jsonPath("$[0].userId").value(100L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptsByUserId_ShouldReturnAttempts_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        when(quizAttemptService.getQuizAttemptsByUserId(100L)).thenReturn(attempts);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100"))
                .andExpect(status().isOk());
    }

    // GET QUIZ ATTEMPTS BY QUIZ ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptsByQuizId_ShouldReturnAttempts_WhenAttemptsExist() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        when(quizAttemptService.getQuizAttemptsByQuizId(1L)).thenReturn(attempts);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].quizAttemptId").value(1L))
                .andExpect(jsonPath("$[0].quizId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptsByQuizId_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz/1"))
                .andExpect(status().isForbidden());
    }

    // GET QUIZ ATTEMPTS BY USER AND QUIZ TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptsByUserAndQuiz_ShouldReturnAttempts_WhenAttemptsExist() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        when(quizAttemptService.getQuizAttemptsByUserAndQuiz(100L, 1L)).thenReturn(attempts);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(100L))
                .andExpect(jsonPath("$[0].quizId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptsByUserAndQuiz_ShouldReturnAttempts_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        when(quizAttemptService.getQuizAttemptsByUserAndQuiz(100L, 1L)).thenReturn(attempts);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1"))
                .andExpect(status().isOk());
    }

    // GET QUIZ ATTEMPTS BY STATUS TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptsByStatus_ShouldReturnAttempts_WhenAttemptsExist() throws Exception {
        // Given
        List<QuizAttemptOutDTO> attempts = Arrays.asList(quizAttemptOutDTO);
        when(quizAttemptService.getQuizAttemptsByStatus("COMPLETED")).thenReturn(attempts);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptsByStatus_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/status/COMPLETED"))
                .andExpect(status().isForbidden());
    }

    // GET LATEST ATTEMPT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getLatestAttemptByUserAndQuiz_ShouldReturnAttempt_WhenAttemptExists() throws Exception {
        // Given
        when(quizAttemptService.getLatestAttemptByUserAndQuiz(100L, 1L)).thenReturn(Optional.of(quizAttemptOutDTO));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizAttemptId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLatestAttemptByUserAndQuiz_ShouldReturnNotFound_WhenNoAttemptExists() throws Exception {
        // Given
        when(quizAttemptService.getLatestAttemptByUserAndQuiz(100L, 1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1/latest"))
                .andExpect(status().isNotFound());
    }

    // DELETE QUIZ ATTEMPT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuizAttempt_ShouldReturnNoContent_WhenAttemptExists() throws Exception {
        // Given
        doNothing().when(quizAttemptService).deleteQuizAttempt(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-attempt/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuizAttempt_ShouldReturnNotFound_WhenAttemptNotExists() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("QuizAttempt not found with ID: 999"))
                .when(quizAttemptService).deleteQuizAttempt(999L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-attempt/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteQuizAttempt_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/quiz-attempt/1"))
                .andExpect(status().isForbidden());
    }

    // COMPLETE ATTEMPT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void completeAttempt_ShouldReturnCompletedAttempt_WhenValidRequest() throws Exception {
        // Given
        QuizAttemptOutDTO completedAttempt = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .status("COMPLETED")
                .finishedAt(LocalDateTime.now())
                .build();

        when(quizAttemptService.completeAttempt(1L, "{\"score\": 85}")).thenReturn(completedAttempt);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\": 85}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void completeAttempt_ShouldReturnBadRequest_WhenAttemptCannotBeCompleted() throws Exception {
        // Given
        when(quizAttemptService.completeAttempt(anyLong(), anyString()))
                .thenThrow(new ResourceNotValidException("Cannot complete quiz attempt with status: COMPLETED"));

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\": 85}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void completeAttempt_ShouldReturnCompletedAttempt_WhenEmployeeAccess() throws Exception {
        // Given
        QuizAttemptOutDTO completedAttempt = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .status("COMPLETED")
                .build();

        when(quizAttemptService.completeAttempt(1L, null)).thenReturn(completedAttempt);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/complete"))
                .andExpect(status().isOk());
    }

    // ABANDON ATTEMPT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void abandonAttempt_ShouldReturnAbandonedAttempt_WhenValidRequest() throws Exception {
        // Given
        QuizAttemptOutDTO abandonedAttempt = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .status("ABANDONED")
                .finishedAt(LocalDateTime.now())
                .build();

        when(quizAttemptService.abandonAttempt(1L)).thenReturn(abandonedAttempt);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/abandon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABANDONED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void abandonAttempt_ShouldReturnBadRequest_WhenAttemptCannotBeAbandoned() throws Exception {
        // Given
        when(quizAttemptService.abandonAttempt(1L))
                .thenThrow(new ResourceNotValidException("Cannot abandon quiz attempt with status: COMPLETED"));

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/abandon"))
                .andExpect(status().isBadRequest());
    }

    // TIMEOUT ATTEMPT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void timeOutAttempt_ShouldReturnTimedOutAttempt_WhenValidRequest() throws Exception {
        // Given
        QuizAttemptOutDTO timedOutAttempt = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .status("TIMED_OUT")
                .finishedAt(LocalDateTime.now())
                .build();

        when(quizAttemptService.timeOutAttempt(1L)).thenReturn(timedOutAttempt);

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/timeout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TIMED_OUT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void timeOutAttempt_ShouldReturnBadRequest_WhenAttemptCannotBeTimedOut() throws Exception {
        // Given
        when(quizAttemptService.timeOutAttempt(1L))
                .thenThrow(new ResourceNotValidException("Cannot time out quiz attempt with status: COMPLETED"));

        // When & Then
        mockMvc.perform(patch("/api/service-api/quiz-attempt/1/timeout"))
                .andExpect(status().isBadRequest());
    }

    // CHECK QUIZ ATTEMPT EXISTS TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void checkQuizAttemptExists_ShouldReturnNotFound_WhenAttemptNotExists() throws Exception {
        // Given
        when(quizAttemptService.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(head("/api/service-api/quiz-attempt/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void checkQuizAttemptExists_ShouldReturnOk_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(head("/api/service-api/quiz-attempt/1"))
                .andExpect(status().isOk());
    }

    // COUNT ATTEMPTS BY USER AND QUIZ TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void countAttemptsByUserAndQuiz_ShouldReturnCount_WhenAttemptsExist() throws Exception {
        // Given
        when(quizAttemptService.countAttemptsByUserAndQuiz(100L, 1L)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void countAttemptsByUserAndQuiz_ShouldReturnCount_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.countAttemptsByUserAndQuiz(100L, 1L)).thenReturn(2L);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void countAttemptsByUserAndQuiz_ShouldReturnZero_WhenNoAttemptsExist() throws Exception {
        // Given
        when(quizAttemptService.countAttemptsByUserAndQuiz(100L, 1L)).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }


    // GET USER ATTEMPT DETAILS TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserAttemptDetails_ShouldReturnUserAttemptDetails_WhenDetailsExist() throws Exception {
        // Given
        UserQuizAttemptDetailsOutDTO userAttemptDetails = UserQuizAttemptDetailsOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .totalScore(new java.math.BigDecimal("85"))
                .maxPossibleScore(new java.math.BigDecimal("100"))
                .correctAnswers(8L)
                .totalQuestions(10L)
                .percentageScore(new java.math.BigDecimal("85.00"))
                .submissionType("MANUAL_SUBMIT")
                .build();

        when(quizAttemptService.getUserAttemptDetails(100L, 1L))
                .thenReturn(Arrays.asList(userAttemptDetails));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched user attempt details"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].totalScore").value(85))
                .andExpect(jsonPath("$.data[0].correctAnswers").value(8));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getUserAttemptDetails_ShouldReturnUserAttemptDetails_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.getUserAttemptDetails(100L, 1L))
                .thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getUserAttemptDetails_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/course/1"))
                .andExpect(status().isForbidden());
    }

    // GET QUIZ ATTEMPT DETAILS BY USER ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptDetailsByUserId_ShouldReturnAttemptDetails_WhenDetailsExist() throws Exception {
        // Given
        QuizAttemptDetailsByUserIDOutDTO attemptDetails = QuizAttemptDetailsByUserIDOutDTO.builder()
                .userQuizAttemptDetailsOutDTOS(Arrays.asList())
                .build();

        when(quizAttemptService.getQuizAttemptDetailsByUserID(100L))
                .thenReturn(Arrays.asList(attemptDetails));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz-attempt-details/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User Attempt Details Fetched Successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptDetailsByUserId_ShouldReturnAttemptDetails_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptDetailsByUserID(100L))
                .thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz-attempt-details/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // GET QUIZ ATTEMPT DETAILS BY COURSE ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptDetailsByCourseId_ShouldReturnAttemptDetails_WhenDetailsExist() throws Exception {
        // Given
        QuizAttemptDetailsByCourseIDOutDTO courseAttemptDetails = QuizAttemptDetailsByCourseIDOutDTO.builder()
                .userId(100L)
                .userName("testuser")
                .firstName("Test")
                .lastName("User")
                .userQuizAttemptDetailsOutDTOS(Arrays.asList())
                .build();

        when(quizAttemptService.getQuizAttemptDetailsByCourseID(1L))
                .thenReturn(Arrays.asList(courseAttemptDetails));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz-attempt-details/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User Attempt Details Fetched Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(100))
                .andExpect(jsonPath("$.data[0].userName").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizAttemptDetailsByCourseId_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz-attempt-details/course/1"))
                .andExpect(status().isForbidden());
    }

    // ERROR HANDLING TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuizAttempt_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnBadRequest_WhenNegativeIds() throws Exception {
        // Given - Invalid input with negative IDs
        QuizAttemptCreateInDTO invalidInput = QuizAttemptCreateInDTO.builder()
                .quizId(-1L)
                .userId(-100L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuizAttempt_ShouldReturnBadRequest_WhenInvalidStatus() throws Exception {
        // Given - Invalid status
        QuizAttemptUpdateInDTO invalidUpdate = QuizAttemptUpdateInDTO.builder()
                .status("INVALID_STATUS")
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/quiz-attempt/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuizAttempt_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        // Given
        when(quizAttemptService.createQuizAttempt(any(QuizAttemptCreateInDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quiz-attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizAttemptCreateInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserAttemptDetails_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(quizAttemptService.getUserAttemptDetails(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Failed to fetch user attempt details"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100/quiz/course/1"))
                .andExpect(status().isInternalServerError());
    }

    // EDGE CASE TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptsByUserId_ShouldReturnEmptyList_WhenNoAttemptsExist() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptsByUserId(100L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizAttemptsByQuizId_ShouldReturnEmptyList_WhenNoAttemptsExist() throws Exception {
        // Given
        when(quizAttemptService.getQuizAttemptsByQuizId(1L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllQuizAttempts_ShouldReturnEmptyPage_WhenNoAttemptsExist() throws Exception {
        // Given
        Page<QuizAttemptOutDTO> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 20), 0);
        when(quizAttemptService.getAllQuizAttempts(any())).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/service-api/quiz-attempt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}