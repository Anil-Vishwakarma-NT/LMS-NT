package com.nt.course_service_lms.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.CourseController;
import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.DashboardDataOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private CourseInDTO courseInDTO;
    private UpdateCourseInDTO updateCourseInDTO;
    private CourseOutDTO courseOutDTO;
    private CourseInfoOutDTO courseInfoOutDTO;
    private CourseSummaryOutDTO courseSummaryOutDTO;
    private DashboardDataOutDTO dashboardDataOutDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        courseInDTO = CourseInDTO.builder()
                .title("Test Course")
                .ownerId(1L)
                .description("Test course description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        updateCourseInDTO = UpdateCourseInDTO.builder()
                .title("Updated Course")
                .ownerId(1L)
                .description("Updated course description")
                .courseLevel("INTERMEDIATE")
                .Active(false)
                .build();

        courseOutDTO = CourseOutDTO.builder()
                .courseId(1L)
                .ownerId(1L)
                .title("Test Course")
                .description("Test course description")
                .level("BEGINNER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        courseInfoOutDTO = CourseInfoOutDTO.builder()
                .courseId(1L)
                .ownerId(1L)
                .title("Test Course")
                .description("Test course description")
                .courseLevel("BEGINNER")
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .build();

        courseSummaryOutDTO = CourseSummaryOutDTO.builder()
                .courseId(1L)
                .title("Test Course")
                .description("Test course description")
                .level("BEGINNER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        dashboardDataOutDTO = DashboardDataOutDTO.builder()
                .recentCourses(Arrays.asList(courseSummaryOutDTO))
                .recentBundles(Arrays.asList())
                .build();
    }

    @Test
    void createCourse_ShouldReturnCreatedCourse_WhenValidInput() throws Exception {
        // Given
        when(courseService.createCourse(any(CourseInDTO.class))).thenReturn(courseOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Created Successfully"))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course"))
                .andExpect(jsonPath("$.data.ownerId").value(1L))
                .andExpect(jsonPath("$.data.description").value("Test course description"))
                .andExpect(jsonPath("$.data.level").value("BEGINNER"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void createCourse_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid course with empty title
        CourseInDTO invalidCourse = CourseInDTO.builder()
                .title("")
                .ownerId(1L)
                .description("Test description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllCourses_ShouldReturnListOfCourses_WhenCoursesExist() throws Exception {
        // Given
        List<CourseOutDTO> courses = Arrays.asList(courseOutDTO);
        when(courseService.getAllCourses()).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Courses Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Test Course"));
    }

    @Test
    void getAllCourses_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCourseById_ShouldReturnCourse_WhenCourseExists() throws Exception {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(courseInfoOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Course Details"))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course"))
                .andExpect(jsonPath("$.data.ownerId").value(1L))
                .andExpect(jsonPath("$.data.description").value("Test course description"))
                .andExpect(jsonPath("$.data.courseLevel").value("BEGINNER"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void getCourseById_ShouldReturnNotFound_WhenCourseDoesNotExist() throws Exception {
        // Given
        when(courseService.getCourseById(999L)).thenThrow(new ResourceNotFoundException("Course not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourse_ShouldReturnSuccess_WhenCourseExists() throws Exception {
        // Given
        when(courseService.deleteCourse(1L)).thenReturn("Course deleted successfully");

        // When & Then
        mockMvc.perform(delete("/api/service-api/course/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteCourse_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/course/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCourse_ShouldReturnUpdatedCourse_WhenValidInput() throws Exception {
        // Given
        CourseOutDTO updatedCourse = CourseOutDTO.builder()
                .courseId(1L)
                .ownerId(1L)
                .title("Updated Course")
                .description("Updated course description")
                .level("INTERMEDIATE")
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseService.updateCourse(anyLong(), any(UpdateCourseInDTO.class))).thenReturn(updatedCourse);

        // When & Then
        mockMvc.perform(put("/api/service-api/course/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Updated Successfully"))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Updated Course"))
                .andExpect(jsonPath("$.data.description").value("Updated course description"))
                .andExpect(jsonPath("$.data.level").value("INTERMEDIATE"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    void updateCourse_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with empty title
        UpdateCourseInDTO invalidUpdate = UpdateCourseInDTO.builder()
                .title("")
                .ownerId(1L)
                .description("Updated description")
                .courseLevel("INTERMEDIATE")
                .Active(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourse_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/course/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkIfCourseExists_ShouldReturnTrue_WhenCourseExists() throws Exception {
        // Given
        when(courseService.courseExistsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkIfCourseExists_ShouldReturnFalse_WhenCourseDoesNotExist() throws Exception {
        // Given
        when(courseService.courseExistsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/999/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getCourseCount_ShouldReturnCount_WhenCalled() throws Exception {
        // Given
        when(courseService.countCourses()).thenReturn(10L);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/count")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Course Count"))
                .andExpect(jsonPath("$.data").value(10L));
    }

    @Test
    void getCourseCount_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/count")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRecentCourses_ShouldReturnRecentCourses_WhenCoursesExist() throws Exception {
        // Given
        List<CourseSummaryOutDTO> recentCourses = Arrays.asList(courseSummaryOutDTO);
        when(courseService.getRecentCourseSummaries()).thenReturn(recentCourses);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Recent Courses"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Test Course"));
    }

    @Test
    void getRecentCourses_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRecentDashboardData_ShouldReturnDashboardData_WhenCalled() throws Exception {
        // Given
        when(courseService.getRecentDashboardData()).thenReturn(dashboardDataOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent-course-and-bundle")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recentCourses").isArray())
                .andExpect(jsonPath("$.recentCourses[0].courseId").value(1L))
                .andExpect(jsonPath("$.recentBundles").isArray());
    }

    @Test
    void getRecentDashboardData_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/recent-course-and-bundle")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCourseNameById_ShouldReturnCourseName_WhenCourseExists() throws Exception {
        // Given
        when(courseService.getCourseNameById(1L)).thenReturn("Test Course");

        // When & Then
        mockMvc.perform(get("/api/service-api/course/1/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test Course"));
    }

    @Test
    void getCourseNameById_ShouldReturnNotFound_WhenCourseDoesNotExist() throws Exception {
        // Given
        when(courseService.getCourseNameById(999L)).thenThrow(new ResourceNotFoundException("Course not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course/999/name"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCoursesInfo_ShouldReturnCoursesInfo_WhenCoursesExist() throws Exception {
        // Given
        List<CourseInfoOutDTO> coursesInfo = Arrays.asList(courseInfoOutDTO);
        when(courseService.getCoursesInfo()).thenReturn(coursesInfo);

        // When & Then
        mockMvc.perform(get("/api/service-api/course/info")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Course Information"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Test Course"));
    }

    @Test
    void getCoursesInfo_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course/info")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getExistingCourseIds_ShouldReturnExistingIds_WhenIdsProvided() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList(1L, 2L, 3L, 999L);
        List<Long> existingIds = Arrays.asList(1L, 2L, 3L);
        when(courseService.findExistingIds(anyList())).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(1L))
                .andExpect(jsonPath("$[1]").value(2L))
                .andExpect(jsonPath("$[2]").value(3L));
    }

    @Test
    void getExistingCourseIds_ShouldReturnEmptyList_WhenNoIdsExist() throws Exception {
        // Given
        List<Long> inputIds = Arrays.asList(999L, 998L);
        List<Long> existingIds = Arrays.asList();
        when(courseService.findExistingIds(anyList())).thenReturn(existingIds);

        // When & Then
        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getExistingCourseIds_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course/existing-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCoursesByIds_ShouldReturnCourses_WhenIdsProvided() throws Exception {
        // Given
        List<Long> courseIds = Arrays.asList(1L, 2L);
        List<CourseInfoOutDTO> courses = Arrays.asList(courseInfoOutDTO);
        StandardResponseOutDTO<List<CourseInfoOutDTO>> response = StandardResponseOutDTO.success(courses, "courses retrieved");
        when(courseService.getCoursesByIds(anyList())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/service-api/course/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("courses retrieved"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L));
    }

    @Test
    void getCoursesByIds_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_ShouldReturnBadRequest_WhenTitleTooShort() throws Exception {
        // Given - Course with title too short (less than 3 characters)
        CourseInDTO invalidCourse = CourseInDTO.builder()
                .title("AB")
                .ownerId(1L)
                .description("Test description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_ShouldReturnBadRequest_WhenOwnerIdInvalid() throws Exception {
        // Given - Course with negative owner ID
        CourseInDTO invalidCourse = CourseInDTO.builder()
                .title("Test Course")
                .ownerId(-1L)
                .description("Test description")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_ShouldReturnBadRequest_WhenDescriptionTooShort() throws Exception {
        // Given - Course with description too short
        CourseInDTO invalidCourse = CourseInDTO.builder()
                .title("Test Course")
                .ownerId(1L)
                .description("AB")
                .courseLevel("BEGINNER")
                .Active(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_ShouldReturnBadRequest_WhenCourseLevelNull() throws Exception {
        // Given - Course with null course level
        CourseInDTO invalidCourse = CourseInDTO.builder()
                .title("Test Course")
                .ownerId(1L)
                .description("Test description")
                .courseLevel(null)
                .Active(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }
}