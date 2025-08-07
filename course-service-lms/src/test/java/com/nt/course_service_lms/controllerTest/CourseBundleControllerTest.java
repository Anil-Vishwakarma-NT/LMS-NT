package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.CourseBundleController;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.service.CourseBundleService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseBundleController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CourseBundleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseBundleService courseBundleService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private CourseBundleInDTO courseBundleInDTO;
    private UpdateCourseBundleInDTO updateCourseBundleInDTO;
    private CourseBundleOutDTO courseBundleOutDTO;
    private CourseBundle courseBundle;
    private CourseInfoOutDTO courseInfoOutDTO;
    private BundleInfoOutDTO bundleInfoOutDTO;
    private BundleSummaryOutDTO bundleSummaryOutDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        courseBundleInDTO = CourseBundleInDTO.builder()
                .courseBundleId(1L)
                .bundleId(1L)
                .courseId(1L)
                .isActive(true)
                .build();

        updateCourseBundleInDTO = UpdateCourseBundleInDTO.builder()
                .bundleId(2L)
                .courseId(2L)
                .isActive(false)
                .build();

        courseBundleOutDTO = CourseBundleOutDTO.builder()
                .courseBundleId(1L)
                .bundleId(1L)
                .bundleName("Test Bundle")
                .courseId(1L)
                .courseName("Test Course")
                .build();

        courseBundle = CourseBundle.builder()
                .courseBundleId(1L)
                .bundleId(1L)
                .courseId(1L)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bundleInfoOutDTO = BundleInfoOutDTO.builder()
                .bundleId(1L)
                .bundleName("Test Bundle")
                .totalCourses(5L)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bundleSummaryOutDTO = BundleSummaryOutDTO.builder()
                .bundleId(1L)
                .bundleName("Test Bundle")
                .courseCount(3L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        courseInfoOutDTO = CourseInfoOutDTO.builder()
                .courseId(1L)
                .courseLevel("BEGINNER")
                .title("Course Test")
                .ownerId(1L)
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .description("Description")
                .build();
    }

    // CREATE COURSE BUNDLE TESTS
    @Test
    void createCourseBundle_ShouldReturnCreatedCourseBundle_WhenValidInput() throws Exception {
        // Given
        when(courseBundleService.createCourseBundle(any(CourseBundleInDTO.class))).thenReturn(courseBundle);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseBundleInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Bundle created successfully."))
                .andExpect(jsonPath("$.data.courseBundleId").value(1L))
                .andExpect(jsonPath("$.data.bundleId").value(1L))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void createCourseBundle_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid course bundle with null bundleId
        CourseBundleInDTO invalidCourseBundle = CourseBundleInDTO.builder()
                .courseBundleId(1L)
                .bundleId(null)
                .courseId(1L)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseBundle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseBundle_ShouldReturnBadRequest_WhenNegativeBundleId() throws Exception {
        // Given - Invalid course bundle with negative bundleId
        CourseBundleInDTO invalidCourseBundle = CourseBundleInDTO.builder()
                .courseBundleId(1L)
                .bundleId(-1L)
                .courseId(1L)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseBundle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseBundle_ShouldReturnBadRequest_WhenNegativeCourseId() throws Exception {
        // Given - Invalid course bundle with negative courseId
        CourseBundleInDTO invalidCourseBundle = CourseBundleInDTO.builder()
                .courseBundleId(1L)
                .bundleId(1L)
                .courseId(-1L)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseBundle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseBundle_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseBundle_ShouldReturnConflict_WhenResourceAlreadyExists() throws Exception {
        // Given
        when(courseBundleService.createCourseBundle(any(CourseBundleInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Course bundle already exists"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseBundleInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void createCourseBundle_ShouldReturnBadRequest_WhenResourceNotValid() throws Exception {
        // Given
        when(courseBundleService.createCourseBundle(any(CourseBundleInDTO.class)))
                .thenThrow(new ResourceNotValidException("Invalid bundle or course ID"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseBundleInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseBundle_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course-bundles")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseBundleInDTO)))
                .andExpect(status().isForbidden());
    }

    // GET ALL COURSE BUNDLES TESTS
    @Test
    void getAllCourseBundles_ShouldReturnListOfCourseBundles_WhenCourseBundlesExist() throws Exception {
        // Given
        List<CourseBundleOutDTO> courseBundles = Arrays.asList(courseBundleOutDTO);
        when(courseBundleService.getAllCourseBundles()).thenReturn(courseBundles);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("All course bundles retrieved successfully."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseBundleId").value(1L))
                .andExpect(jsonPath("$.data[0].bundleName").value("Test Bundle"))
                .andExpect(jsonPath("$.data[0].courseName").value("Test Course"));
    }

    @Test
    void getAllCourseBundles_ShouldReturnListOfCourseBundles_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseBundleOutDTO> courseBundles = Arrays.asList(courseBundleOutDTO);
        when(courseBundleService.getAllCourseBundles()).thenReturn(courseBundles);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllCourseBundles_ShouldReturnNotFound_WhenNoCourseBundlesExist() throws Exception {
        // Given
        when(courseBundleService.getAllCourseBundles())
                .thenThrow(new ResourceNotFoundException("No course bundles found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // GET COURSE BUNDLE BY ID TESTS
    @Test
    void getCourseBundleById_ShouldReturnCourseBundle_WhenCourseBundleExists() throws Exception {
        // Given
        when(courseBundleService.getCourseBundleById(1L)).thenReturn(courseBundleOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Bundle with id:1 retrieved successfully."))
                .andExpect(jsonPath("$.data.courseBundleId").value(1L))
                .andExpect(jsonPath("$.data.bundleName").value("Test Bundle"));
    }

    @Test
    void getCourseBundleById_ShouldReturnCourseBundle_WhenEmployeeRole() throws Exception {
        // Given
        when(courseBundleService.getCourseBundleById(1L)).thenReturn(courseBundleOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getCourseBundleById_ShouldReturnNotFound_WhenCourseBundleDoesNotExist() throws Exception {
        // Given
        when(courseBundleService.getCourseBundleById(999L))
                .thenThrow(new ResourceNotFoundException("Course bundle not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // DELETE COURSE BUNDLE TESTS
    @Test
    void deleteCourseBundle_ShouldReturnSuccess_WhenCourseBundleExists() throws Exception {
        // Given
        doNothing().when(courseBundleService).deleteCourseBundle(1L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course-bundle with ID 1 deleted successfully."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteCourseBundle_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCourseBundle_ShouldReturnNotFound_WhenCourseBundleDoesNotExist() throws Exception {
        // Given
        doAnswer(invocation -> {
            throw new ResourceNotFoundException("Course bundle not found");
        }).when(courseBundleService).deleteCourseBundle(999L);

        // When & Then
        mockMvc.perform(delete("/api/service-api/course-bundles/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // UPDATE COURSE BUNDLE TESTS
    @Test
    void updateCourseBundle_ShouldReturnUpdatedMessage_WhenValidInput() throws Exception {
        // Given
        String updateMessage = "Course Bundle Updated Successfully";
        when(courseBundleService.updateCourseBundle(anyLong(), any(UpdateCourseBundleInDTO.class)))
                .thenReturn(updateMessage);

        // When & Then
        mockMvc.perform(put("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseBundleInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course bundle with id1 updated successfully."))
                .andExpect(jsonPath("$.data").value(updateMessage));
    }

    @Test
    void updateCourseBundle_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with null bundleId
        UpdateCourseBundleInDTO invalidUpdate = UpdateCourseBundleInDTO.builder()
                .bundleId(null)
                .courseId(1L)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourseBundle_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/course-bundles/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseBundleInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCourseBundle_ShouldReturnNotFound_WhenCourseBundleDoesNotExist() throws Exception {
        // Given
        when(courseBundleService.updateCourseBundle(anyLong(), any(UpdateCourseBundleInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course bundle not found"));

        // When & Then
        mockMvc.perform(put("/api/service-api/course-bundles/999")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseBundleInDTO)))
                .andExpect(status().isNotFound());
    }

    // GET ALL COURSES BY BUNDLE ID TESTS
    @Test
    void getAllCoursesByBundleId_ShouldReturnCourses_WhenCoursesExist() throws Exception {
        // Given
        List<CourseInfoOutDTO> courseBundles = Arrays.asList(courseInfoOutDTO);
        when(courseBundleService.getAllCoursesByBundle(1L)).thenReturn(courseBundles);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/bundle/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course bundles with id retrieved successfully."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L));
    }

    @Test
    void getAllCoursesByBundleId_ShouldReturnCourses_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseInfoOutDTO> courseBundles = Arrays.asList(courseInfoOutDTO);
        when(courseBundleService.getAllCoursesByBundle(1L)).thenReturn(courseBundles);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/bundle/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllCoursesByBundleId_ShouldReturnNotFound_WhenNoCoursesInBundle() throws Exception {
        // Given
        when(courseBundleService.getAllCoursesByBundle(999L))
                .thenThrow(new ResourceNotFoundException("No courses in the bundle"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/bundle/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // GET ALL BUNDLE INFO TESTS
    @Test
    void getAllBundleInfo_ShouldReturnBundleInfo_WhenBundlesExist() throws Exception {
        // Given
        List<BundleInfoOutDTO> bundleInfoList = Arrays.asList(bundleInfoOutDTO);
        when(courseBundleService.getBundlesInfo()).thenReturn(bundleInfoList);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/info")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundles info retrieved successfully."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bundleId").value(1L))
                .andExpect(jsonPath("$.data[0].bundleName").value("Test Bundle"))
                .andExpect(jsonPath("$.data[0].totalCourses").value(5L));
    }

    @Test
    void getAllBundleInfo_ShouldReturnBundleInfo_WhenEmployeeRole() throws Exception {
        // Given
        List<BundleInfoOutDTO> bundleInfoList = Arrays.asList(bundleInfoOutDTO);
        when(courseBundleService.getBundlesInfo()).thenReturn(bundleInfoList);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/info")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // GET RECENT BUNDLES TESTS
    @Test
    void getRecentBundles_ShouldReturnRecentBundles_WhenBundlesExist() throws Exception {
        // Given
        List<BundleSummaryOutDTO> bundleSummaries = Arrays.asList(bundleSummaryOutDTO);
        when(courseBundleService.getRecentBundleSummaries()).thenReturn(bundleSummaries);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/recent")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Recent bundles retrieved successfully."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bundleId").value(1L))
                .andExpect(jsonPath("$.data[0].courseCount").value(3L));
    }

    @Test
    void getRecentBundles_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/recent")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // FIND COURSE IDS BY BUNDLE ID TESTS
    @Test
    void findCourseIdsByBundleId_ShouldReturnCourseIds_WhenCoursesExist() throws Exception {
        // Given
        List<Long> courseIds = Arrays.asList(1L, 2L, 3L);
        when(courseBundleService.findCourseIdsByBundleId(1L)).thenReturn(courseIds);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/bundle-id/1/course-ids")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(1L))
                .andExpect(jsonPath("$[1]").value(2L))
                .andExpect(jsonPath("$[2]").value(3L));
    }

    @Test
    void findCourseIdsByBundleId_ShouldReturnCourseIds_WhenEmployeeRole() throws Exception {
        // Given
        List<Long> courseIds = Arrays.asList(1L, 2L);
        when(courseBundleService.findCourseIdsByBundleId(1L)).thenReturn(courseIds);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/bundle-id/1/course-ids")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void findCourseIdsByBundleId_ShouldReturnInternalServerError_WhenServerError() throws Exception {
        // Given
        when(courseBundleService.findCourseIdsByBundleId(999L))
                .thenThrow(new RuntimeException("SERVER ERROR"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-bundles/bundle-id/999/course-ids")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isInternalServerError());
    }
}