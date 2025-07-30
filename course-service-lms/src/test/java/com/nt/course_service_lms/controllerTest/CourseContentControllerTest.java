package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.CourseContentController;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.CourseContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseContentController.class)
@Import(CourseContentControllerTest.TestConfig.class)
class CourseContentControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CourseContentService courseContentService() {
            return mock(CourseContentService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseContentService courseContentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CourseContentOutDTO sampleOutDTO;

    @BeforeEach
    void setUp() {
        sampleOutDTO = CourseContentOutDTO.builder()
                .courseContentId(1L)
                .courseId(100L)
                .title("Sample Title")
                .description("Sample Description")
                .resourceLink("http://example.com/resource")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createCourseContent_shouldReturnCreatedContent() throws Exception {
        CourseContentInDTO inDTO = CourseContentInDTO.builder()
                .courseId(100L)
                .title("Sample Title")
                .description("Sample Description")
                .resourceLink("http://example.com/resource")
                .isActive(true)
                .build();

        when(courseContentService.createCourseContent(any())).thenReturn(sampleOutDTO);

        mockMvc.perform(post("/api/service-api/course-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.courseContentId").value(1L));
    }

    @Test
    void getCourseContentById_shouldReturnContent() throws Exception {
        when(courseContentService.getCourseContentById(1L)).thenReturn(sampleOutDTO);

        mockMvc.perform(get("/api/service-api/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseContentId").value(1L));
    }

    @Test
    void getCourseContentById_notFound() throws Exception {
        when(courseContentService.getCourseContentById(99L))
                .thenThrow(new ResourceNotFoundException("CourseContent not found"));

        mockMvc.perform(get("/api/service-api/course-content/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("CourseContent not found"));
    }

    @Test
    void getAllCourseContents_shouldReturnList() throws Exception {
        when(courseContentService.getAllCourseContents()).thenReturn(Arrays.asList(sampleOutDTO));

        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].courseContentId").value(1L));
    }

    @Test
    void updateCourseContent_shouldReturnUpdatedContent() throws Exception {
        UpdateCourseContentInDTO updateDTO = UpdateCourseContentInDTO.builder()
                .courseId(100L)
                .title("Updated Title")
                .description("Updated Description")
                .resourceLink("http://example.com/resource")
                .isActive(true)
                .build();

        CourseContentOutDTO updatedOutDTO = CourseContentOutDTO.builder()
                .courseContentId(1L)
                .courseId(100L)
                .title("Updated Title")
                .description("Updated Description")
                .resourceLink("http://example.com/resource")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseContentService.updateCourseContent(eq(1L), any())).thenReturn(updatedOutDTO);

        mockMvc.perform(put("/api/service-api/course-content/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    void deleteCourseContent_shouldReturnSuccessMessage() throws Exception {
        when(courseContentService.deleteCourseContent(1L)).thenReturn("Deleted Successfully");

        mockMvc.perform(delete("/api/service-api/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted Successfully"));
    }

    @Test
    void getAllCourseContentByCourseId_shouldReturnList() throws Exception {
        when(courseContentService.getAllCourseContentByCourseId(100L))
                .thenReturn(Arrays.asList(sampleOutDTO));

        mockMvc.perform(get("/api/service-api/course-content/100"))
                .andExpect(status().isOk());
    }
    @Test
    void createCourseContent_missingRequiredFields_shouldReturnBadRequest() throws Exception {
        CourseContentInDTO invalidDTO = CourseContentInDTO.builder()
                .courseId(9L)
                .title("")      // required
                .description("Some description")
                .resourceLink("http://example.com")
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/service-api/course-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getCourseContentById_invalidIdFormat_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/service-api/course-content/abc")) // should be a number
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'id' must be of type 'Long'. Provided value: 'abc'"));
    }
    @Test
    void getAllCourseContents_shouldReturnEmptyList() throws Exception {
        when(courseContentService.getAllCourseContents()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    void createCourseContent_duplicate_shouldReturnConflict() throws Exception {
        CourseContentInDTO inDTO = CourseContentInDTO.builder()
                .courseId(100L)
                .title("Duplicate Title")
                .description("Duplicate")
                .resourceLink("http://example.com")
                .isActive(true)
                .build();

        when(courseContentService.createCourseContent(any()))
                .thenThrow(new ResourceAlreadyExistsException("Content already exists"));

        mockMvc.perform(post("/api/service-api/course-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Content already exists"));
    }
    @Test
    void updateCourseContent_notFound_shouldReturnNotFound() throws Exception {
        UpdateCourseContentInDTO updateDTO = UpdateCourseContentInDTO.builder()
                .courseId(100L)
                .title("Updated Title")
                .description("Updated Description")
                .resourceLink("http://example.com")
                .isActive(true)
                .build();

        when(courseContentService.updateCourseContent(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        mockMvc.perform(put("/api/service-api/course-content/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course content not found"));
    }
    @Test
    void deleteCourseContent_notFound_shouldReturnNotFound() throws Exception {
        when(courseContentService.deleteCourseContent(999L))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        mockMvc.perform(delete("/api/service-api/course-content/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course content not found"));
    }
    @Test
    void updateCourseContent_missingFields_shouldReturnBadRequest() throws Exception {
        UpdateCourseContentInDTO invalidDTO = UpdateCourseContentInDTO.builder()
                .courseId(22L)
                .title("")       // required
                .description("Desc")
                .resourceLink("http://example.com")
                .isActive(true)
                .build();

        mockMvc.perform(put("/api/service-api/course-content/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getCourseContentCount_shouldReturnCount() throws Exception {
        when(courseContentService.getAllCourseContentByCourseId(100L))
                .thenReturn(Arrays.asList(sampleOutDTO, sampleOutDTO));

        mockMvc.perform(get("/api/service-api/course-content/course/100/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(2))
                .andExpect(jsonPath("$.message").value("Course Content Count Retrieved Successfully"));
    }
    @Test
    void getCourseContentCount_shouldReturnZero() throws Exception {
        when(courseContentService.getAllCourseContentByCourseId(200L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/service-api/course-content/course/200/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
    }


}
