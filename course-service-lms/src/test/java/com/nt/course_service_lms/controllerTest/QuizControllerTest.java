package com.nt.course_service_lms.controllerTest;

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
class QuizControllerTest {

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
                .title("Sample Quiz")
                .description("Sample quiz description")
                .timeLimit(60)
                .attemptsAllowed(3)
                .passingScore(new BigDecimal("75.00"))
                .randomizeQuestions(true)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .build();

        quizUpdateInDTO = QuizUpdateInDTO.builder()
                .title("Updated Quiz")
                .description("Updated description")
                .timeLimit(90)
                .attemptsAllowed(5)
                .passingScore(new BigDecimal("80.00"))
                .randomizeQuestions(false)
                .showResults(false)
                .isActive(false)
                .build();

        quizOutDTO = QuizOutDTO.builder()
                .quizId(1L)
                .parentType("course")
                .parentId(1L)
                .title("Sample Quiz")
                .description("Sample quiz description")
                .timeLimit(60)
                .attemptsAllowed(3)
                .passingScore(new BigDecimal("75.00"))
                .randomizeQuestions(true)
                .showResults(true)
                .isActive(true)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createQuiz_ShouldReturnCreatedQuiz_WhenValidInput() throws Exception {
        when(quizService.createQuiz(any(QuizCreateInDTO.class))).thenReturn(quizOutDTO);

        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.quizId").value(1L));
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder().title("").build();

        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnConflict_WhenQuizAlreadyExists() throws Exception {
        when(quizService.createQuiz(any(QuizCreateInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Quiz already exists"));

        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void createQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllQuizzes_ShouldReturnListOfQuizzes_WhenQuizzesExist() throws Exception {
        List<QuizOutDTO> quizzes = Collections.singletonList(quizOutDTO);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        mockMvc.perform(get("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quizId").value(1L));
    }

    @Test
    void getAllQuizzes_ShouldReturnNotFound_WhenNoQuizzesExist() throws Exception {
        when(quizService.getAllQuizzes()).thenThrow(new ResourceNotFoundException("No quizzes found"));

        mockMvc.perform(get("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllQuizzes_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/service-api/quizzes")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenAdminAccess() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(quizOutDTO);

        mockMvc.perform(get("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quizId").value(1L));
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenEmployeeAccess() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(quizOutDTO);

        mockMvc.perform(get("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk());
    }

    @Test
    void getQuizById_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        when(quizService.getQuizById(999L)).thenThrow(new ResourceNotFoundException("Quiz not found"));

        mockMvc.perform(get("/api/service-api/quizzes/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuizzesByCourse_ShouldReturnQuizzes_WhenEmployeeAccess() throws Exception {
        List<QuizOutDTO> quizzes = Collections.singletonList(quizOutDTO);
        when(quizService.getQuizzesByCourse(1L)).thenReturn(quizzes);

        mockMvc.perform(get("/api/service-api/quizzes/course/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quizId").value(1L));
    }

    @Test
    void getQuizzesByCourseContent_ShouldReturnQuizzes_WhenEmployeeAccess() throws Exception {
        List<QuizOutDTO> quizzes = Collections.singletonList(quizOutDTO);
        when(quizService.getQuizzesByCourseContent(1L)).thenReturn(quizzes);

        mockMvc.perform(get("/api/service-api/quizzes/course-content/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quizId").value(1L));
    }

    @Test
    void updateQuiz_ShouldReturnUpdatedQuiz_WhenValidInput() throws Exception {
        quizOutDTO.setTitle("Updated Quiz");
        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class))).thenReturn(quizOutDTO);

        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Quiz"));
    }

    @Test
    void updateQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        QuizUpdateInDTO invalidUpdate = QuizUpdateInDTO.builder()
                .title(String.join("", Collections.nCopies(256, "A"))).build();

        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteQuiz_ShouldReturnSuccess_WhenQuizExists() throws Exception {
        doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(delete("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Quiz deleted successfully"));
    }

    @Test
    void deleteQuiz_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Quiz not found")).when(quizService).deleteQuiz(999L);

        mockMvc.perform(delete("/api/service-api/quizzes/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createQuiz_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateQuiz_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createQuiz_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(quizService.createQuiz(any(QuizCreateInDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/service-api/quizzes")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isInternalServerError());
    }
}