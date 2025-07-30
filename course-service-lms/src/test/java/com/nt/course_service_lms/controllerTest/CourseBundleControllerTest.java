package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.controller.CourseBundleController;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.CourseBundleService;

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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseBundleController.class)
@Import(CourseBundleControllerTest.TestConfig.class)
class CourseBundleControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CourseBundleService courseBundleService() {
            return Mockito.mock(CourseBundleService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseBundleService courseBundleService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CourseBundleOutDTO sampleOutDTO;

    @BeforeEach
    void setup() {
        sampleOutDTO = CourseBundleOutDTO.builder()
                .courseBundleId(1L)
                .bundleId(10L)
                .bundleName("Full Stack Bundle")
                .courseId(20L)
                .courseName("Java Mastery")
                .build();
    }

    @Test
    void createCourseBundle_ReturnsCreated() throws Exception {
        CourseBundleInDTO input = new CourseBundleInDTO();
        input.setBundleId(10L);
        input.setCourseId(20L);

        CourseBundle saved = new CourseBundle();
        saved.setCourseBundleId(1L);
        saved.setBundleId(10L);
        saved.setCourseId(20L);

        Mockito.when(courseBundleService.createCourseBundle(any())).thenReturn(saved);

        mockMvc.perform(post("/api/service-api/course-bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Course Bundle created successfully."))
                .andExpect(jsonPath("$.data.courseBundleId").value(1));
    }



    @Test
    void createCourseBundle_Duplicate_ReturnsBadRequest() throws Exception {
        CourseBundleInDTO input = new CourseBundleInDTO();
        input.setBundleId(10L);
        input.setCourseId(20L);

        Mockito.when(courseBundleService.createCourseBundle(any()))
                .thenThrow(new ResourceAlreadyExistsException("Duplicate course in bundle"));

        mockMvc.perform(post("/api/service-api/course-bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCourseBundles_ReturnsList() throws Exception {
        Mockito.when(courseBundleService.getAllCourseBundles()).thenReturn(Arrays.asList(sampleOutDTO));

        mockMvc.perform(get("/api/service-api/course-bundles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].bundleName").value("Full Stack Bundle"));
    }

    @Test
    void getAllCourseBundles_EmptyList_ReturnsEmptyArray() throws Exception {
        Mockito.when(courseBundleService.getAllCourseBundles()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/service-api/course-bundles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void getCourseBundleById_ReturnsBundle() throws Exception {
        Mockito.when(courseBundleService.getCourseBundleById(1L)).thenReturn(sampleOutDTO);

        mockMvc.perform(get("/api/service-api/course-bundles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseName").value("Java Mastery"));
    }

    @Test
    void getCourseBundleById_NotFound_Returns404() throws Exception {
        Mockito.when(courseBundleService.getCourseBundleById(999L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/service-api/course-bundles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCourseBundleById_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/service-api/course-bundles/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourseBundle_ReturnsUpdatedMessage() throws Exception {
        UpdateCourseBundleInDTO updateDTO = new UpdateCourseBundleInDTO();
        updateDTO.setBundleId(10L);
        updateDTO.setCourseId(20L);

        Mockito.when(courseBundleService.updateCourseBundle(eq(1L), any()))
                .thenReturn("CourseBundle updated successfully");

        mockMvc.perform(put("/api/service-api/course-bundles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course bundle with id1 updated successfully."));
    }

    @Test
    void updateCourseBundle_NotFound_Returns404() throws Exception {
        UpdateCourseBundleInDTO updateDTO = new UpdateCourseBundleInDTO();
        updateDTO.setBundleId(10L);
        updateDTO.setCourseId(20L);

        Mockito.when(courseBundleService.updateCourseBundle(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/service-api/course-bundles/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourseBundle_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/service-api/course-bundles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course-bundle with ID 1 deleted successfully."));
    }

    @Test
    void deleteCourseBundle_NotFound_Returns404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Not found"))
                .when(courseBundleService).deleteCourseBundle(99L);

        mockMvc.perform(delete("/api/service-api/course-bundles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourseBundle_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/service-api/course-bundles/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCoursesByBundleId_ReturnsCourseList() throws Exception {
        CourseBundle entity = new CourseBundle();
        entity.setCourseBundleId(1L);
        entity.setBundleId(10L);
        entity.setCourseId(20L);

        Mockito.when(courseBundleService.getAllCoursesByBundle(10L)).thenReturn(Arrays.asList(entity));

        mockMvc.perform(get("/api/service-api/course-bundles/bundle/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].bundleId").value(10));
    }

    @Test
    void getCoursesByBundleId_EmptyList_ReturnsEmptyArray() throws Exception {
        Mockito.when(courseBundleService.getAllCoursesByBundle(50L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/service-api/course-bundles/bundle/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}

