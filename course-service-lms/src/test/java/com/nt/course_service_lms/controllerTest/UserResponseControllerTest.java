package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestAuthenticationFilter;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.UserResponseController;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.UserResponseService;
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
import org.springframework.data.domain.Pageable;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserResponseController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class UserResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserResponseService userResponseService;

    @MockitoBean
    private TestAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private UserResponseInDTO userResponseInDTO;
    private UserResponseUpdateInDTO userResponseUpdateInDTO;
    private UserResponseOutDTO userResponseOutDTO;
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

        userResponseUpdateInDTO = UserResponseUpdateInDTO.builder()
                .userAnswer("[\"b\"]")
                .isCorrect(false)
                .pointsEarned(BigDecimal.ZERO)
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
    }

    // CREATE USER RESPONSE TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserResponse_ShouldReturnCreatedResponses_WhenValidInput() throws Exception {
        // Given
        List<UserResponseInDTO> inputList = Arrays.asList(userResponseInDTO);
        List<UserResponseOutDTO> outputList = Arrays.asList(userResponseOutDTO);
        when(userResponseService.createUserResponse(any(List.class))).thenReturn(outputList);

        // When & Then
        mockMvc.perform(post("/api/service-api/v1/user-responses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Successfully created 1 user responses"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].responseId").value(1L))
                .andExpect(jsonPath("$.data[0].userId").value(1L))
                .andExpect(jsonPath("$.data[0].isCorrect").value(true));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createUserResponse_ShouldReturnCreatedResponses_WhenEmployeeRole() throws Exception {
        // Given
        List<UserResponseInDTO> inputList = Arrays.asList(userResponseInDTO);
        List<UserResponseOutDTO> outputList = Arrays.asList(userResponseOutDTO);
        when(userResponseService.createUserResponse(any(List.class))).thenReturn(outputList);

        // When & Then
        mockMvc.perform(post("/api/service-api/v1/user-responses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserResponse_ShouldReturnBadRequest_WhenEmptyList() throws Exception {
        // Given
        List<UserResponseInDTO> emptyList = Collections.emptyList();

        // When & Then
        mockMvc.perform(post("/api/service-api/v1/user-responses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("User response list cannot be empty"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserResponse_ShouldReturnConflict_WhenResponseAlreadyExists() throws Exception {
        // Given
        List<UserResponseInDTO> inputList = Arrays.asList(userResponseInDTO);
        when(userResponseService.createUserResponse(any(List.class)))
                .thenThrow(new ResourceAlreadyExistsException("User response already exists"));

        // When & Then
        mockMvc.perform(post("/api/service-api/v1/user-responses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputList)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void createUserResponse_ShouldReturnForbidden_WhenInsufficientRole() throws Exception {
        // Given
        List<UserResponseInDTO> inputList = Arrays.asList(userResponseInDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/v1/user-responses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputList)))
                .andExpect(status().isForbidden());
    }

    // GET USER RESPONSE BY ID TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserResponseById_ShouldReturnResponse_WhenResponseExists() throws Exception {
        // Given
        when(userResponseService.getUserResponseById(1L)).thenReturn(userResponseOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User response retrieved successfully"))
                .andExpect(jsonPath("$.data.responseId").value(1L))
                .andExpect(jsonPath("$.data.userId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getUserResponseById_ShouldReturnResponse_WhenEmployeeRole() throws Exception {
        // Given
        when(userResponseService.getUserResponseById(1L)).thenReturn(userResponseOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserResponseById_ShouldReturnNotFound_WhenResponseDoesNotExist() throws Exception {
        // Given
        when(userResponseService.getUserResponseById(999L))
                .thenThrow(new ResourceNotFoundException("User response not found with ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/999"))
                .andExpect(status().isNotFound());
    }

    // UPDATE USER RESPONSE TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserResponse_ShouldReturnUpdatedResponse_WhenValidInput() throws Exception {
        // Given
        UserResponseOutDTO updatedResponse = UserResponseOutDTO.builder()
                .responseId(1L)
                .userId(1L)
                .quizId(1L)
                .questionId(1L)
                .attempt(1L)
                .userAnswer("[\"b\"]")
                .isCorrect(false)
                .pointsEarned(BigDecimal.ZERO)
                .answeredAt(testDateTime)
                .build();

        when(userResponseService.updateUserResponse(eq(1L), any(UserResponseUpdateInDTO.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/service-api/v1/user-responses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userResponseUpdateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User response updated successfully"))
                .andExpect(jsonPath("$.data.userAnswer").value("[\"b\"]"))
                .andExpect(jsonPath("$.data.isCorrect").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserResponse_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid input with blank userAnswer
        UserResponseUpdateInDTO invalidUpdate = UserResponseUpdateInDTO.builder()
                .userAnswer("")
                .isCorrect(true)
                .pointsEarned(BigDecimal.valueOf(10.0))
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/v1/user-responses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateUserResponse_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/v1/user-responses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userResponseUpdateInDTO)))
                .andExpect(status().isForbidden());
    }

    // DELETE USER RESPONSE TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUserResponse_ShouldReturnSuccess_WhenResponseExists() throws Exception {
        // Given
        doNothing().when(userResponseService).deleteUserResponse(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/v1/user-responses/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User response deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteUserResponse_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/v1/user-responses/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // GET ALL USER RESPONSES TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUserResponses_ShouldReturnPagedResponses_WhenResponsesExist() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseOutDTO> responsePage = new PageImpl<>(Arrays.asList(userResponseOutDTO), pageable, 1);
        when(userResponseService.getAllUserResponses(any(Pageable.class))).thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User responses retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllUserResponses_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses"))
                .andExpect(status().isForbidden());
    }

    // GET USER RESPONSES BY USER ID TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserResponsesByUserId_ShouldReturnResponses_WhenResponsesExist() throws Exception {
        // Given
        List<UserResponseOutDTO> responses = Arrays.asList(userResponseOutDTO);
        when(userResponseService.getUserResponsesByUserId(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User responses retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getUserResponsesByUserId_ShouldReturnResponses_WhenEmployeeRole() throws Exception {
        // Given
        List<UserResponseOutDTO> responses = Arrays.asList(userResponseOutDTO);
        when(userResponseService.getUserResponsesByUserId(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // GET USER RESPONSES BY USER ID PAGINATED TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserResponsesByUserIdPaginated_ShouldReturnPagedResponses() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseOutDTO> responsePage = new PageImpl<>(Arrays.asList(userResponseOutDTO), pageable, 1);
        when(userResponseService.getUserResponsesByUserId(eq(1L), any(Pageable.class))).thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/paginated")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    // GET USER RESPONSES BY QUIZ ID TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserResponsesByQuizId_ShouldReturnResponses_WhenResponsesExist() throws Exception {
        // Given
        List<UserResponseOutDTO> responses = Arrays.asList(userResponseOutDTO);
        when(userResponseService.getUserResponsesByQuizId(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(1L));
    }

    // GET USER RESPONSES BY QUIZ ID PAGINATED TESTS

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getUserResponsesByQuizIdPaginated_ShouldReturnPagedResponses() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseOutDTO> responsePage = new PageImpl<>(Arrays.asList(userResponseOutDTO), pageable, 1);
        when(userResponseService.getUserResponsesByQuizId(eq(1L), any(Pageable.class))).thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/quiz/1/paginated")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // GET USER RESPONSES BY USER ID AND QUIZ ID TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserResponsesByUserIdAndQuizId_ShouldReturnResponses() throws Exception {
        // Given
        List<UserResponseOutDTO> responses = Arrays.asList(userResponseOutDTO);
        when(userResponseService.getUserResponsesByUserIdAndQuizId(1L, 1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray());
    }

    // GET USER RESPONSES BY USER ID, QUIZ ID AND ATTEMPT TESTS

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getUserResponsesByUserIdQuizIdAndAttempt_ShouldReturnResponses() throws Exception {
        // Given
        List<UserResponseOutDTO> responses = Arrays.asList(userResponseOutDTO);
        when(userResponseService.getUserResponsesByUserIdAndQuizIdAndAttempt(1L, 1L, 1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/attempt/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray());
    }

    // GET TOTAL SCORE TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTotalScore_ShouldReturnScore_WhenScoreExists() throws Exception {
        // Given
        BigDecimal totalScore = BigDecimal.valueOf(85.5);
        when(userResponseService.getTotalScore(1L, 1L, 1L)).thenReturn(totalScore);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/attempt/1/total-score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Total score retrieved successfully"))
                .andExpect(jsonPath("$.data").value(85.5));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getTotalScore_ShouldReturnScore_WhenEmployeeRole() throws Exception {
        // Given
        BigDecimal totalScore = BigDecimal.ZERO;
        when(userResponseService.getTotalScore(1L, 1L, 1L)).thenReturn(totalScore);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/attempt/1/total-score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
    }

    // COUNT CORRECT ANSWERS TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void countCorrectAnswers_ShouldReturnCount_WhenAnswersExist() throws Exception {
        // Given
        when(userResponseService.countCorrectAnswers(1L, 1L, 1L)).thenReturn(8L);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/attempt/1/correct-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Correct answers count retrieved successfully"))
                .andExpect(jsonPath("$.data").value(8));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void countCorrectAnswers_ShouldReturnZero_WhenNoCorrectAnswers() throws Exception {
        // Given
        when(userResponseService.countCorrectAnswers(1L, 1L, 1L)).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/attempt/1/correct-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
    }

    // GET MAX ATTEMPT NUMBER TESTS

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMaxAttemptNumber_ShouldReturnMaxAttempt_WhenAttemptsExist() throws Exception {
        // Given
        when(userResponseService.getMaxAttemptNumber(1L, 1L)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/max-attempt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Maximum attempt number retrieved successfully"))
                .andExpect(jsonPath("$.data").value(3));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getMaxAttemptNumber_ShouldReturnZero_WhenNoAttemptsExist() throws Exception {
        // Given
        when(userResponseService.getMaxAttemptNumber(1L, 1L)).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/user/1/quiz/1/max-attempt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
    }

    // UNAUTHORIZED ACCESS TESTS

    @Test
    void createUserResponse_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // Given
        List<UserResponseInDTO> inputList = Arrays.asList(userResponseInDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/v1/user-responses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputList)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserResponseById_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/v1/user-responses/1"))
                .andExpect(status().isUnauthorized());
    }
}