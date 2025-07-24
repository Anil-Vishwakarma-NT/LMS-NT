package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.QuizController;
import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
@Import(QuizControllerTest.TestConfig.class)
class QuizControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public QuizService quizService() {
            return Mockito.mock(QuizService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizService quizService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuizOutDTO getMockQuizOutDTO(Long id) {
        return QuizOutDTO.builder()
                .quizId(id)
                .title("Sample Quiz")
                .description("This is a sample quiz.")
                .parentType("course")
                .parentId(1L)
                .isActive(true)
                .build();
    }

    @Test
    void testCreateQuizSuccess() throws Exception {
        QuizCreateInDTO dto = QuizCreateInDTO.builder()
                .title("New Quiz")
                .description("Quiz Description")
                .parentType("course")
                .parentId(1L)
                .build();

        QuizOutDTO response = getMockQuizOutDTO(1L);
        Mockito.when(quizService.createQuiz(any())).thenReturn(response);

        mockMvc.perform(post("/api/service-api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.quizId", is(1)));
    }

    @Test
    void testCreateQuizValidationFail() throws Exception {
        QuizCreateInDTO dto = new QuizCreateInDTO();

        mockMvc.perform(post("/api/service-api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllQuizzesSuccess() throws Exception {
        List<QuizOutDTO> quizzes = Arrays.asList(getMockQuizOutDTO(1L), getMockQuizOutDTO(2L));
        Mockito.when(quizService.getAllQuizzes()).thenReturn(quizzes);

        mockMvc.perform(get("/api/service-api/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].quizId", is(1)))
                .andExpect(jsonPath("$.data[1].quizId", is(2)));
    }

    @Test
    void testGetQuizByIdSuccess() throws Exception {
        Mockito.when(quizService.getQuizById(1L)).thenReturn(getMockQuizOutDTO(1L));

        mockMvc.perform(get("/api/service-api/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quizId", is(1)));
    }

    @Test
    void testGetQuizByIdNotFound() throws Exception {
        Mockito.when(quizService.getQuizById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/service-api/quizzes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetQuizzesByCourseSuccess() throws Exception {
        Mockito.when(quizService.getQuizzesByCourse(1L)).thenReturn(Arrays.asList(getMockQuizOutDTO(1L)));

        mockMvc.perform(get("/api/service-api/quizzes/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].parentType", is("course")));
    }

    @Test
    void testGetQuizzesByCourseContentSuccess() throws Exception {
        QuizOutDTO quiz = getMockQuizOutDTO(2L);
        quiz.setParentType("course-content");

        Mockito.when(quizService.getQuizzesByCourseContent(1L)).thenReturn(Arrays.asList(quiz));

        mockMvc.perform(get("/api/service-api/quizzes/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].parentType", is("course-content")));
    }

    @Test
    void testUpdateQuizSuccess() throws Exception {
        QuizUpdateInDTO updateDTO = QuizUpdateInDTO.builder()
                .title("Updated Quiz")
                .description("Updated Desc")
                .build();

        QuizOutDTO updated = getMockQuizOutDTO(1L);
        updated.setTitle("Updated Quiz");

        Mockito.when(quizService.updateQuiz(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Updated Quiz")));
    }

    @Test
    void testUpdateQuizValidationFail() throws Exception {
        QuizUpdateInDTO invalidDTO = new QuizUpdateInDTO();

        mockMvc.perform(put("/api/service-api/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteQuizSuccess() throws Exception {
        Mockito.doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(delete("/api/service-api/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("deleted")));
    }

    @Test
    void testDeleteQuizNotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Quiz not found")).when(quizService).deleteQuiz(999L);

        mockMvc.perform(delete("/api/service-api/quizzes/999"))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testUpdateQuizNotFound() throws Exception {
        QuizUpdateInDTO dto = QuizUpdateInDTO.builder()
                .title("Update")
                .description("Desc")
                .build();

        Mockito.when(quizService.updateQuiz(eq(999L), any()))
                .thenThrow(new RuntimeException("Quiz not found"));

        mockMvc.perform(put("/api/service-api/quizzes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testGetQuizzesByCourseEmpty() throws Exception {
        Mockito.when(quizService.getQuizzesByCourse(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/service-api/quizzes/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
    @Test
    void testGetQuizzesByCourseContentEmpty() throws Exception {
        Mockito.when(quizService.getQuizzesByCourseContent(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/service-api/quizzes/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

}

