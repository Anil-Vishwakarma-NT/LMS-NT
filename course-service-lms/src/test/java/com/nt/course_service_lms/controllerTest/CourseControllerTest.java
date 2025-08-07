package com.nt.course_service_lms.controllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.CourseController;
import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.DashboardDataOutDTO;
import com.nt.course_service_lms.service.CourseService;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for CourseController using MockMvc with Spring Security integration.
 * Tests all REST endpoints for course management operations with proper security context.
 */
@WebMvcTest(CourseController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper for LocalDateTime serialization
        objectMapper.registerModule(new JavaTimeModule());
    }

    // Test Data Builders
    private CourseInDTO buildCourseInDTO() {
        return CourseInDTO.builder()
                .title("Java Programming")
                .ownerId(1L)
                .description("Complete Java programming course")
                .courseLevel("BEGINNER")
                .isActive(true)
                .build();
    }

    private UpdateCourseInDTO buildUpdateCourseInDTO() {
        return UpdateCourseInDTO.builder()
                .title("Advanced Java Programming")
                .ownerId(1L)
                .description("Advanced Java programming course")
                .courseLevel("ADVANCED")
                .Active(true)
                .build();
    }

    private CourseOutDTO buildCourseOutDTO() {
        return CourseOutDTO.builder()
                .courseId(1L)
                .title("Java Programming")
                .ownerId(1L)
                .description("Complete Java programming course")
                .level("BEGINNER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CourseInfoOutDTO buildCourseInfoOutDTO() {
        return CourseInfoOutDTO.builder()
                .courseId(1L)
                .title("Java Programming")
                .ownerId(1L)
                .description("Complete Java programming course")
                .courseLevel("BEGINNER")
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CourseSummaryOutDTO buildCourseSummaryOutDTO() {
        return CourseSummaryOutDTO.builder()
                .title("Java Programming")
                .description("Complete Java programming course")
                .level("BEGINNER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // CREATE COURSE TESTS
    @Test
    void createCourse_ValidInput_ShouldReturnCreatedCourse() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        CourseOutDTO courseOutDTO = buildCourseOutDTO();
        when(courseService.createCourse(any(CourseInDTO.class))).thenReturn(courseOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Course Created Successfully")))
                .andExpect(jsonPath("$.data.courseId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Java Programming")));

        verify(courseService).createCourse(any(CourseInDTO.class));
    }

    @Test
    void createCourse_UserRole_ShouldReturnForbidden() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createCourse_InvalidInput_BlankTitle_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setTitle("");

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // GET ALL COURSES TESTS
    @Test
    void getAllCourses_ShouldReturnListOfCourses() throws Exception {
        // Given
        List<CourseOutDTO> courses = Arrays.asList(buildCourseOutDTO());
        when(courseService.getAllCourses()).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Courses Successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", is("Java Programming")));

        verify(courseService).getAllCourses();
    }

    @Test
    void getAllCourses_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET COURSE BY ID TESTS
    @Test
    void getCourseById_ValidId_ShouldReturnCourse() throws Exception {
        // Given
        Long courseId = 1L;
        CourseInfoOutDTO courseInfo = buildCourseInfoOutDTO();
        when(courseService.getCourseById(courseId)).thenReturn(courseInfo);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}", courseId)
                        .header("X-Test-Role", "ADMIN")) // Assuming any authenticated user can access
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Course Details")))
                .andExpect(jsonPath("$.data.courseId", is(1)));

        verify(courseService).getCourseById(courseId);
    }

    // DELETE COURSE TESTS
    @Test
    void deleteCourse_ValidId_ShouldReturnSuccessMessage() throws Exception {
        // Given
        Long courseId = 1L;
        String successMessage = "Course deleted successfully";
        when(courseService.deleteCourse(courseId)).thenReturn(successMessage);

        // When & Then
        mockMvc.perform(delete("/api/service-api/course/{id}", courseId)
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is(successMessage)));

        verify(courseService).deleteCourse(courseId);
    }

    @Test
    void deleteCourse_UserRole_ShouldReturnForbidden() throws Exception {
        // Given
        Long courseId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/service-api/course/{id}", courseId)
                        .header("X-Test-Role", "EMPLOYEE"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // UPDATE COURSE TESTS
    @Test
    void updateCourse_ValidInput_ShouldReturnUpdatedCourse() throws Exception {
        // Given
        Long courseId = 1L;
        UpdateCourseInDTO updateDTO = buildUpdateCourseInDTO();
        CourseOutDTO updatedCourse = buildCourseOutDTO();
        updatedCourse.setTitle("Advanced Java Programming");
        updatedCourse.setLevel("ADVANCED");

        when(courseService.updateCourse(eq(courseId), any(UpdateCourseInDTO.class))).thenReturn(updatedCourse);

        // When & Then
        mockMvc.perform(put("/api/service-api/course/{id}", courseId)
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Course Updated Successfully")))
                .andExpect(jsonPath("$.data.title", is("Advanced Java Programming")));

        verify(courseService).updateCourse(eq(courseId), any(UpdateCourseInDTO.class));
    }

    @Test
    void updateCourse_UserRole_ShouldReturnForbidden() throws Exception {
        // Given
        Long courseId = 1L;
        UpdateCourseInDTO updateDTO = buildUpdateCourseInDTO();

        // When & Then
        mockMvc.perform(put("/api/service-api/course/{id}", courseId)
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // CHECK IF COURSE EXISTS TESTS
    @Test
    void checkIfCourseExists_ExistingCourse_ShouldReturnTrue() throws Exception {
        // Given
        Long courseId = 1L;
        when(courseService.courseExistsById(courseId)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}/exists", courseId)
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(courseService).courseExistsById(courseId);
    }

    // GET COURSE COUNT TESTS
    @Test
    void getCourseCount_ShouldReturnCount() throws Exception {
        // Given
        long courseCount = 25L;
        when(courseService.countCourses()).thenReturn(courseCount);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/count")
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Course Count")))
                .andExpect(jsonPath("$.data", is(25)));

        verify(courseService).countCourses();
    }

    @Test
    void getCourseCount_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/count")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET RECENT COURSES TESTS
    @Test
    void getRecentCourses_ShouldReturnRecentCourseSummaries() throws Exception {
        // Given
        List<CourseSummaryOutDTO> recentCourses = Arrays.asList(buildCourseSummaryOutDTO());
        when(courseService.getRecentCourseSummaries()).thenReturn(recentCourses);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent")
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Recent Courses")))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(courseService).getRecentCourseSummaries();
    }

    // GET RECENT DASHBOARD DATA TESTS
    @Test
    void getRecentDashboardData_ShouldReturnDashboardData() throws Exception {
        // Given
        DashboardDataOutDTO dashboardData = DashboardDataOutDTO.builder()
                .recentCourses(Collections.singletonList(buildCourseSummaryOutDTO()))
                .recentBundles(Collections.singletonList(BundleSummaryOutDTO.builder().bundleName("Java Bundle").build()))
                .build();
        when(courseService.getRecentDashboardData()).thenReturn(dashboardData);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent-course-and-bundle")
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recentCourses", hasSize(1)))
                .andExpect(jsonPath("$.recentBundles", hasSize(1)));

        verify(courseService).getRecentDashboardData();
    }

    // GET COURSE NAME BY ID TESTS
    @Test
    void getCourseNameById_ValidId_ShouldReturnCourseName() throws Exception {
        // Given
        Long courseId = 1L;
        String courseName = "Java Programming";
        when(courseService.getCourseNameById(courseId)).thenReturn(courseName);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}/name", courseId)
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(courseName));

        verify(courseService).getCourseNameById(courseId);
    }

    // GET COURSES INFO TESTS
    @Test
    void getCoursesInfo_ShouldReturnCoursesInfoList() throws Exception {
        // Given
        List<CourseInfoOutDTO> coursesInfo = Collections.singletonList(buildCourseInfoOutDTO());
        when(courseService.getCoursesInfo()).thenReturn(coursesInfo);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/info")
                        .header("X-Test-Role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Course Information")))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(courseService).getCoursesInfo();
    }

    // GET EXISTING COURSE IDS TESTS
    @Test
    void getExistingCourseIds_ValidIds_ShouldReturnExistingIds() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList(1L, 2L, 999L);
        List<Long> existingIds = Arrays.asList(1L, 2L);
        when(courseService.findExistingIds(inputIds)).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is(1)))
                .andExpect(jsonPath("$[1]", is(2)));

        verify(courseService).findExistingIds(inputIds);
    }

    // EDGE CASE TESTS
    @Test
    void createCourse_NegativeOwnerId_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setOwnerId(-1L);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_EmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}