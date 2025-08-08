package com.nt.course_service_lms.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.QuizController;
import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class QuizControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private QuizCreateInDTO quizCreateInDTO;
    private QuizUpdateInDTO quizUpdateInDTO;
    private QuizOutDTO quizOutDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        quizCreateInDTO = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("Java Fundamentals Quiz")
                .description("A comprehensive quiz covering Java basics")
                .timeLimit(60)
                .attemptsAllowed(3)
                .passingScore(BigDecimal.valueOf(70.00))
                .randomizeQuestions(true)
                .showResults(false)
                .isActive(true)
                .createdBy(1)
                .build();

        quizUpdateInDTO = QuizUpdateInDTO.builder()
                .title("Updated Java Fundamentals Quiz")
                .description("Updated description for Java basics quiz")
                .timeLimit(90)
                .attemptsAllowed(2)
                .passingScore(BigDecimal.valueOf(75.00))
                .randomizeQuestions(false)
                .showResults(true)
                .isActive(true)
                .build();

        quizOutDTO = QuizOutDTO.builder()
                .quizId(1L)
                .parentType("course")
                .parentId(1L)
                .title("Java Fundamentals Quiz")
                .description("A comprehensive quiz covering Java basics")
                .timeLimit(60)
                .attemptsAllowed(3)
                .passingScore(BigDecimal.valueOf(70.00))
                .randomizeQuestions(true)
                .showResults(false)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createQuiz_ShouldReturnCreatedQuiz_WhenValidInput() throws Exception {
        // Given
        when(quizService.createQuiz(any(QuizCreateInDTO.class))).thenReturn(quizOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz created successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Java Fundamentals Quiz"))
                .andExpect(jsonPath("$.data.parentType").value("course"))
                .andExpect(jsonPath("$.data.parentId").value(1L))
                .andExpect(jsonPath("$.data.timeLimit").value(60))
                .andExpect(jsonPath("$.data.attemptsAllowed").value(3))
                .andExpect(jsonPath("$.data.passingScore").value(70.00))
                .andExpect(jsonPath("$.data.randomizeQuestions").value(true))
                .andExpect(jsonPath("$.data.showResults").value(false))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid quiz with blank title
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("")
                .description("Description")
                .attemptsAllowed(3)
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenInvalidParentType() throws Exception {
        // Given - Invalid parent type
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("invalid-type")
                .parentId(1L)
                .title("Valid Title")
                .description("Description")
                .attemptsAllowed(3)
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenNegativeAttemptsAllowed() throws Exception {
        // Given - Invalid attempts allowed
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("Valid Title")
                .description("Description")
                .attemptsAllowed(0)
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnConflict_WhenQuizAlreadyExists() throws Exception {
        // Given
        when(quizService.createQuiz(any(QuizCreateInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Quiz with this title already exists"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void createQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllQuizzes_ShouldReturnListOfQuizzes_WhenQuizzesExist() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("All quizzes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Java Fundamentals Quiz"))
                .andExpect(jsonPath("$.data[0].parentType").value("course"));
    }

    @Test
    void getAllQuizzes_ShouldReturnNotFound_WhenNoQuizzesExist() throws Exception {
        // Given
        when(quizService.getAllQuizzes()).thenThrow(new ResourceNotFoundException("No quizzes found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllQuizzes_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenQuizExists() throws Exception {
        // Given
        when(quizService.getQuizById(1L)).thenReturn(quizOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz retrieved successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Java Fundamentals Quiz"))
                .andExpect(jsonPath("$.data.parentId").value(1L));
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizService.getQuizById(1L)).thenReturn(quizOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz retrieved successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L));
    }

    @Test
    void getQuizById_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        // Given
        when(quizService.getQuizById(999L)).thenThrow(new ResourceNotFoundException("Quiz not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuizzesByCourse_ShouldReturnQuizzes_WhenCourseHasQuizzes() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourse(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course quizzes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(1L))
                .andExpect(jsonPath("$.data[0].parentType").value("course"))
                .andExpect(jsonPath("$.data[0].parentId").value(1L));
    }

    @Test
    void getQuizzesByCourse_ShouldReturnQuizzes_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourse(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course quizzes retrieved successfully"));
    }

    @Test
    void getQuizzesByCourse_ShouldReturnNotFound_WhenCourseHasNoQuizzes() throws Exception {
        // Given
        when(quizService.getQuizzesByCourse(999L)).thenThrow(new ResourceNotFoundException("No quizzes found for course"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuizzesByCourseContent_ShouldReturnQuizzes_WhenCourseContentHasQuizzes() throws Exception {
        // Given
        QuizOutDTO courseContentQuiz = QuizOutDTO.builder()
                .quizId(2L)
                .parentType("course-content")
                .parentId(1L)
                .title("Content Quiz")
                .description("Quiz for course content")
                .timeLimit(30)
                .attemptsAllowed(2)
                .passingScore(BigDecimal.valueOf(80.00))
                .randomizeQuestions(false)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<QuizOutDTO> quizzes = Arrays.asList(courseContentQuiz);
        when(quizService.getQuizzesByCourseContent(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course-content/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course content quizzes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(2L))
                .andExpect(jsonPath("$.data[0].parentType").value("course-content"))
                .andExpect(jsonPath("$.data[0].parentId").value(1L));
    }

    @Test
    void getQuizzesByCourseContent_ShouldReturnQuizzes_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourseContent(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course-content/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course content quizzes retrieved successfully"));
    }

    @Test
    void getQuizzesByCourseContent_ShouldReturnNotFound_WhenCourseContentHasNoQuizzes() throws Exception {
        // Given
        when(quizService.getQuizzesByCourseContent(999L)).thenThrow(new ResourceNotFoundException("No quizzes found for course content"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course-content/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateQuiz_ShouldReturnUpdatedQuiz_WhenValidInput() throws Exception {
        // Given
        QuizOutDTO updatedQuiz = QuizOutDTO.builder()
                .quizId(1L)
                .parentType("course")
                .parentId(1L)
                .title("Updated Java Fundamentals Quiz")
                .description("Updated description for Java basics quiz")
                .timeLimit(90)
                .attemptsAllowed(2)
                .passingScore(BigDecimal.valueOf(75.00))
                .randomizeQuestions(false)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class))).thenReturn(updatedQuiz);

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz updated successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Updated Java Fundamentals Quiz"))
                .andExpect(jsonPath("$.data.timeLimit").value(90))
                .andExpect(jsonPath("$.data.attemptsAllowed").value(2))
                .andExpect(jsonPath("$.data.passingScore").value(75.00))
                .andExpect(jsonPath("$.data.randomizeQuestions").value(false))
                .andExpect(jsonPath("$.data.showResults").value(true));
    }

    @Test
    void updateQuiz_ShouldReturnBadRequest_WhenInvalidTimeLimit() throws Exception {
        // Given - Invalid time limit exceeding maximum
        QuizUpdateInDTO invalidUpdate = QuizUpdateInDTO.builder()
                .title("Valid Title")
                .timeLimit(700) // Exceeds 600 minutes limit
                .attemptsAllowed(2)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateQuiz_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        // Given
        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quiz not found"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/999")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateQuiz_ShouldReturnConflict_WhenTitleAlreadyExists() throws Exception {
        // Given
        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Quiz with this title already exists"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteQuiz_ShouldReturnSuccess_WhenQuizExists() throws Exception {
        // Given
        doNothing().when(quizService).deleteQuiz(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz deleted successfully"))
                .andExpect(jsonPath("$.data").value("Quiz deleted successfully"));
    }

    @Test
    void deleteQuiz_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Quiz not found")).when(quizService).deleteQuiz(999L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quizzes/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenExceedingMaxAttempts() throws Exception {
        // Given - Invalid attempts exceeding maximum
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("Valid Title")
                .description("Description")
                .attemptsAllowed(15) // Exceeds maximum of 10
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenNegativePassingScore() throws Exception {
        // Given - Invalid negative passing score
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("Valid Title")
                .description("Description")
                .attemptsAllowed(3)
                .passingScore(BigDecimal.valueOf(-10.00)) // Negative score
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenTitleTooLong() throws Exception {
        // Given - Title exceeding 255 characters
        String longTitle = "A".repeat(256);
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title(longTitle)
                .description("Description")
                .attemptsAllowed(3)
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenDescriptionTooLong() throws Exception {
        // Given - Description exceeding 1000 characters
        String longDescription = "A".repeat(1001);
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("Valid Title")
                .description(longDescription)
                .attemptsAllowed(3)
                .isActive(true)
                .createdBy(1)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }
}