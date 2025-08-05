package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.constants.CommonConstants;
import com.nt.user_service_lms.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.exception.ResourceNotValidException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.GroupRepository;
import com.nt.user_service_lms.repository.UserGroupRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.serviceImpl.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private CourseMicroserviceClient courseMicroserviceClient;

    @Mock
    private UserGroupRepository userGroupRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private EnrollmentRequestInDTO validUserToCourseRequest;
    private EnrollmentRequestInDTO validUserToBundleRequest;
    private EnrollmentRequestInDTO validGroupToCourseRequest;
    private EnrollmentRequestInDTO validGroupToBundleRequest;
    private Enrollment sampleEnrollment;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestData();
    }

    private void setupTestData() {
        // Sample user
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setUserName("johndoe");

        // Sample enrollment
        sampleEnrollment = new Enrollment();
        sampleEnrollment.setEnrollmentId(1L);
        sampleEnrollment.setUserId(1L);
        sampleEnrollment.setCourseId(100L);
        sampleEnrollment.setAssignedBy(2L);
        sampleEnrollment.setAssignedAt(LocalDateTime.now());
        sampleEnrollment.setStatus("PENDING");
        sampleEnrollment.setEnrollmentSource("INDIVIDUAL");
        sampleEnrollment.setCreatedAt(LocalDateTime.now());
        sampleEnrollment.setUpdatedAt(LocalDateTime.now());
        sampleEnrollment.setActive(true);

        // Valid requests
        validUserToCourseRequest = new EnrollmentRequestInDTO();
        validUserToCourseRequest.setUserIds(List.of(1L, 2L));
        validUserToCourseRequest.setCourseIds(List.of(100L, 101L));
        validUserToCourseRequest.setAssignedBy(3L);
        validUserToCourseRequest.setStatus("PENDING");
        validUserToCourseRequest.setDeadline(LocalDateTime.now().plusDays(30));

        validUserToBundleRequest = new EnrollmentRequestInDTO();
        validUserToBundleRequest.setUserIds(List.of(1L, 2L));
        validUserToBundleRequest.setBundleIds(List.of(200L, 201L));
        validUserToBundleRequest.setAssignedBy(3L);
        validUserToBundleRequest.setStatus("PENDING");
        validUserToBundleRequest.setDeadline(LocalDateTime.now().plusDays(30));

        validGroupToCourseRequest = new EnrollmentRequestInDTO();
        validGroupToCourseRequest.setGroupIds(List.of(10L, 11L));
        validGroupToCourseRequest.setCourseIds(List.of(100L, 101L));
        validGroupToCourseRequest.setAssignedBy(3L);
        validGroupToCourseRequest.setStatus("PENDING");
        validGroupToCourseRequest.setDeadline(LocalDateTime.now().plusDays(30));

        validGroupToBundleRequest = new EnrollmentRequestInDTO();
        validGroupToBundleRequest.setGroupIds(List.of(10L, 11L));
        validGroupToBundleRequest.setBundleIds(List.of(200L, 201L));
        validGroupToBundleRequest.setAssignedBy(3L);
        validGroupToBundleRequest.setStatus("PENDING");
        validGroupToBundleRequest.setDeadline(LocalDateTime.now().plusDays(30));
    }

    // Enrollment Tests
    @Test
    void testEnrollUsersToCourses_Success() {
        // Given
        setupValidationMocks();
        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                anyLong(), isNull(), anyLong(), isNull(), eq("INDIVIDUAL"), eq(true)))
                .thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);

        // When
        List<EnrollmentOutDTO> result = enrollmentService.enroll(validUserToCourseRequest);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size()); // 2 users * 2 courses
        verify(enrollmentRepository, times(4)).save(any(Enrollment.class));
    }

    @Test
    void testEnrollUsersToBundles_Success() {
        // Given
        setupValidationMocks();
        when(courseMicroserviceClient.findCourseIdsByBundleId(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(100L, 101L)));
        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                anyLong(), isNull(), anyLong(), anyLong(), eq("BUNDLE"), eq(true)))
                .thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);

        // When
        List<EnrollmentOutDTO> result = enrollmentService.enroll(validUserToBundleRequest);

        // Then
        assertNotNull(result);
        assertEquals(8, result.size()); // 2 users * 2 bundles * 2 courses per bundle
        verify(enrollmentRepository, times(8)).save(any(Enrollment.class));
    }

    @Test
    void testEnrollGroupsToCourses_Success() {
        // Given
        setupValidationMocks();
        when(userGroupRepository.findUserIdsByGroupId(anyLong()))
                .thenReturn(List.of(1L, 2L));
        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                anyLong(), anyLong(), anyLong(), isNull(), eq("GROUP"), eq(true)))
                .thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);

        // When
        List<EnrollmentOutDTO> result = enrollmentService.enroll(validGroupToCourseRequest);

        // Then
        assertNotNull(result);
        assertEquals(8, result.size()); // 2 groups * 2 courses * 2 users per group
        verify(enrollmentRepository, times(8)).save(any(Enrollment.class));
    }

    @Test
    void testEnrollGroupsToBundles_Success() {
        // Given
        setupValidationMocks();
        when(userGroupRepository.findUserIdsByGroupId(anyLong()))
                .thenReturn(List.of(1L, 2L));
        when(courseMicroserviceClient.findCourseIdsByBundleId(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(100L, 101L)));
        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                anyLong(), anyLong(), anyLong(), anyLong(), eq("GROUP_BUNDLE"), eq(true)))
                .thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);

        // When
        List<EnrollmentOutDTO> result = enrollmentService.enroll(validGroupToBundleRequest);

        // Then
        assertNotNull(result);
        assertEquals(16, result.size()); // 2 groups * 2 bundles * 2 courses * 2 users
        verify(enrollmentRepository, times(16)).save(any(Enrollment.class));
    }

    @Test
    void testEnroll_InvalidRequest() {
        // Given
        EnrollmentRequestInDTO invalidRequest = new EnrollmentRequestInDTO();
        // Invalid request with both users and groups

        // When & Then
        assertThrows(ResourceNotValidException.class,
                () -> enrollmentService.enroll(invalidRequest));
    }

    @Test
    void testEnroll_DuplicateEnrollment() {
        // Given
        setupValidationMocks();
        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                anyLong(), isNull(), anyLong(), isNull(), eq("INDIVIDUAL"), eq(true)))
                .thenReturn(true);

        // When & Then
        assertThrows(ResourceAlreadyExistsException.class,
                () -> enrollmentService.enroll(validUserToCourseRequest));
    }

    @Test
    void testEnroll_UserNotFound() {
        // Given
        when(userRepository.existsById(3L)).thenReturn(true);
        when(userRepository.findExistingIds(anyList())).thenReturn(List.of()); // Empty list

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> enrollmentService.enroll(validUserToCourseRequest));
    }

    @Test
    void testEnroll_CourseNotFound() {
        // Given
        when(userRepository.existsById(3L)).thenReturn(true);
        when(userRepository.findExistingIds(anyList())).thenReturn(List.of(1L, 2L));
        when(courseMicroserviceClient.getExistingCourseIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of())); // Empty list

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> enrollmentService.enroll(validUserToCourseRequest));
    }

    @Test
    void testEnroll_BundleNotFound() {
        // Given
        when(userRepository.existsById(3L)).thenReturn(true);
        when(userRepository.findExistingIds(anyList())).thenReturn(List.of(1L, 2L));
        when(courseMicroserviceClient.getExistingBundleIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of())); // Empty list

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> enrollmentService.enroll(validUserToBundleRequest));
    }

    @Test
    void testEnroll_GroupNotFound() {
        // Given
        when(userRepository.existsById(3L)).thenReturn(true);
        when(groupRepository.findExistingIds(anyList())).thenReturn(List.of()); // Empty list

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> enrollmentService.enroll(validGroupToCourseRequest));
    }

    @Test
    void testEnroll_AssignedByUserNotFound() {
        // Given
        when(userRepository.existsById(3L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> enrollmentService.enroll(validUserToCourseRequest));
    }

    @Test
    void testEnroll_ExceptionHandling() {
        // Given
        setupValidationMocks();
        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(ResourceNotValidException.class,
                () -> enrollmentService.enroll(validUserToCourseRequest));
    }

    // Enrollment Statistics Tests
    @Test
    void testGetEnrollmentStats_Success() {
        // Given
        List<Enrollment> enrollments = createSampleEnrollments();
        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // When
        EnrollmentDashBoardStatsOutDTO result = enrollmentService.getEnrollmentStats();

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getTotalEnrollments());
        assertEquals(4L, result.getActiveEnrollments());
        assertEquals(1L, result.getInactiveEnrollments());
        assertEquals(2L, result.getPendingEnrollments());
        assertEquals(2L, result.getInProgressEnrollments());
        assertEquals(1L, result.getCompletedEnrollments());
        assertTrue(result.getOverallCompletionRate().compareTo(BigDecimal.valueOf(20.00)) == 0);
    }

    @Test
    void testGetEnrollmentStats_EmptyDatabase() {
        // Given
        when(enrollmentRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        EnrollmentDashBoardStatsOutDTO result = enrollmentService.getEnrollmentStats();

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotalEnrollments());
        assertEquals(0L, result.getActiveEnrollments());
        assertEquals(BigDecimal.ZERO, result.getOverallCompletionRate());
        assertTrue(result.getStatusDistribution().isEmpty());
        assertTrue(result.getSourceDistribution().isEmpty());
    }

    @Test
    void testGetEnrollmentStats_ExceptionHandling() {
        // Given
        when(enrollmentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(ResourceNotValidException.class,
                () -> enrollmentService.getEnrollmentStats());
    }

    // User Enrollments Tests
    @Test
    void testGetUserEnrollmentsByUserID_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L))
                .thenReturn(createUserEnrollments());
        setupCourseMicroserviceMocks();

        // When
        UserEnrollmentsOutDTO result = enrollmentService.getUserEnrollmentsByUserID(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("John Doe", result.getUserName());
        assertEquals(2L, result.getCourseEnrollments());
        assertEquals(1L, result.getBundleEnrollments());
    }

    @Test
    void testGetUserEnrollmentsByUserID_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> enrollmentService.getUserEnrollmentsByUserID(1L));
    }

    @Test
    void testGetUserEnrollmentsByUserID_EmptyEnrollments() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L))
                .thenReturn(Collections.emptyList());

        // When
        UserEnrollmentsOutDTO result = enrollmentService.getUserEnrollmentsByUserID(1L);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getCourseEnrollments());
        assertEquals(0L, result.getBundleEnrollments());
        assertEquals(0L, result.getTotalCourses());
        assertEquals(0.0f, result.getAverageCompletion());
        assertEquals(0, result.getUpcomingDeadlines());

    }

    @Test
    void testGetAllUsersEnrollments_Success() {
        // Given
        List<Enrollment> allEnrollments = createUserEnrollments();
        when(enrollmentRepository.findByIsActiveTrue()).thenReturn(allEnrollments);
        when(userRepository.findAllById(anySet())).thenReturn(List.of(sampleUser));
        setupBatchProcessingMocks();

        // When
        List<UserEnrollmentsOutDTO> result = enrollmentService.getAllUsersEnrollments();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllUsersEnrollments_EmptyEnrollments() {
        // Given
        when(enrollmentRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

        // When
        List<UserEnrollmentsOutDTO> result = enrollmentService.getAllUsersEnrollments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Individual Course Enrollments Tests
    @Test
    void testGetIndividualCourseEnrollments_Success() {
        // Given
        List<Enrollment> courseEnrollments = createIndividualCourseEnrollments();
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull("INDIVIDUAL"))
                .thenReturn(courseEnrollments);
        setupBatchProcessingMocks();
        setupCourseInfoMocks();

        // When
        List<UserCourseEnrollmentOutDTO> result = enrollmentService.getIndividualCourseEnrollments();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetIndividualCourseEnrollments_EmptyEnrollments() {
        // Given
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull("INDIVIDUAL"))
                .thenReturn(Collections.emptyList());

        // When
        List<UserCourseEnrollmentOutDTO> result = enrollmentService.getIndividualCourseEnrollments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIndividualCourseEnrollments_ExceptionHandling() {
        // Given
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull("INDIVIDUAL"))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(ResourceNotValidException.class,
                () -> enrollmentService.getIndividualCourseEnrollments());
    }

    // Individual Bundle Enrollments Tests
    @Test
    void testGetIndividualBundleEnrollments_Success() {
        // Given
        List<Enrollment> bundleEnrollments = createIndividualBundleEnrollments();
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull("BUNDLE"))
                .thenReturn(bundleEnrollments);
        setupBatchProcessingMocks();
        setupBundleInfoMocks();

        // When
        List<UserBundleEnrollmentOutDTO> result = enrollmentService.getIndividualBundleEnrollments();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetIndividualBundleEnrollments_EmptyEnrollments() {
        // Given
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull("BUNDLE"))
                .thenReturn(Collections.emptyList());

        // When
        List<UserBundleEnrollmentOutDTO> result = enrollmentService.getIndividualBundleEnrollments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIndividualBundleEnrollments_ExceptionHandling() {
        // Given
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull("BUNDLE"))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(ResourceNotValidException.class,
                () -> enrollmentService.getIndividualBundleEnrollments());
    }

    // Get User Enrolled Courses Tests
    @Test
    void testGetUserEnrolledCourses_Success() {
        // Given
        List<Enrollment> enrollments = createMultipleCourseEnrollments();
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(enrollments);

        // When
        List<UserCourseEnrollDetails> result = enrollmentService.getUserEnrolledCourses(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Should have 2 unique courses
        // Verify earliest enrollment is selected for each course
        UserCourseEnrollDetails course1Details = result.stream()
                .filter(detail -> detail.getCourseId().equals(100L))
                .findFirst().orElse(null);
        assertNotNull(course1Details);
        assertEquals(2L, course1Details.getAssignedById()); // Earlier assignment
    }

    @Test
    void testGetUserEnrolledCourses_EmptyEnrollments() {
        // Given
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L))
                .thenReturn(Collections.emptyList());

        // When
        List<UserCourseEnrollDetails> result = enrollmentService.getUserEnrolledCourses(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserEnrolledCourses_NullCourseIds() {
        // Given
        Enrollment enrollmentWithNullCourse = new Enrollment();
        enrollmentWithNullCourse.setCourseId(null);
        enrollmentWithNullCourse.setAssignedAt(LocalDateTime.now());
        enrollmentWithNullCourse.setAssignedBy(2L);

        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L))
                .thenReturn(List.of(enrollmentWithNullCourse));

        // When
        List<UserCourseEnrollDetails> result = enrollmentService.getUserEnrolledCourses(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Helper Methods
    private void setupValidationMocks() {
        when(userRepository.existsById(3L)).thenReturn(true);
        when(userRepository.findExistingIds(anyList())).thenReturn(List.of(1L, 2L));
        when(groupRepository.findExistingIds(anyList())).thenReturn(List.of(10L, 11L));
        when(courseMicroserviceClient.getExistingCourseIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of(100L, 101L)));
        when(courseMicroserviceClient.getExistingBundleIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of(200L, 201L)));
    }

    private List<Enrollment> createSampleEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // Pending individual enrollment
        Enrollment e1 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(20));
        enrollments.add(e1);

        // In progress individual enrollment
        Enrollment e2 = createEnrollment(2L, null, 101L, null, "IN_PROGRESS", "INDIVIDUAL", true, LocalDateTime.now().minusDays(15));
        enrollments.add(e2);

        // Completed group enrollment
        Enrollment e3 = createEnrollment(3L, 10L, 102L, null, "COMPLETED", "GROUP", true, LocalDateTime.now().minusDays(10));
        e3.setCompletedAt(LocalDateTime.now().minusDays(2));
        enrollments.add(e3);

        // Pending bundle enrollment
        Enrollment e4 = createEnrollment(4L, null, 103L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(5));
        enrollments.add(e4);

        // Inactive enrollment
        Enrollment e5 = createEnrollment(5L, null, 104L, null, "IN_PROGRESS", "INDIVIDUAL", false, LocalDateTime.now().minusDays(30));
        enrollments.add(e5);

        return enrollments;
    }

    private Enrollment createEnrollment(Long userId, Long groupId, Long courseId, Long bundleId,
                                        String status, String source, boolean isActive, LocalDateTime createdAt) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setGroupId(groupId);
        enrollment.setCourseId(courseId);
        enrollment.setBundleId(bundleId);
        enrollment.setStatus(status);
        enrollment.setEnrollmentSource(source);
        enrollment.setActive(isActive);
        enrollment.setCreatedAt(createdAt);
        enrollment.setAssignedAt(createdAt);
        enrollment.setAssignedBy(1L);
        return enrollment;
    }

    private List<Enrollment> createUserEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // Direct course enrollments
        Enrollment courseEnrollment1 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(10));
        Enrollment courseEnrollment2 = createEnrollment(1L, null, 101L, null, "IN_PROGRESS", "INDIVIDUAL", true, LocalDateTime.now().minusDays(5));

        // Bundle enrollment
        Enrollment bundleEnrollment = createEnrollment(1L, null, 102L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(3));

        enrollments.add(courseEnrollment1);
        enrollments.add(courseEnrollment2);
        enrollments.add(bundleEnrollment);

        return enrollments;
    }

    private List<Enrollment> createIndividualCourseEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        Enrollment e1 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(10));
        Enrollment e2 = createEnrollment(2L, null, 100L, null, "IN_PROGRESS", "INDIVIDUAL", true, LocalDateTime.now().minusDays(5));
        enrollments.add(e1);
        enrollments.add(e2);
        return enrollments;
    }

    private List<Enrollment> createIndividualBundleEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        Enrollment e1 = createEnrollment(1L, null, 100L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(10));
        Enrollment e2 = createEnrollment(1L, null, 101L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(10));
        enrollments.add(e1);
        enrollments.add(e2);
        return enrollments;
    }

    private List<Enrollment> createMultipleCourseEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // Two enrollments for course 100 - earlier one should be selected
        Enrollment e1 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(10));
        e1.setAssignedBy(2L);
        Enrollment e2 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(5));
        e2.setAssignedBy(3L);

        // One enrollment for course 101
        Enrollment e3 = createEnrollment(1L, null, 101L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(3));
        e3.setAssignedBy(4L);

        enrollments.add(e1);
        enrollments.add(e2);
        enrollments.add(e3);

        return enrollments;
    }

    private void setupCourseMicroserviceMocks() {
        when(courseMicroserviceClient.getCourseNameById(anyLong()))
                .thenReturn(ResponseEntity.ok("Sample Course"));
        when(courseMicroserviceClient.getBundleNameById(anyLong()))
                .thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success("Sample Bundle", null)));
        when(courseMicroserviceClient.findCourseIdsByBundleId(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(102L, 103L)));

        CourseProgressWithMetaDTO progress = new CourseProgressWithMetaDTO();
        progress.setCourseCompletionPercentage(75.0);
        when(courseMicroserviceClient.getCourseProgressWithMeta(anyLong(), anyLong()))
                .thenReturn(progress);
    }

}