//package com.nt.course_service_lms.controllerTest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nt.course_service_lms.controller.QuizController;
//import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
//import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
//import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
//import com.nt.course_service_lms.service.QuizService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(QuizController.class)
//@Import(QuizControllerTest.TestConfig.class)
//class QuizControllerTest {
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public QuizService quizService() {
//            return Mockito.mock(QuizService.class);
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private QuizService quizService;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private QuizOutDTO getMockQuizOutDTO(Long id) {
//        return QuizOutDTO.builder()
//                .quizId(id)
//                .title("Sample Quiz")
//                .description("This is a sample quiz.")
//                .parentType("course")
//                .parentId(1L)
//                .isActive(true)
//                .build();
//    }
//
//    @Test
//    void testCreateQuizSuccess() throws Exception {
//        QuizCreateInDTO dto = QuizCreateInDTO.builder()
//                .title("New Quiz")
//                .description("Quiz Description")
//                .parentType("course")
//                .parentId(1L)
//                .build();
//
//        QuizOutDTO response = getMockQuizOutDTO(1L);
//        Mockito.when(quizService.createQuiz(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/service-api/quizzes")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.quizId", is(1)));
//    }
//
//    @Test
//    void testCreateQuizValidationFail() throws Exception {
//        QuizCreateInDTO dto = new QuizCreateInDTO();
//
//        mockMvc.perform(post("/api/service-api/quizzes")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testGetAllQuizzesSuccess() throws Exception {
//        List<QuizOutDTO> quizzes = Arrays.asList(getMockQuizOutDTO(1L), getMockQuizOutDTO(2L));
//        Mockito.when(quizService.getAllQuizzes()).thenReturn(quizzes);
//
//        mockMvc.perform(get("/api/service-api/quizzes"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].quizId", is(1)))
//                .andExpect(jsonPath("$.data[1].quizId", is(2)));
//    }
//
//    @Test
//    void testGetQuizByIdSuccess() throws Exception {
//        Mockito.when(quizService.getQuizById(1L)).thenReturn(getMockQuizOutDTO(1L));
//
//        mockMvc.perform(get("/api/service-api/quizzes/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.quizId", is(1)));
//    }
//
//    @Test
//    void testGetQuizByIdNotFound() throws Exception {
//        Mockito.when(quizService.getQuizById(999L)).thenReturn(null);
//
//        mockMvc.perform(get("/api/service-api/quizzes/999"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testGetQuizzesByCourseSuccess() throws Exception {
//        Mockito.when(quizService.getQuizzesByCourse(1L)).thenReturn(Arrays.asList(getMockQuizOutDTO(1L)));
//
//        mockMvc.perform(get("/api/service-api/quizzes/course/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].parentType", is("course")));
//    }
//
//    @Test
//    void testGetQuizzesByCourseContentSuccess() throws Exception {
//        QuizOutDTO quiz = getMockQuizOutDTO(2L);
//        quiz.setParentType("course-content");
//
//        Mockito.when(quizService.getQuizzesByCourseContent(1L)).thenReturn(Arrays.asList(quiz));
//
//        mockMvc.perform(get("/api/service-api/quizzes/course-content/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].parentType", is("course-content")));
//    }
//
//    @Test
//    void testUpdateQuizSuccess() throws Exception {
//        QuizUpdateInDTO updateDTO = QuizUpdateInDTO.builder()
//                .title("Updated Quiz")
//                .description("Updated Desc")
//                .build();
//
//        QuizOutDTO updated = getMockQuizOutDTO(1L);
//        updated.setTitle("Updated Quiz");
//
//        Mockito.when(quizService.updateQuiz(eq(1L), any())).thenReturn(updated);
//
//        mockMvc.perform(put("/api/service-api/quizzes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.title", is("Updated Quiz")));
//    }
//
//    @Test
//    void testUpdateQuizValidationFail() throws Exception {
//        QuizUpdateInDTO invalidDTO = new QuizUpdateInDTO();
//
//        mockMvc.perform(put("/api/service-api/quizzes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testDeleteQuizSuccess() throws Exception {
//        Mockito.doNothing().when(quizService).deleteQuiz(1L);
//
//        mockMvc.perform(delete("/api/service-api/quizzes/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", containsString("deleted")));
//    }
//
//    @Test
//    void testDeleteQuizNotFound() throws Exception {
//        Mockito.doThrow(new RuntimeException("Quiz not found")).when(quizService).deleteQuiz(999L);
//
//        mockMvc.perform(delete("/api/service-api/quizzes/999"))
//                .andExpect(status().isInternalServerError());
//    }
//    @Test
//    void testUpdateQuizNotFound() throws Exception {
//        QuizUpdateInDTO dto = QuizUpdateInDTO.builder()
//                .title("Update")
//                .description("Desc")
//                .build();
//
//        Mockito.when(quizService.updateQuiz(eq(999L), any()))
//                .thenThrow(new RuntimeException("Quiz not found"));
//
//        mockMvc.perform(put("/api/service-api/quizzes/999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isInternalServerError());
//    }
//    @Test
//    void testGetQuizzesByCourseEmpty() throws Exception {
//        Mockito.when(quizService.getQuizzesByCourse(1L)).thenReturn(Arrays.asList());
//
//        mockMvc.perform(get("/api/service-api/quizzes/course/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", hasSize(0)));
//    }
//    @Test
//    void testGetQuizzesByCourseContentEmpty() throws Exception {
//        Mockito.when(quizService.getQuizzesByCourseContent(1L)).thenReturn(Arrays.asList());
//
//        mockMvc.perform(get("/api/service-api/quizzes/course-content/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", hasSize(0)));
//    }
//
//}
//

package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.SecurityConfig;
import com.nt.course_service_lms.config.ServiceAuthenticationFilter;
import com.nt.course_service_lms.controller.QuizController;
import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.QuizService;
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

@WebMvcTest(QuizController.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private ServiceAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private QuizCreateInDTO quizCreateInDTO;
    private QuizUpdateInDTO quizUpdateInDTO;
    private QuizOutDTO quizOutDTO;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter to DO NOTHING but continue the chain
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(serviceAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

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
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturnCreatedQuiz_WhenValidInput() throws Exception {
        // Given
        when(quizService.createQuiz(any(QuizCreateInDTO.class))).thenReturn(quizOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz created successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Sample Quiz"))
                .andExpect(jsonPath("$.data.parentType").value("course"))
                .andExpect(jsonPath("$.data.parentId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid quiz with empty title
        QuizCreateInDTO invalidQuiz = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(1L)
                .title("")
                .attemptsAllowed(3)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturnConflict_WhenQuizAlreadyExists() throws Exception {
        // Given
        when(quizService.createQuiz(any(QuizCreateInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Quiz with title 'Sample Quiz' already exists"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllQuizzes_ShouldReturnListOfQuizzes_WhenQuizzesExist() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("All quizzes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Sample Quiz"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllQuizzes_ShouldReturnNotFound_WhenNoQuizzesExist() throws Exception {
        // Given
        when(quizService.getAllQuizzes()).thenThrow(new ResourceNotFoundException("No quizzes found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllQuizzes_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizById_ShouldReturnQuiz_WhenQuizExists() throws Exception {
        // Given
        when(quizService.getQuizById(1L)).thenReturn(quizOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz retrieved successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Sample Quiz"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizById_ShouldReturnQuiz_WhenEmployeeAccess() throws Exception {
        // Given
        when(quizService.getQuizById(1L)).thenReturn(quizOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizById_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        // Given
        when(quizService.getQuizById(999L)).thenThrow(new ResourceNotFoundException("Quiz with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getQuizById_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizzesByCourse_ShouldReturnQuizzes_WhenQuizzesExist() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourse(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course quizzes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizzesByCourse_ShouldReturnQuizzes_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourse(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course quizzes retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizzesByCourse_ShouldReturnNotFound_WhenNoQuizzesExist() throws Exception {
        // Given
        when(quizService.getQuizzesByCourse(999L))
                .thenThrow(new ResourceNotFoundException("No quizzes found for course ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizzesByCourseContent_ShouldReturnQuizzes_WhenQuizzesExist() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourseContent(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course content quizzes retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].quizId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getQuizzesByCourseContent_ShouldReturnQuizzes_WhenEmployeeAccess() throws Exception {
        // Given
        List<QuizOutDTO> quizzes = Arrays.asList(quizOutDTO);
        when(quizService.getQuizzesByCourseContent(1L)).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course content quizzes retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQuizzesByCourseContent_ShouldReturnNotFound_WhenNoQuizzesExist() throws Exception {
        // Given
        when(quizService.getQuizzesByCourseContent(999L))
                .thenThrow(new ResourceNotFoundException("No quizzes found for course content ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/service-api/quizzes/course-content/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldReturnUpdatedQuiz_WhenValidInput() throws Exception {
        // Given
        QuizOutDTO updatedQuiz = QuizOutDTO.builder()
                .quizId(1L)
                .parentType("course")
                .parentId(1L)
                .title("Updated Quiz")
                .description("Updated description")
                .timeLimit(90)
                .attemptsAllowed(5)
                .passingScore(new BigDecimal("80.00"))
                .randomizeQuestions(false)
                .showResults(false)
                .isActive(false)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class))).thenReturn(updatedQuiz);

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz updated successfully"))
                .andExpect(jsonPath("$.data.quizId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Updated Quiz"))
                .andExpect(jsonPath("$.data.isActive").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with too long title
        QuizUpdateInDTO invalidUpdate = QuizUpdateInDTO.builder()
                .title(String.join("", java.util.Collections.nCopies(256, "A")))
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        // Given
        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quiz with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldReturnConflict_WhenTitleAlreadyExists() throws Exception {
        // Given
        when(quizService.updateQuiz(anyLong(), any(QuizUpdateInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Quiz with title 'Updated Quiz' already exists"));

        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizUpdateInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_ShouldReturnSuccess_WhenQuizExists() throws Exception {
        // Given
        doNothing().when(quizService).deleteQuiz(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quizzes/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Quiz deleted successfully"))
                .andExpect(jsonPath("$.data").value("Quiz deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_ShouldReturnNotFound_WhenQuizDoesNotExist() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Quiz with ID 999 not found"))
                .when(quizService).deleteQuiz(999L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/quizzes/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteQuiz_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/quizzes/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        // Given
        when(quizService.createQuiz(any(QuizCreateInDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/service-api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizCreateInDTO)))
                .andExpect(status().isInternalServerError());
    }
}