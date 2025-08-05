package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestAuthenticationFilter;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.CourseContentController;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.CourseContentService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseContentController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class CourseContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseContentService courseContentService;

    @MockitoBean
    private TestAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private CourseContentInDTO courseContentInDTO;
    private UpdateCourseContentInDTO updateCourseContentInDTO;
    private CourseContentOutDTO courseContentOutDTO;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter to DO NOTHING but continue the chain
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(serviceAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        // Initialize test data
        courseContentInDTO = CourseContentInDTO.builder()
                .courseId(1L)
                .title("Test Course Content")
                .description("This is a test course content description")
                .resourceLink("https://example.com/resource")
                .isActive(true)
                .build();

        updateCourseContentInDTO = UpdateCourseContentInDTO.builder()
                .courseId(2L)
                .title("Updated Course Content")
                .description("This is an updated course content description")
                .resourceLink("https://updated-example.com/resource")
                .isActive(false)
                .build();

        courseContentOutDTO = CourseContentOutDTO.builder()
                .courseContentId(1L)
                .courseId(1L)
                .title("Test Course Content")
                .description("This is a test course content description")
                .resourceLink("https://example.com/resource")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // CREATE COURSE CONTENT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnCreatedCourseContent_WhenValidInput() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Created Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course Content"))
                .andExpect(jsonPath("$.data.description").value("This is a test course content description"))
                .andExpect(jsonPath("$.data.resourceLink").value("https://example.com/resource"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid course content with blank title
        CourseContentInDTO invalidCourseContent = CourseContentInDTO.builder()
                .courseId(1L)
                .title("")
                .description("This is a test description")
                .resourceLink("https://example.com/resource")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnConflict_WhenResourceAlreadyExists() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Course content already exists"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentInDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenCourseNotFound() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createCourseContent_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenInvalidResourceLink() throws Exception {
        // Given - Invalid resource link
        CourseContentInDTO invalidCourseContent = CourseContentInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .resourceLink("invalid-url")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenNegativeCourseId() throws Exception {
        // Given - Negative course ID
        CourseContentInDTO invalidCourseContent = CourseContentInDTO.builder()
                .courseId(-1L)
                .title("Test Title")
                .description("Test Description")
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    // GET ALL COURSE CONTENTS TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCourseContents_ShouldReturnListOfCourseContents_WhenCourseContentsExist() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContents()).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Contents Retrieved Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseContentId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Test Course Content"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllCourseContents_ShouldReturnListOfCourseContents_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContents()).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCourseContents_ShouldReturnNotFound_WhenNoCourseContentsExist() throws Exception {
        // Given
        when(courseContentService.getAllCourseContents())
                .thenThrow(new ResourceNotFoundException("No course contents found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCourseContents_ShouldReturnForbidden_WhenUserRole() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isForbidden());
    }

    // GET COURSE CONTENT BY ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentById_ShouldReturnCourseContent_WhenCourseContentExists() throws Exception {
        // Given
        when(courseContentService.getCourseContentById(1L)).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Retrieved Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course Content"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCourseContentById_ShouldReturnCourseContent_WhenEmployeeRole() throws Exception {
        // Given
        when(courseContentService.getCourseContentById(1L)).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentById_ShouldReturnNotFound_WhenCourseContentDoesNotExist() throws Exception {
        // Given
        when(courseContentService.getCourseContentById(999L))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/999"))
                .andExpect(status().isNotFound());
    }

    // GET COURSE CONTENT BY COURSE ID TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentByCourseId_ShouldReturnCourseContents_WhenCourseContentExists() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Contents Retrieved Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCourseContentByCourseId_ShouldReturnCourseContents_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentByCourseId_ShouldReturnNotFound_WhenCourseNotFound() throws Exception {
        // Given
        when(courseContentService.getAllCourseContentByCourseId(999L))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentByCourseId_ShouldReturnNotFound_WhenNoCourseContentsFound() throws Exception {
        // Given
        when(courseContentService.getAllCourseContentByCourseId(1L))
                .thenThrow(new ResourceNotFoundException("No course contents found for course"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1"))
                .andExpect(status().isNotFound());
    }

    // DELETE COURSE CONTENT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourseContent_ShouldReturnSuccess_WhenCourseContentExists() throws Exception {
        // Given
        when(courseContentService.deleteCourseContent(1L)).thenReturn("Course content deleted successfully");

        // When & Then
        mockMvc.perform(delete("/api/service-api/course-content/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course content deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteCourseContent_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/course-content/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourseContent_ShouldReturnNotFound_WhenCourseContentDoesNotExist() throws Exception {
        // Given
        when(courseContentService.deleteCourseContent(999L))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        // When & Then
        mockMvc.perform(delete("/api/service-api/course-content/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // UPDATE COURSE CONTENT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnUpdatedCourseContent_WhenValidInput() throws Exception {
        // Given
        CourseContentOutDTO updatedCourseContent = CourseContentOutDTO.builder()
                .courseContentId(1L)
                .courseId(2L)
                .title("Updated Course Content")
                .description("This is an updated course content description")
                .resourceLink("https://updated-example.com/resource")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseContentService.updateCourseContent(anyLong(), any(UpdateCourseContentInDTO.class)))
                .thenReturn(updatedCourseContent);

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Updated Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Updated Course Content"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with blank title
        UpdateCourseContentInDTO invalidUpdate = UpdateCourseContentInDTO.builder()
                .courseId(1L)
                .title("")
                .description("Updated description")
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateCourseContent_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnNotFound_WhenCourseContentDoesNotExist() throws Exception {
        // Given
        when(courseContentService.updateCourseContent(anyLong(), any(UpdateCourseContentInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnConflict_WhenDuplicateFound() throws Exception {
        // Given
        when(courseContentService.updateCourseContent(anyLong(), any(UpdateCourseContentInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Course content with same title already exists"));

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isConflict());
    }


    // GET COURSE CONTENT COUNT TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentCount_ShouldReturnCount_WhenCourseContentsExist() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO, courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Count Retrieved Successfully"))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCourseContentCount_ShouldReturnCount_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Collections.singletonList(courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseContentCount_ShouldReturnZero_WhenNoCourseContents() throws Exception {
        // Given
        when(courseContentService.getAllCourseContentByCourseId(1L))
                .thenThrow(new ResourceNotFoundException("No course contents found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCourseContentCount_ShouldReturnForbidden_WhenUserRole() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count"))
                .andExpect(status().isForbidden());
    }

    // EDGE CASES AND ERROR SCENARIOS
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenDescriptionTooLong() throws Exception {
        // Given - Description exceeding max length
        String longDescription = String.join("", Collections.nCopies(1001, "a")); // Assuming max is 1000
        CourseContentInDTO invalidCourseContent = CourseContentInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description(longDescription)
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnBadRequest_WhenTitleTooLong() throws Exception {
        // Given - Title exceeding max length
        String longTitle = String.join("", Collections.nCopies(101, "a"));
        ; // Assuming max is 100
        CourseContentInDTO invalidCourseContent = CourseContentInDTO.builder()
                .courseId(1L)
                .title(longTitle)
                .description("Test Description")
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCourseContents_ShouldReturnUnauthorized_WhenNoAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/course-content"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCourseContent_ShouldReturnUnauthorized_WhenNoAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentInDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnBadRequest_WhenInvalidResourceLink() throws Exception {
        // Given - Invalid resource link in update
        UpdateCourseContentInDTO invalidUpdate = UpdateCourseContentInDTO.builder()
                .courseId(1L)
                .title("Valid Title")
                .description("Valid Description")
                .resourceLink("invalid-url")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldAcceptEmptyResourceLink() throws Exception {
        // Given - Empty resource link should be valid according to regex pattern
        CourseContentInDTO validCourseContent = CourseContentInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .resourceLink("")
                .isActive(true)
                .build();

        when(courseContentService.createCourseContent(any(CourseContentInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCourseContent)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseContent_ShouldAcceptFtpResourceLink() throws Exception {
        // Given - FTP resource link should be valid
        CourseContentInDTO validCourseContent = CourseContentInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .resourceLink("ftp://example.com/resource")
                .isActive(true)
                .build();

        when(courseContentService.createCourseContent(any(CourseContentInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCourseContent)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnBadRequest_WhenNegativeCourseId() throws Exception {
        // Given - Negative course ID in update
        UpdateCourseContentInDTO invalidUpdate = UpdateCourseContentInDTO.builder()
                .courseId(-1L)
                .title("Valid Title")
                .description("Valid Description")
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseContent_ShouldReturnBadRequest_WhenBlankDescription() throws Exception {
        // Given - Blank description in update
        UpdateCourseContentInDTO invalidUpdate = UpdateCourseContentInDTO.builder()
                .courseId(1L)
                .title("Valid Title")
                .description("")
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }
}