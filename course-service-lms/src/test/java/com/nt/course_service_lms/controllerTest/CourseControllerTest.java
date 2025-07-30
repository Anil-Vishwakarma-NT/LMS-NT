package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.CourseController;
import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@Import(CourseControllerTest.TestConfig.class)
class CourseControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CourseService courseService() {
            return Mockito.mock(CourseService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseService courseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CourseOutDTO sampleCourse;

    @BeforeEach
    void setup() {
        sampleCourse = CourseOutDTO.builder()
                .courseId(1L)
                .ownerId(101L)
                .title("Java Basics")
                .description("Learn Java")
                .level("BEGINNER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateCourse_Success() throws Exception {
        CourseInDTO input = CourseInDTO.builder()
                .title("Java Basics")
                .ownerId(101L)
                .description("Learn Java")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        Mockito.when(courseService.createCourse(any())).thenReturn(sampleCourse);

        mockMvc.perform(post("/api/service-api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Course created successfully"))
                .andExpect(jsonPath("$.data.title").value("Java Basics"));
    }

    @Test
    void testGetAllCourses() throws Exception {
        Mockito.when(courseService.getAllCourses()).thenReturn(Arrays.asList(sampleCourse));

        mockMvc.perform(get("/api/service-api/course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].courseId").value(1L));
    }

    @Test
    void testGetCourseById_Success() throws Exception {
        CourseInfoOutDTO info = new CourseInfoOutDTO();
        Mockito.when(courseService.getCourseById(1L)).thenReturn(info);

        mockMvc.perform(get("/api/service-api/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testUpdateCourse_Success() throws Exception {
        UpdateCourseInDTO update = UpdateCourseInDTO.builder()
                .title("Advanced Java")
                .ownerId(101L)
                .description("Deep Dive")
                .courseLevel("ADVANCED")
                .Active(true)
                .build();

        Mockito.when(courseService.updateCourse(eq(1L), any())).thenReturn(sampleCourse);

        mockMvc.perform(put("/api/service-api/course/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.title").value("Java Basics"));
    }

    @Test
    void testDeleteCourse_Success() throws Exception {
        Mockito.when(courseService.deleteCourse(1L)).thenReturn("Course deleted successfully");

        mockMvc.perform(delete("/api/service-api/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Course deleted successfully"));
    }

    @Test
    void testCreateCourse_InvalidInput() throws Exception {
        CourseInDTO invalid = new CourseInDTO();

        mockMvc.perform(post("/api/service-api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.ownerId").exists());
    }

    @Test
    void testGetCourseById_NotFound() throws Exception {
        Mockito.when(courseService.getCourseById(999L))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        mockMvc.perform(get("/api/service-api/course/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    void testUpdateCourse_TypeMismatch() throws Exception {
        mockMvc.perform(put("/api/service-api/course/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message", containsString("Failed to convert")));
    }

    @Test
    void testDeleteCourse_UncaughtException() throws Exception {
        Mockito.when(courseService.deleteCourse(1L)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(delete("/api/service-api/course/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

    @Test
    void testUpdateCourse_NotFound() throws Exception {
        UpdateCourseInDTO update = UpdateCourseInDTO.builder()
                .title("Advanced Java")
                .ownerId(101L)
                .description("Deep Dive")
                .courseLevel("ADVANCED")
                .Active(true)
                .build();

        Mockito.when(courseService.updateCourse(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        mockMvc.perform(put("/api/service-api/course/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    void testCountCourses() throws Exception {
        Mockito.when(courseService.countCourses()).thenReturn(42L);

        mockMvc.perform(get("/api/service-api/course/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(42));
    }

    @Test
    void testGetCoursesInfo() throws Exception {
        CourseInfoOutDTO info1 = new CourseInfoOutDTO();
        CourseInfoOutDTO info2 = new CourseInfoOutDTO();

        Mockito.when(courseService.getCoursesInfo()).thenReturn(Arrays.asList(info1, info2));

        mockMvc.perform(get("/api/service-api/course/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetRecentCourseSummaries() throws Exception {
        CourseSummaryOutDTO recent1 = CourseSummaryOutDTO.builder()
                .title("Python")
                .description("Intro")
                .level("Beginner")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CourseSummaryOutDTO recent2 = CourseSummaryOutDTO.builder()
                .title("JavaScript")
                .description("Web basics")
                .level("Beginner")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(courseService.getRecentCourseSummaries()).thenReturn(Arrays.asList(recent1, recent2));

        mockMvc.perform(get("/api/service-api/course/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Python"));
    }

    @Test
    void testFindExistingIds() throws Exception {
        List<Long> inputIds = Arrays.asList(1L, 2L, 3L, 999L);
        List<Long> existing = Arrays.asList(1L, 3L);

        Mockito.when(courseService.findExistingIds(inputIds)).thenReturn(existing);

        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0]").value(1));
    }

    @Test
    void testCourseExistsById_True() throws Exception {
        Mockito.when(courseService.courseExistsById(1L)).thenReturn(true);

        mockMvc.perform(get("/api/service-api/course/1/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testCourseExistsById_False() throws Exception {
        Mockito.when(courseService.courseExistsById(999L)).thenReturn(false);

        mockMvc.perform(get("/api/service-api/course/999/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(false));
    }
}


