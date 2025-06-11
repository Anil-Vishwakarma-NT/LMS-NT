package com.example.course_service_lms.controllerTest;

import com.example.course_service_lms.controller.CourseBundleController;
import com.example.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.example.course_service_lms.dto.inDTO.CourseBundleInDTO;
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
        CourseBundleInDTO mockCourseBundleInDTO = new CourseBundleInDTO(1L, 101L, 201L);
        when(courseBundleService.createCourseBundle(any(CourseBundleInDTO.class))).thenReturn(mockCourseBundleInDTO);

        // Perform POST request
        mockMvc.perform(post("/api/bundles/course_bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseBundleId\": 1, \"bundleId\": 101, \"courseId\": 201}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseBundleId").value(1L));
    }

    @Test
    void testGetAllCourseBundles() throws Exception {
        // Mock service response
        List<CourseBundleOutDTO> mockCourseBundles = List.of(
                new CourseBundleOutDTO(1L, 101L, "MockBundle1", 201L, "MockCourse1"),
                new CourseBundleOutDTO(2L, 102L, "MockBundle2", 202L, "MockCourse2")
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
        CourseBundleOutDTO mockCourseBundleOutDTO = new CourseBundleOutDTO(1L, 101L, "MockBundle", 201L, "MockCourse");
        when(courseBundleService.getCourseBundleById(1L)).thenReturn(mockCourseBundleOutDTO);

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
        CourseBundleInDTO mockUpdatedCourseBundleInDTO = new CourseBundleInDTO(1L, 102L,  202L);
        when(courseBundleService.updateCourseBundle(eq(1L), any(CourseBundleInDTO.class))).thenReturn(mockUpdatedCourseBundleInDTO);

        // Perform PUT request
        mockMvc.perform(put("/api/bundles/course_bundles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseBundleId\": 1, \"bundleId\": 102, \"courseId\": 202}"))
                .andExpect(status().isOk());
    }
}