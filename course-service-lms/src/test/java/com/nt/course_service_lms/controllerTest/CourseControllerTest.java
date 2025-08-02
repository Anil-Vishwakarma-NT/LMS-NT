package com.nt.course_service_lms.controllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.ServiceAuthenticationFilter;
import com.nt.course_service_lms.config.TestAuthenticationFilter;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private TestAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter to do nothing but continue the chain
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(serviceAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

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
                .Active(true)
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
    @WithMockUser(roles = "ADMIN")
    void createCourse_ValidInput_ShouldReturnCreatedCourse() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        CourseOutDTO courseOutDTO = buildCourseOutDTO();
        when(courseService.createCourse(any(CourseInDTO.class))).thenReturn(courseOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Course Created Successfully")))
                .andExpect(jsonPath("$.data.courseId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Java Programming")))
                .andExpect(jsonPath("$.data.ownerId", is(1)))
                .andExpect(jsonPath("$.data.description", is("Complete Java programming course")))
                .andExpect(jsonPath("$.data.level", is("BEGINNER")))
                .andExpect(jsonPath("$.data.active", is(true)));

        verify(courseService).createCourse(any(CourseInDTO.class));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createCourse_UserRole_ShouldReturnForbidden() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_InvalidInput_BlankTitle_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setTitle("");

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_InvalidInput_NullOwnerId_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setOwnerId(null);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_InvalidInput_ShortDescription_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setDescription("AB"); // Less than 3 characters

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // GET ALL COURSES TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCourses_ShouldReturnListOfCourses() throws Exception {
        // Given
        List<CourseOutDTO> courses = Arrays.asList(
                buildCourseOutDTO(),
                CourseOutDTO.builder()
                        .courseId(2L)
                        .title("Python Programming")
                        .ownerId(2L)
                        .description("Complete Python programming course")
                        .level("INTERMEDIATE")
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        when(courseService.getAllCourses()).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/service-api/course"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Courses Successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].courseId", is(1)))
                .andExpect(jsonPath("$.data[0].title", is("Java Programming")))
                .andExpect(jsonPath("$.data[1].courseId", is(2)))
                .andExpect(jsonPath("$.data[1].title", is("Python Programming")));

        verify(courseService).getAllCourses();
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllCourses_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET COURSE BY ID TESTS - No security restriction in controller
    @Test
    @WithMockUser
    void getCourseById_ValidId_ShouldReturnCourse() throws Exception {
        // Given
        Long courseId = 1L;
        CourseInfoOutDTO courseInfo = buildCourseInfoOutDTO();
        when(courseService.getCourseById(courseId)).thenReturn(courseInfo);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}", courseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Course Details")))
                .andExpect(jsonPath("$.data.courseId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Java Programming")))
                .andExpect(jsonPath("$.data.ownerId", is(1)))
                .andExpect(jsonPath("$.data.description", is("Complete Java programming course")))
                .andExpect(jsonPath("$.data.courseLevel", is("BEGINNER")))
                .andExpect(jsonPath("$.data.active", is(true)));

        verify(courseService).getCourseById(courseId);
    }

    // DELETE COURSE TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_ValidId_ShouldReturnSuccessMessage() throws Exception {
        // Given
        Long courseId = 1L;
        String successMessage = "Course deleted successfully";
        when(courseService.deleteCourse(courseId)).thenReturn(successMessage);

        // When & Then
        mockMvc.perform(delete("/api/service-api/course/{id}", courseId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is(successMessage)))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(courseService).deleteCourse(courseId);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteCourse_UserRole_ShouldReturnForbidden() throws Exception {
        // Given
        Long courseId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/service-api/course/{id}", courseId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // UPDATE COURSE TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_ValidInput_ShouldReturnUpdatedCourse() throws Exception {
        // Given
        Long courseId = 1L;
        UpdateCourseInDTO updateDTO = buildUpdateCourseInDTO();
        CourseOutDTO updatedCourse = CourseOutDTO.builder()
                .courseId(courseId)
                .title("Advanced Java Programming")
                .ownerId(1L)
                .description("Advanced Java programming course")
                .level("ADVANCED")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseService.updateCourse(eq(courseId), any(UpdateCourseInDTO.class)))
                .thenReturn(updatedCourse);

        // When & Then
        mockMvc.perform(put("/api/service-api/course/{id}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Course Updated Successfully")))
                .andExpect(jsonPath("$.data.courseId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Advanced Java Programming")))
                .andExpect(jsonPath("$.data.level", is("ADVANCED")));

        verify(courseService).updateCourse(eq(courseId), any(UpdateCourseInDTO.class));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateCourse_UserRole_ShouldReturnForbidden() throws Exception {
        // Given
        Long courseId = 1L;
        UpdateCourseInDTO updateDTO = buildUpdateCourseInDTO();

        // When & Then
        mockMvc.perform(put("/api/service-api/course/{id}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_InvalidInput_BlankTitle_ShouldReturnBadRequest() throws Exception {
        // Given
        Long courseId = 1L;
        UpdateCourseInDTO updateDTO = buildUpdateCourseInDTO();
        updateDTO.setTitle("");

        // When & Then
        mockMvc.perform(put("/api/service-api/course/{id}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // CHECK IF COURSE EXISTS TESTS - No security restriction
    @Test
    @WithMockUser
    void checkIfCourseExists_ExistingCourse_ShouldReturnTrue() throws Exception {
        // Given
        Long courseId = 1L;
        when(courseService.courseExistsById(courseId)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}/exists", courseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(courseService).courseExistsById(courseId);
    }

    @Test
    @WithMockUser
    void checkIfCourseExists_NonExistingCourse_ShouldReturnFalse() throws Exception {
        // Given
        Long courseId = 999L;
        when(courseService.courseExistsById(courseId)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}/exists", courseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(courseService).courseExistsById(courseId);
    }

    // GET COURSE COUNT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseCount_ShouldReturnCount() throws Exception {
        // Given
        long courseCount = 25L;
        when(courseService.countCourses()).thenReturn(courseCount);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Course Count")))
                .andExpect(jsonPath("$.data", is(25)));

        verify(courseService).countCourses();
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCourseCount_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/count"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET RECENT COURSES TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getRecentCourses_ShouldReturnRecentCourseSummaries() throws Exception {
        // Given
        List<CourseSummaryOutDTO> recentCourses = Arrays.asList(
                buildCourseSummaryOutDTO(),
                CourseSummaryOutDTO.builder()
                        .title("Python Programming")
                        .description("Complete Python course")
                        .level("INTERMEDIATE")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        when(courseService.getRecentCourseSummaries()).thenReturn(recentCourses);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Recent Courses")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title", is("Java Programming")))
                .andExpect(jsonPath("$.data[1].title", is("Python Programming")));

        verify(courseService).getRecentCourseSummaries();
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getRecentCourses_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET RECENT DASHBOARD DATA TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getRecentDashboardData_ShouldReturnDashboardData() throws Exception {
        // Given
        DashboardDataOutDTO dashboardData = DashboardDataOutDTO.builder()
                .recentCourses(Arrays.asList(buildCourseSummaryOutDTO()))
                .recentBundles(Arrays.asList(
                        BundleSummaryOutDTO.builder()
                                .bundleId(1L)
                                .bundleName("Java Bundle")
                                .courseCount(3L)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ))
                .build();

        when(courseService.getRecentDashboardData()).thenReturn(dashboardData);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent-course-and-bundle"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recentCourses", hasSize(1)))
                .andExpect(jsonPath("$.recentBundles", hasSize(1)))
                .andExpect(jsonPath("$.recentCourses[0].title", is("Java Programming")))
                .andExpect(jsonPath("$.recentBundles[0].bundleName", is("Java Bundle")));

        verify(courseService).getRecentDashboardData();
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getRecentDashboardData_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent-course-and-bundle"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET COURSE NAME BY ID TESTS - No security restriction
    @Test
    @WithMockUser
    void getCourseNameById_ValidId_ShouldReturnCourseName() throws Exception {
        // Given
        Long courseId = 1L;
        String courseName = "Java Programming";
        when(courseService.getCourseNameById(courseId)).thenReturn(courseName);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/{id}/name", courseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(courseName));

        verify(courseService).getCourseNameById(courseId);
    }

    // GET COURSES INFO TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCoursesInfo_ShouldReturnCoursesInfoList() throws Exception {
        // Given
        List<CourseInfoOutDTO> coursesInfo = Arrays.asList(
                buildCourseInfoOutDTO(),
                CourseInfoOutDTO.builder()
                        .courseId(2L)
                        .title("Python Programming")
                        .ownerId(2L)
                        .description("Complete Python course")
                        .courseLevel("INTERMEDIATE")
                        .isActive(true)
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        when(courseService.getCoursesInfo()).thenReturn(coursesInfo);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Fetched Course Information")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].courseId", is(1)))
                .andExpect(jsonPath("$.data[0].title", is("Java Programming")))
                .andExpect(jsonPath("$.data[1].courseId", is(2)))
                .andExpect(jsonPath("$.data[1].title", is("Python Programming")));

        verify(courseService).getCoursesInfo();
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCoursesInfo_UserRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/info"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // GET EXISTING COURSE IDS TESTS - No security restriction
    @Test
    @WithMockUser
    void getExistingCourseIds_ValidIds_ShouldReturnExistingIds() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList(1L, 2L, 3L, 999L);
        List<Long> existingIds = Arrays.asList(1L, 2L, 3L);
        when(courseService.findExistingIds(inputIds)).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is(1)))
                .andExpect(jsonPath("$[1]", is(2)))
                .andExpect(jsonPath("$[2]", is(3)));

        verify(courseService).findExistingIds(inputIds);
    }

    @Test
    @WithMockUser
    void getExistingCourseIds_EmptyList_ShouldReturnEmptyList() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList();
        List<Long> existingIds = Arrays.asList();
        when(courseService.findExistingIds(inputIds)).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseService).findExistingIds(inputIds);
    }

    // EDGE CASE TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_NegativeOwnerId_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setOwnerId(-1L);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_NullCourseLevel_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseInDTO courseInDTO = buildCourseInDTO();
        courseInDTO.setCourseLevel(null);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_EmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}