package com.nt.user_service_lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dto.outDTO.CourseDeadlinesDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ServicePrincipal servicePrincipal;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserIdByEmail_Success() throws Exception {
        // Given
        String email = "test@example.com";
        UserOutDTO userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(1L);
        userOutDTO.setFirstName("John");
        userOutDTO.setLastName("Doe");

        StandardResponseOutDTO<UserOutDTO> response = StandardResponseOutDTO.success(userOutDTO, "User found");

        when(userService.getUserDetailsByEmail(email)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getUserId")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));

        verify(userService).getUserDetailsByEmail(email);
    }

    @Test
    void getUserIdByEmail_EmptyEmail() throws Exception {
        // Given
        String email = "";
        StandardResponseOutDTO<UserOutDTO> response = StandardResponseOutDTO.error("User not found");

        when(userService.getUserDetailsByEmail(email)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getUserId")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService).getUserDetailsByEmail(email);
    }

    @Test
    void getUserIdByAuth_Success() throws Exception {
        // Given
        String username = "test@example.com";
        UserOutDTO userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(1L);
        userOutDTO.setFirstName("Jane");
        userOutDTO.setLastName("Smith");

        StandardResponseOutDTO<UserOutDTO> response = StandardResponseOutDTO.success(userOutDTO, "User found");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userService.getUserDetailsByEmail(username)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getUserDetails")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"));

        verify(userService).getUserDetailsByEmail(username);
    }

    @Test
    void getUserNameById_Success() throws Exception {
        // Given
        Long userId = 1L;
        UserOutDTO userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(userId);
        userOutDTO.setFirstName("Alice");
        userOutDTO.setLastName("Johnson");

        StandardResponseOutDTO<UserOutDTO> response = StandardResponseOutDTO.success(userOutDTO, "User found");

        when(userService.getUserDetailsByUserId(userId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.firstName").value("Alice"))
                .andExpect(jsonPath("$.data.lastName").value("Johnson"));

        verify(userService).getUserDetailsByUserId(userId);
    }

    @Test
    void getUserNameById_UserNotFound() throws Exception {
        // Given
        Long userId = 999L;
        StandardResponseOutDTO<UserOutDTO> response = StandardResponseOutDTO.success(null, "User not found");

        when(userService.getUserDetailsByUserId(userId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService).getUserDetailsByUserId(userId);
    }

    @Test
    void getUserDeadlines_Success() throws Exception {
        // Given
        String userEmail = "test@example.com";
        CourseDeadlinesDTO courseDeadline = new CourseDeadlinesDTO();
        courseDeadline.setCourseId(1L);
        courseDeadline.setTitle("Java Programming");
        courseDeadline.setOwnerId(1L);
        courseDeadline.setDeadline(LocalDateTime.now().plusDays(3));

        List<CourseDeadlinesDTO> deadlines = Arrays.asList(courseDeadline);
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response =
                StandardResponseOutDTO.success(deadlines, "Deadlines found");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(servicePrincipal);
        when(servicePrincipal.getUserEmail()).thenReturn(userEmail);
        when(userService.deadlineCourses(userEmail)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getDeadlines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Java Programming"));

        verify(userService).deadlineCourses(userEmail);
    }

    @Test
    void getUserDeadlines_UnauthorizedAccess() throws Exception {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not-service-principal");

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getDeadlines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEnrolledCoursesByUserId_Success() throws Exception {
        // Given
        String userId = "1";
        UserCourseEnrollDetails enrollDetail = new UserCourseEnrollDetails();
        enrollDetail.setCourseId(1L);
        enrollDetail.setAssignedById(2L);
        enrollDetail.setEnrollmentDate(LocalDateTime.now().minusDays(10));
        enrollDetail.setDeadline(LocalDateTime.now().plusDays(5));

        List<UserCourseEnrollDetails> enrolledCourses = Arrays.asList(enrollDetail);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(servicePrincipal);
        when(servicePrincipal.getUserId()).thenReturn(userId);
        when(userService.getUserEnrolledCourses(Long.parseLong(userId))).thenReturn(enrolledCourses);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/userCourses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L))
                .andExpect(jsonPath("$.data[0].assignedById").value(2L))
                .andExpect(jsonPath("$.message").value("Fetched enrolled courses successfully"));

        verify(userService).getUserEnrolledCourses(1L);
    }

    @Test
    void getEnrolledCoursesByUserId_UnauthorizedAccess() throws Exception {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not-service-principal");

        // When & Then
        mockMvc.perform(get("/api/service-api/users/userCourses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserEnrollments_Success() throws Exception {
        // Given
        Long userId = 1L;
        Map<String, Long> stats = new HashMap<>();
        stats.put("enrollments", 5L);
        stats.put("groups", 3L);

        when(userService.userStatistics(userId)).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/{userId}/statistics", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enrollments").value(5L))
                .andExpect(jsonPath("$.data.groups").value(3L))
                .andExpect(jsonPath("$.message").value("Fetched Users Enrolled"));

        verify(userService).userStatistics(userId);
    }

    @Test
    void getUserEnrollments_EmptyStats() throws Exception {
        // Given
        Long userId = 999L;
        Map<String, Long> emptyStats = new HashMap<>();
        emptyStats.put("enrollments", 0L);
        emptyStats.put("groups", 0L);

        when(userService.userStatistics(userId)).thenReturn(emptyStats);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/{userId}/statistics", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enrollments").value(0L))
                .andExpect(jsonPath("$.data.groups").value(0L));

        verify(userService).userStatistics(userId);
    }

    @Test
    void getUserDeadlines_EmptyDeadlines() throws Exception {
        // Given
        String userEmail = "test@example.com";
        List<CourseDeadlinesDTO> emptyDeadlines = Arrays.asList();
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response =
                StandardResponseOutDTO.success(emptyDeadlines, "No deadlines found");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(servicePrincipal);
        when(servicePrincipal.getUserEmail()).thenReturn(userEmail);
        when(userService.deadlineCourses(userEmail)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getDeadlines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(userService).deadlineCourses(userEmail);
    }

    @Test
    void getEnrolledCoursesByUserId_EmptyEnrollments() throws Exception {
        // Given
        String userId = "1";
        List<UserCourseEnrollDetails> emptyEnrollments = Arrays.asList();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(servicePrincipal);
        when(servicePrincipal.getUserId()).thenReturn(userId);
        when(userService.getUserEnrolledCourses(Long.parseLong(userId))).thenReturn(emptyEnrollments);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/userCourses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Fetched enrolled courses successfully"));

        verify(userService).getUserEnrolledCourses(1L);
    }

    @Test
    void getUserIdByEmail_NullResponse() throws Exception {
        // Given
        String email = "nonexistent@example.com";
        StandardResponseOutDTO<UserOutDTO> response = StandardResponseOutDTO.success(null, "User not found");

        when(userService.getUserDetailsByEmail(email)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/service-api/users/getUserId")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService).getUserDetailsByEmail(email);
    }

    @Test
    void getUserIdByAuth_NullAuthentication() throws Exception {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // When & Then - This would likely cause a NullPointerException in real scenario
        // but we're testing the controller behavior
        mockMvc.perform(get("/api/service-api/users/getUserDetails")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}