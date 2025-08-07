package com.nt.user_service_lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.user_service_lms.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.exception.GlobalExceptionHandler;
import com.nt.user_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.exception.ResourceNotValidException;
import com.nt.user_service_lms.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void enroll_Success() throws Exception {
        // Given
        EnrollmentRequestInDTO requestDTO = new EnrollmentRequestInDTO();
        requestDTO.setUserIds(Arrays.asList(1L, 2L));
        requestDTO.setCourseIds(Arrays.asList(101L, 102L));
        requestDTO.setAssignedBy(1L);
        requestDTO.setDeadline(LocalDateTime.now().plusDays(30));
        requestDTO.setStatus("PENDING");

        EnrollmentOutDTO enrollmentOutDTO = new EnrollmentOutDTO();
        enrollmentOutDTO.setEnrollmentId(1L);
        enrollmentOutDTO.setUserId(1L);
        enrollmentOutDTO.setCourseId(101L);
        enrollmentOutDTO.setStatus("PENDING");

        List<EnrollmentOutDTO> enrollments = Arrays.asList(enrollmentOutDTO);

        when(enrollmentService.enroll(any(EnrollmentRequestInDTO.class))).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(post("/api/service-api/enrollment/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].enrollmentId").value(1L))
                .andExpect(jsonPath("$.data[0].userId").value(1L))
                .andExpect(jsonPath("$.data[0].courseId").value(101L))
                .andExpect(jsonPath("$.message").value("Enrollment Successful"));

        verify(enrollmentService).enroll(any(EnrollmentRequestInDTO.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void enroll_ResourceAlreadyExistsException() throws Exception {
        // Given
        EnrollmentRequestInDTO requestDTO = new EnrollmentRequestInDTO();
        requestDTO.setUserIds(Arrays.asList(1L));
        requestDTO.setCourseIds(Arrays.asList(101L));
        requestDTO.setAssignedBy(1L);
        requestDTO.setDeadline(LocalDateTime.of(2025, 8, 31, 10, 30)); // 31st Aug 2025, 10:30 AM


        when(enrollmentService.enroll(any(EnrollmentRequestInDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("User already enrolled"));

        // When & Then
        mockMvc.perform(post("/api/service-api/enrollment/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(enrollmentService).enroll(any(EnrollmentRequestInDTO.class));
    }

//    @Test
//    @WithMockUser(authorities = "ROLE_ADMIN")
//    void enroll_ValidationError() throws Exception {
//        // Given
//        EnrollmentRequestInDTO requestDTO = new EnrollmentRequestInDTO();
//        // Invalid request - no users or courses
//
//        when(enrollmentService.enroll(any(EnrollmentRequestInDTO.class)))
//                .thenThrow(new ResourceNotValidException("Enrollment failed: "));
//
//        // When & Then
//        mockMvc.perform(post("/api/service-api/enrollment/enroll")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isBadRequest());
//
//        verify(enrollmentService).enroll(any(EnrollmentRequestInDTO.class));
//    }



    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getEnrollmentStatistics_Success() throws Exception {
        // Given
        EnrollmentDashBoardStatsOutDTO stats = EnrollmentDashBoardStatsOutDTO.builder()
                .totalEnrollments(100L)
                .activeEnrollments(80L)
                .inactiveEnrollments(20L)
                .pendingEnrollments(15L)
                .inProgressEnrollments(35L)
                .completedEnrollments(30L)
                .expiredEnrollments(20L)
                .individualEnrollments(60L)
                .groupEnrollments(25L)
                .bundleEnrollments(10L)
                .groupBundleEnrollments(5L)
                .totalUniqueUsers(50L)
                .totalUniqueCourses(25L)
                .totalUniqueBundles(5L)
                .totalUniqueGroups(10L)
                .overallCompletionRate(new BigDecimal("75.50"))
                .individualCompletionRate(new BigDecimal("80.00"))
                .groupCompletionRate(new BigDecimal("70.00"))
                .bundleCompletionRate(new BigDecimal("65.00"))
                .recentEnrollments(20L)
                .recentCompletions(15L)
                .statusDistribution(new HashMap<>())
                .sourceDistribution(new HashMap<>())
                .build();

        when(enrollmentService.getEnrollmentStats()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEnrollments").value(100L))
                .andExpect(jsonPath("$.data.activeEnrollments").value(80L))
                .andExpect(jsonPath("$.data.overallCompletionRate").value(75.50))
                .andExpect(jsonPath("$.message").value("Fetched Enrollment Statistics"));

        verify(enrollmentService).getEnrollmentStats();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getEnrollmentStatistics_EmptyStats() throws Exception {
        // Given
        EnrollmentDashBoardStatsOutDTO emptyStats = EnrollmentDashBoardStatsOutDTO.builder()
                .totalEnrollments(0L)
                .activeEnrollments(0L)
                .inactiveEnrollments(0L)
                .overallCompletionRate(BigDecimal.ZERO)
                .statusDistribution(new HashMap<>())
                .sourceDistribution(new HashMap<>())
                .build();

        when(enrollmentService.getEnrollmentStats()).thenReturn(emptyStats);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEnrollments").value(0L))
                .andExpect(jsonPath("$.data.activeEnrollments").value(0L));

        verify(enrollmentService).getEnrollmentStats();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserEnrollmentsByUserId_Success() throws Exception {
        // Given
        Long userId = 1L;
        UserEnrollmentsOutDTO userEnrollments = new UserEnrollmentsOutDTO();
        userEnrollments.setUserId(userId);
        userEnrollments.setUserName("John Doe");
        userEnrollments.setCourseEnrollments(5L);
        userEnrollments.setBundleEnrollments(2L);
        userEnrollments.setTotalCourses(10L);
        userEnrollments.setAverageCompletion(75.5f);
        userEnrollments.setUpcomingDeadlines(3);
        userEnrollments.setStatus(true);

        when(enrollmentService.getUserEnrollmentsByUserID(userId)).thenReturn(userEnrollments);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-enrollments/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.userName").value("John Doe"))
                .andExpect(jsonPath("$.data.courseEnrollments").value(5L))
                .andExpect(jsonPath("$.data.bundleEnrollments").value(2L))
                .andExpect(jsonPath("$.message").value("User Enrollment Fetched"));

        verify(enrollmentService).getUserEnrollmentsByUserID(userId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserEnrollmentsByUserId_UserNotFound() throws Exception {
        // Given
        Long userId = 999L;
        when(enrollmentService.getUserEnrollmentsByUserID(userId))
                .thenThrow(new RuntimeException("User not found with id: " + userId));

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-enrollments/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(enrollmentService).getUserEnrollmentsByUserID(userId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserEnrollments_Success() throws Exception {
        // Given
        UserEnrollmentsOutDTO user1 = new UserEnrollmentsOutDTO();
        user1.setUserId(1L);
        user1.setUserName("John Doe");
        user1.setCourseEnrollments(3L);

        UserEnrollmentsOutDTO user2 = new UserEnrollmentsOutDTO();
        user2.setUserId(2L);
        user2.setUserName("Jane Smith");
        user2.setCourseEnrollments(2L);

        List<UserEnrollmentsOutDTO> userEnrollments = Arrays.asList(user1, user2);

        when(enrollmentService.getAllUsersEnrollments()).thenReturn(userEnrollments);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-enrollments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(1L))
                .andExpect(jsonPath("$.data[0].userName").value("John Doe"))
                .andExpect(jsonPath("$.data[1].userId").value(2L))
                .andExpect(jsonPath("$.data[1].userName").value("Jane Smith"))
                .andExpect(jsonPath("$.message").value("User Enrollment Fetched"));

        verify(enrollmentService).getAllUsersEnrollments();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserEnrollments_EmptyList() throws Exception {
        // Given
        when(enrollmentService.getAllUsersEnrollments()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-enrollments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(enrollmentService).getAllUsersEnrollments();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserCourseEnrollments_Success() throws Exception {
        // Given
        UserCourseEnrollmentOutDTO courseEnrollment = new UserCourseEnrollmentOutDTO();
        courseEnrollment.setCourseId(101L);
        courseEnrollment.setCourseName("Java Programming");
        courseEnrollment.setIndividualEnrollments(5L);
        courseEnrollment.setOwnerId(1L);
        courseEnrollment.setOwnerName("Teacher John");
        courseEnrollment.setActive(true);

        List<UserCourseEnrollmentOutDTO> courseEnrollments = Arrays.asList(courseEnrollment);

        when(enrollmentService.getIndividualCourseEnrollments()).thenReturn(courseEnrollments);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-course-enrollments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(101L))
                .andExpect(jsonPath("$.data[0].courseName").value("Java Programming"))
                .andExpect(jsonPath("$.data[0].individualEnrollments").value(5L))
                .andExpect(jsonPath("$.message").value("Fetched Course Enrollments for User"));

        verify(enrollmentService).getIndividualCourseEnrollments();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserBundleEnrollments_Success() throws Exception {
        // Given
        UserBundleEnrollmentOutDTO bundleEnrollment = new UserBundleEnrollmentOutDTO();
        bundleEnrollment.setBundleId(201L);
        bundleEnrollment.setBundleName("Programming Bundle");
        bundleEnrollment.setIndividualEnrollments(3L);
        bundleEnrollment.setTotalCourses(5L);
        bundleEnrollment.setAverageCompletion(68.5f);
        bundleEnrollment.setActive(true);

        List<UserBundleEnrollmentOutDTO> bundleEnrollments = Arrays.asList(bundleEnrollment);

        when(enrollmentService.getIndividualBundleEnrollments()).thenReturn(bundleEnrollments);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-bundle-enrollments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bundleId").value(201L))
                .andExpect(jsonPath("$.data[0].bundleName").value("Programming Bundle"))
                .andExpect(jsonPath("$.data[0].individualEnrollments").value(3L))
                .andExpect(jsonPath("$.data[0].totalCourses").value(5L))
                .andExpect(jsonPath("$.message").value("Fetched Bundle Enrollments for User"));

        verify(enrollmentService).getIndividualBundleEnrollments();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getEnrolledCoursesByUserId_Success() throws Exception {
        // Given
        Long userId = 1L;
        UserCourseEnrollDetails enrollDetail = new UserCourseEnrollDetails();
        enrollDetail.setCourseId(101L);
        enrollDetail.setAssignedById(2L);
        enrollDetail.setEnrollmentDate(LocalDateTime.now().minusDays(10));
        enrollDetail.setDeadline(LocalDateTime.now().plusDays(20));

        List<UserCourseEnrollDetails> enrolledCourses = Arrays.asList(enrollDetail);

        when(enrollmentService.getUserEnrolledCourses(userId)).thenReturn(enrolledCourses);

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/userCourses/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(101L))
                .andExpect(jsonPath("$.data[0].assignedById").value(2L))
                .andExpect(jsonPath("$.message").value("Fetched enrolled courses successfully"));

        verify(enrollmentService).getUserEnrolledCourses(userId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_EMPLOYEE")
    void getEnrolledCoursesByUserId_AsEmployee() throws Exception {
        // Given
        Long userId = 1L;
        UserCourseEnrollDetails enrollDetail = new UserCourseEnrollDetails();
        enrollDetail.setCourseId(101L);
        enrollDetail.setAssignedById(2L);

        List<UserCourseEnrollDetails> enrolledCourses = Arrays.asList(enrollDetail);

        when(enrollmentService.getUserEnrolledCourses(userId)).thenReturn(enrolledCourses);

        // When & Then - Should be accessible for EMPLOYEE role
        mockMvc.perform(get("/api/service-api/enrollment/userCourses/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(101L));

        verify(enrollmentService).getUserEnrolledCourses(userId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getEnrolledCoursesByUserId_EmptyList() throws Exception {
        // Given
        Long userId = 1L;
        when(enrollmentService.getUserEnrolledCourses(userId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/userCourses/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(enrollmentService).getUserEnrolledCourses(userId);
    }

    @Test
    @WithMockUser(roles = "ROLE_ADMIN")
    void enroll_BundleEnrollment() throws Exception {
        // Given
        EnrollmentRequestInDTO requestDTO = new EnrollmentRequestInDTO();
        requestDTO.setUserIds(Arrays.asList(1L));
        requestDTO.setBundleIds(Arrays.asList(201L));
        requestDTO.setAssignedBy(1L);
        requestDTO.setDeadline(LocalDateTime.of(2025, 8, 31, 10, 30)); // 31st Aug 2025, 10:30 AM
        requestDTO.setStatus("ACTIVE");

        EnrollmentOutDTO enrollmentOutDTO = new EnrollmentOutDTO();
        enrollmentOutDTO.setEnrollmentId(1L);
        enrollmentOutDTO.setUserId(1L);
        enrollmentOutDTO.setBundleId(201L);
        enrollmentOutDTO.setCourseId(101L);
        enrollmentOutDTO.setEnrollmentSource("BUNDLE");

        List<EnrollmentOutDTO> enrollments = Arrays.asList(enrollmentOutDTO);

        when(enrollmentService.enroll(any(EnrollmentRequestInDTO.class))).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(post("/api/service-api/enrollment/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bundleId").value(201L))
                .andExpect(jsonPath("$.data[0].enrollmentSource").value("BUNDLE"));

        verify(enrollmentService).enroll(any(EnrollmentRequestInDTO.class));
    }
//
//    @Test
//    @WithMockUser(authorities = "ROLE_ADMIN")
//    void getEnrollmentStatistics_ServiceException() throws Exception {
//
//        when(enrollmentService.getEnrollmentStats())
//                .thenThrow(new ResourceNotValidException("Failed to calculate enrollment statistics: "));
//
//           mockMvc.perform(get("/api/service-api/enrollment/statistics")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Failed to calculate enrollment statistics: ")); // Optional: check body
//
//            verify(enrollmentService).getEnrollmentStats();
//    }

    // Additional test for server errors (if needed)
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getEnrollmentStatistics_ServerException() throws Exception {
        // Given - Mock the service to throw a runtime exception
        when(enrollmentService.getEnrollmentStats())
                .thenThrow(new ResourceNotValidException("Failed to fetch enrollment statistics"));

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(enrollmentService).getEnrollmentStats();
    }
    @Test
    @WithMockUser(roles= "ROLE_ADMIN")
    void getUserCourseEnrollments_ServiceException() throws Exception {
        // Given
        when(enrollmentService.getIndividualCourseEnrollments())
                .thenThrow(new ResourceNotValidException("Failed to fetch course enrollments"));

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-course-enrollments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(enrollmentService).getIndividualCourseEnrollments();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserBundleEnrollments_ServiceException() throws Exception {
        // Given
        when(enrollmentService.getIndividualBundleEnrollments())
                .thenThrow(new ResourceNotValidException("Failed to fetch individual bundle enrollments: "));

        // When & Then
        mockMvc.perform(get("/api/service-api/enrollment/user-bundle-enrollments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(enrollmentService).getIndividualBundleEnrollments();
    }

//    @Test
//    void getAllEndpoints_Unauthorized() throws Exception {
//        // Test all endpoints without authentication
//        mockMvc.perform(get("/api/service-api/enrollment/statistics"))
//                .andExpect(status().isUnauthorized());
//
//        mockMvc.perform(get("/api/service-api/enrollment/user-enrollments"))
//                .andExpect(status().isUnauthorized());
//
//        mockMvc.perform(get("/api/service-api/enrollment/user-course-enrollments"))
//                .andExpect(status().isUnauthorized());
//
//        mockMvc.perform(get("/api/service-api/enrollment/user-bundle-enrollments"))
//                .andExpect(status().isUnauthorized());
//
//        mockMvc.perform(post("/api/service-api/enrollment/enroll")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void enroll_GroupToCourse() throws Exception {
        // Given
        EnrollmentRequestInDTO requestDTO = new EnrollmentRequestInDTO();
        requestDTO.setGroupIds(Arrays.asList(301L));
        requestDTO.setCourseIds(Arrays.asList(101L));
        requestDTO.setAssignedBy(1L);
        requestDTO.setStatus("PENDING");
        requestDTO.setDeadline(LocalDateTime.of(2025, 8, 31, 10, 30)); // 31st Aug 2025, 10:30 AM

        EnrollmentOutDTO enrollmentOutDTO = new EnrollmentOutDTO();
        enrollmentOutDTO.setEnrollmentId(1L);
        enrollmentOutDTO.setUserId(1L);
        enrollmentOutDTO.setGroupId(301L);
        enrollmentOutDTO.setCourseId(101L);
        enrollmentOutDTO.setEnrollmentSource("GROUP");

        List<EnrollmentOutDTO> enrollments = Arrays.asList(enrollmentOutDTO);

        when(enrollmentService.enroll(any(EnrollmentRequestInDTO.class))).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(post("/api/service-api/enrollment/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].groupId").value(301L))
                .andExpect(jsonPath("$.data[0].enrollmentSource").value("GROUP"));

        verify(enrollmentService).enroll(any(EnrollmentRequestInDTO.class));
    }
}