package com.example.course_service_lms.controllerTest;

import com.example.course_service_lms.controller.CourseBundleController;
import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.service.CourseBundleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseBundleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseBundleService courseBundleService;

    private CourseBundleController courseBundleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        courseBundleController = new CourseBundleController(courseBundleService);
        mockMvc = MockMvcBuilders.standaloneSetup(courseBundleController).build();
    }

    @Test
    void testCreateCourseBundle() throws Exception {
        // Mock service response
        CourseBundleDTO mockCourseBundleDTO = new CourseBundleDTO(1L, 101L, "MockBundle", 201L, "MockCourse");
        when(courseBundleService.createCourseBundle(any(CourseBundleDTO.class))).thenReturn(mockCourseBundleDTO);

        // Perform POST request
        mockMvc.perform(post("/api/bundles/course_bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseBundleId\": 1, \"bundleId\": 101, \"bundleName\": \"MockBundle\", \"courseId\": 201, \"courseName\": \"MockCourse\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseBundleId").value(1L))
                .andExpect(jsonPath("$.bundleName").value("MockBundle"))
                .andExpect(jsonPath("$.courseName").value("MockCourse"));
    }

    @Test
    void testGetAllCourseBundles() throws Exception {
        // Mock service response
        List<CourseBundleDTO> mockCourseBundles = List.of(
                new CourseBundleDTO(1L, 101L, "MockBundle1", 201L, "MockCourse1"),
                new CourseBundleDTO(2L, 102L, "MockBundle2", 202L, "MockCourse2")
        );
        when(courseBundleService.getAllCourseBundles()).thenReturn(mockCourseBundles);

        // Perform GET request
        mockMvc.perform(get("/api/bundles/course_bundles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].bundleName").value("MockBundle1"))
                .andExpect(jsonPath("$[1].bundleName").value("MockBundle2"));
    }

    @Test
    void testGetCourseBundleById() throws Exception {
        // Mock service response
        CourseBundleDTO mockCourseBundleDTO = new CourseBundleDTO(1L, 101L, "MockBundle", 201L, "MockCourse");
        when(courseBundleService.getCourseBundleById(1L)).thenReturn(mockCourseBundleDTO);

        // Perform GET request
        mockMvc.perform(get("/api/bundles/course_bundles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseBundleId").value(1L))
                .andExpect(jsonPath("$.bundleName").value("MockBundle"))
                .andExpect(jsonPath("$.courseName").value("MockCourse"));
    }

    @Test
    void testDeleteCourseBundle() throws Exception {
        // Perform DELETE request
        mockMvc.perform(delete("/api/bundles/course_bundles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course-bundle with ID 1 deleted successfully."));

        // Verify service interaction
        verify(courseBundleService, times(1)).deleteCourseBundle(1L);
    }

    @Test
    void testUpdateCourseBundle() throws Exception {
        // Mock service response
        CourseBundleDTO mockUpdatedCourseBundleDTO = new CourseBundleDTO(1L, 102L, "UpdatedBundle", 202L, "UpdatedCourse");
        when(courseBundleService.updateCourseBundle(eq(1L), any(CourseBundleDTO.class))).thenReturn(mockUpdatedCourseBundleDTO);

        // Perform PUT request
        mockMvc.perform(put("/api/bundles/course_bundles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseBundleId\": 1, \"bundleId\": 102, \"bundleName\": \"UpdatedBundle\", \"courseId\": 202, \"courseName\": \"UpdatedCourse\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bundleName").value("UpdatedBundle"))
                .andExpect(jsonPath("$.courseName").value("UpdatedCourse"));
    }
}