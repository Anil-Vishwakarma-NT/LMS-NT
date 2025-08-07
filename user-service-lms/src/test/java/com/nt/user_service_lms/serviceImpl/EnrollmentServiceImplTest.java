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
    private User sampleUserSecond;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample user
        sampleUser = new User();
        sampleUser.setUserId(4L);
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setUserName("johndoe");


        sampleUserSecond = new User();
        sampleUserSecond.setUserId(3L);
        sampleUserSecond.setFirstName("john");
        sampleUserSecond.setLastName("Evan");
        sampleUserSecond.setUserName("johnevan");

        // Sample enrollment
        sampleEnrollment = new Enrollment();
        sampleEnrollment.setEnrollmentId(1L);
        sampleEnrollment.setUserId(4L);
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
        validUserToCourseRequest.setUserIds(List.of(4L));
        validUserToCourseRequest.setCourseIds(List.of(100L, 101L));
        validUserToCourseRequest.setAssignedBy(3L);
        validUserToCourseRequest.setStatus("ACTIVE");
        validUserToCourseRequest.setDeadline(LocalDateTime.now().plusDays(30));

        validUserToBundleRequest = new EnrollmentRequestInDTO();
        validUserToBundleRequest.setUserIds(List.of(4L));
        validUserToBundleRequest.setBundleIds(List.of(200L, 201L));
        validUserToBundleRequest.setAssignedBy(3L);
        validUserToBundleRequest.setStatus("ACTIVE");
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
//    @Test
//    void testEnrollUsersToCourses_Success() {
//        // Given
//        setupValidationMocks();
//        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
//                anyLong(), isNull(), anyLong(), isNull(), eq("INDIVIDUAL"), eq(true)))
//                .thenReturn(false);
//        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);
//
//        // When
//        List<EnrollmentOutDTO> result = enrollmentService.enroll(validUserToCourseRequest);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(4, result.size()); // 2 users * 2 courses
//        verify(enrollmentRepository, times(4)).save(any(Enrollment.class));
//    }

    @Test
    void testEnrollUsersToBundles_Success() {
        // Given
        Long assignedByUserId = 3L;
        List<Long> userIds = List.of(4L);
        List<Long> bundleIds = List.of(200L, 201L);
        List<Long> courseIds = List.of(100L, 101L);

        // Mock the assigned by user validation (this was missing/incorrect)
        when(userRepository.existsById(assignedByUserId)).thenReturn(true);

        // Mock user existence validation for target users
        when(userRepository.findExistingIds(userIds)).thenReturn(userIds);

        // Mock bundle validation
        when(courseMicroserviceClient.getExistingBundleIds(bundleIds))
                .thenReturn(ResponseEntity.ok(bundleIds));

        // Mock course IDs for each bundle
        when(courseMicroserviceClient.findCourseIdsByBundleId(200L))
                .thenReturn(ResponseEntity.ok(courseIds));
        when(courseMicroserviceClient.findCourseIdsByBundleId(201L))
                .thenReturn(ResponseEntity.ok(courseIds));

        // Mock enrollment existence check (should return false for new enrollments)
        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
                anyLong(), isNull(), anyLong(), anyLong(), eq("BUNDLE"), eq(true)))
                .thenReturn(false);

        // Mock enrollment save operation
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);

        // When
        List<EnrollmentOutDTO> result = enrollmentService.enroll(validUserToBundleRequest);


        assertNotNull(result);
        verify(enrollmentRepository, times(4)).save(any(Enrollment.class));

        verify(userRepository).existsById(assignedByUserId);
        verify(userRepository).findExistingIds(userIds);
        verify(courseMicroserviceClient).getExistingBundleIds(bundleIds);
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
        assertThrows(ResourceNotValidException.class,
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
Long userId = 4L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(userId))
                .thenReturn(createUserEnrollments());
        setupCourseMicroserviceMocks();

        // When
        UserEnrollmentsOutDTO result = enrollmentService.getUserEnrollmentsByUserID(userId);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getUserId());
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

        Long userId = 4L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(userId))
                .thenReturn(Collections.emptyList());

        // When
        UserEnrollmentsOutDTO result = enrollmentService.getUserEnrollmentsByUserID(userId);

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
        assertFalse(!result.isEmpty());
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

        Long assignedByUserId = 3L;
        List<Long> userIds = List.of(4L);
        List<Long> groups = List.of(10L, 11L);
        List<Long> bundleIds = List.of(200L, 201L);
        List<Long> courseIds = List.of(100L, 101L);
        when(userRepository.existsById(assignedByUserId)).thenReturn(true);
        when(userRepository.findExistingIds(userIds)).thenReturn(userIds);
        when(groupRepository.findExistingIds(groups)).thenReturn(groups);
        when(courseMicroserviceClient.getExistingCourseIds(courseIds))
                .thenReturn(ResponseEntity.ok(List.of(100L, 101L)));
        when(courseMicroserviceClient.getExistingBundleIds(bundleIds))
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
    // Add these helper methods to your EnrollmentServiceImplTest class

    private void setupBatchProcessingMocks() {
        // Mock user repository batch calls
        when(userRepository.findAllById(anySet())).thenReturn(List.of(sampleUser));

        // Mock course microservice batch calls
        when(courseMicroserviceClient.getExistingCourseIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of(100L, 101L, 102L, 103L)));
        when(courseMicroserviceClient.getExistingBundleIds(anyList()))
                .thenReturn(ResponseEntity.ok(List.of(200L, 201L)));

        // Mock course info batch call
        List<CourseInfoOutDTO> courseInfoList = new ArrayList<>();
        CourseInfoOutDTO courseInfo1 = new CourseInfoOutDTO();
        courseInfo1.setCourseId(100L);
        courseInfo1.setOwnerId(1L);
        courseInfo1.setActive(true);
        CourseInfoOutDTO courseInfo2 = new CourseInfoOutDTO();
        courseInfo2.setCourseId(101L);
        courseInfo2.setOwnerId(1L);
        courseInfo2.setActive(true);
        courseInfoList.add(courseInfo1);
        courseInfoList.add(courseInfo2);

        StandardResponseOutDTO<List<CourseInfoOutDTO>> courseInfoResponse =
                StandardResponseOutDTO.success(courseInfoList, "Success");
        when(courseMicroserviceClient.getCourseInfo())
                .thenReturn(ResponseEntity.ok(courseInfoResponse));

        // Mock bundle info batch call
        List<BundleInfoOutDTO> bundleInfoList = new ArrayList<>();
        BundleInfoOutDTO bundleInfo1 = new BundleInfoOutDTO();
        bundleInfo1.setBundleId(200L);
        bundleInfo1.setTotalCourses(2L);
        bundleInfo1.setActive(true);
        bundleInfoList.add(bundleInfo1);

        StandardResponseOutDTO<List<BundleInfoOutDTO>> bundleInfoResponse =
                StandardResponseOutDTO.success(bundleInfoList, "Success");
        when(courseMicroserviceClient.getBundleInfo())
                .thenReturn(ResponseEntity.ok(bundleInfoResponse));
    }

    private void setupCourseInfoMocks() {
        // Mock course info for individual course enrollments
        List<CourseInfoOutDTO> courseInfoList = new ArrayList<>();
        CourseInfoOutDTO courseInfo = new CourseInfoOutDTO();
        courseInfo.setCourseId(100L);
        courseInfo.setOwnerId(1L);
        courseInfo.setActive(true);
        courseInfoList.add(courseInfo);

        StandardResponseOutDTO<List<CourseInfoOutDTO>> response =
                StandardResponseOutDTO.success(courseInfoList, "Success");
        when(courseMicroserviceClient.getCourseInfo())
                .thenReturn(ResponseEntity.ok(response));

        when(userRepository.findAllById(anyList())).thenReturn(List.of(sampleUser));

        CourseProgressWithMetaDTO progress = new CourseProgressWithMetaDTO();
        progress.setCourseCompletionPercentage(75.0);
        when(courseMicroserviceClient.getCourseProgressWithMeta(anyLong(), anyLong()))
                .thenReturn(progress);
    }

    private void setupBundleInfoMocks() {
        // Mock bundle info for individual bundle enrollments
        List<BundleInfoOutDTO> bundleInfoList = new ArrayList<>();
        BundleInfoOutDTO bundleInfo = new BundleInfoOutDTO();
        bundleInfo.setBundleId(200L);
        bundleInfo.setTotalCourses(2L);
        bundleInfo.setActive(true);
        bundleInfoList.add(bundleInfo);

        StandardResponseOutDTO<List<BundleInfoOutDTO>> response =
                StandardResponseOutDTO.success(bundleInfoList, "Success");
        when(courseMicroserviceClient.getBundleInfo())
                .thenReturn(ResponseEntity.ok(response));

        when(userRepository.findAllById(anyList())).thenReturn(List.of(sampleUser));

        CourseProgressWithMetaDTO progress = new CourseProgressWithMetaDTO();
        progress.setCourseCompletionPercentage(80.0);
        when(courseMicroserviceClient.getCourseProgressWithMeta(anyLong(), anyLong()))
                .thenReturn(progress);
    }

// Additional test cases that were missing

    @Test
    void testGetEnrollmentStats_WithDifferentStatuses() {
        // Given
        List<Enrollment> enrollments = createDiverseEnrollments();
        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // When
        EnrollmentDashBoardStatsOutDTO result = enrollmentService.getEnrollmentStats();

        // Then
        assertNotNull(result);
        assertEquals(6L, result.getTotalEnrollments());
        assertEquals(5L, result.getActiveEnrollments());
        assertEquals(1L, result.getInactiveEnrollments());
        assertEquals(2L, result.getPendingEnrollments());
        assertEquals(2L, result.getInProgressEnrollments());
        assertEquals(1L, result.getCompletedEnrollments());
        assertEquals(1L, result.getExpiredEnrollments());
        assertTrue(result.getOverallCompletionRate().compareTo(BigDecimal.valueOf(16.67)) == 0);

        // Check status distribution
        Map<String, BigDecimal> statusDistribution = result.getStatusDistribution();
        assertTrue(statusDistribution.get("PENDING").compareTo(BigDecimal.valueOf(33.33)) == 0);
        assertTrue(statusDistribution.get("IN_PROGRESS").compareTo(BigDecimal.valueOf(33.33)) == 0);
        assertTrue(statusDistribution.get("COMPLETED").compareTo(BigDecimal.valueOf(16.67)) == 0);
        assertTrue(statusDistribution.get("EXPIRED").compareTo(BigDecimal.valueOf(16.67)) == 0);
    }

    @Test
    void testGetUserEnrollmentsByUserID_WithMixedEnrollments() {
        // Given
        Long userId = 4L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(userId))
                .thenReturn(createMixedUserEnrollments());
        when(userRepository.findById(4L)).thenReturn(Optional.of(sampleUser));
        List<Enrollment> mixedEnrollments = createMixedUserEnrollments();
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(4L)).thenReturn(mixedEnrollments);
        setupCourseMicroserviceMocks();

        // When
            UserEnrollmentsOutDTO result = enrollmentService.getUserEnrollmentsByUserID(userId);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getUserId());
        assertEquals("John Doe", result.getUserName());
        assertEquals(2L, result.getCourseEnrollments()); // Direct course enrollments
        assertEquals(2L, result.getBundleEnrollments()); // Bundle enrollments
        assertEquals(4L, result.getTotalCourses()); // 2 direct + 2 from bundle
        assertTrue(result.getAverageCompletion() > 0);
        assertTrue(result.isStatus());
    }

    @Test
    void testGetIndividualCourseEnrollments_WithMultipleUsersPerCourse() {
        // Given
        List<Enrollment> courseEnrollments = createMultipleUsersCourseEnrollments();
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull("INDIVIDUAL"))
                .thenReturn(courseEnrollments);
        setupBatchProcessingMocks();
        setupCourseInfoMocks();

        // When
        List<UserCourseEnrollmentOutDTO> result = enrollmentService.getIndividualCourseEnrollments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // One unique course

        UserCourseEnrollmentOutDTO courseDTO = result.get(0);
        assertEquals(100L, courseDTO.getCourseId());
        assertEquals(2L, courseDTO.getIndividualEnrollments()); // Two users enrolled
        assertNotNull(courseDTO.getEnrolledUserOutDTOList());
        assertEquals(2, courseDTO.getEnrolledUserOutDTOList().size());
    }

    @Test
    void testGetIndividualBundleEnrollments_WithMultipleUsersPerBundle() {
        // Given
        List<Enrollment> bundleEnrollments = createMultipleUsersBundleEnrollments();
        when(enrollmentRepository.findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull("BUNDLE"))
                .thenReturn(bundleEnrollments);
        setupBatchProcessingMocks();
        setupBundleInfoMocks();

        // When
        List<UserBundleEnrollmentOutDTO> result = enrollmentService.getIndividualBundleEnrollments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // One unique bundle

        UserBundleEnrollmentOutDTO bundleDTO = result.get(0);
        assertEquals(200L, bundleDTO.getBundleId());
        assertEquals(2L, bundleDTO.getIndividualEnrollments()); // Two users enrolled
        assertNotNull(bundleDTO.getEnrolledUserOutDTOList());
        assertEquals(2, bundleDTO.getEnrolledUserOutDTOList().size());
        assertTrue(bundleDTO.getAverageCompletion() >= 0);
    }

    @Test
    void testGetUserEnrolledCourses_WithDuplicateEnrollments() {
        // Given
        List<Enrollment> enrollments = createDuplicateCourseEnrollments();
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(enrollments);

        // When
        List<UserCourseEnrollDetails> result = enrollmentService.getUserEnrolledCourses(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Should have 2 unique courses despite duplicates

        // Verify earliest enrollment is selected for course 100
        UserCourseEnrollDetails course100Details = result.stream()
                .filter(detail -> detail.getCourseId().equals(100L))
                .findFirst().orElse(null);
        assertNotNull(course100Details);
        assertEquals(2L, course100Details.getAssignedById()); // Earlier assignment
    }

    @Test
    void testGetAllUsersEnrollments_Performance() {
        // Given
        List<Enrollment> largeEnrollmentSet = createLargeEnrollmentSet();
        when(enrollmentRepository.findByIsActiveTrue()).thenReturn(largeEnrollmentSet);

        List<User> users = createMultipleUsers();
        when(userRepository.findAllById(anySet())).thenReturn(users);
        setupBatchProcessingMocks();

        // When
        List<UserEnrollmentsOutDTO> result = enrollmentService.getAllUsersEnrollments();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(courseMicroserviceClient, atMost(10)).getCourseProgressWithMeta(anyLong(), anyLong());
    }

    @Test
    void testEnroll_GroupWithNoUsers() {
        // Given
        setupValidationMocks();
        when(userGroupRepository.findUserIdsByGroupId(10L)).thenReturn(Collections.emptyList());

        // When
        List<EnrollmentOutDTO> result = enrollmentService.enroll(validGroupToCourseRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // No enrollments created when group has no users
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }



// Helper methods for additional test data

    private List<Enrollment> createDiverseEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        enrollments.add(createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(10)));
        enrollments.add(createEnrollment(2L, null, 101L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(15)));
        enrollments.add(createEnrollment(3L, null, 102L, null, "IN_PROGRESS", "INDIVIDUAL", true, LocalDateTime.now().minusDays(20)));
        enrollments.add(createEnrollment(4L, 10L, 103L, null, "IN_PROGRESS", "GROUP", true, LocalDateTime.now().minusDays(25)));
        enrollments.add(createEnrollment(5L, null, 104L, 200L, "COMPLETED", "BUNDLE", true, LocalDateTime.now().minusDays(30)));
        enrollments.add(createEnrollment(6L, null, 105L, null, "EXPIRED", "INDIVIDUAL", false, LocalDateTime.now().minusDays(35)));

        return enrollments;
    }

    private List<Enrollment> createMixedUserEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // Direct course enrollments
        enrollments.add(createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(10)));
        enrollments.add(createEnrollment(1L, null, 101L, null, "IN_PROGRESS", "INDIVIDUAL", true, LocalDateTime.now().minusDays(5)));

        // Bundle enrollment with multiple courses
        enrollments.add(createEnrollment(1L, null, 102L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(3)));
        enrollments.add(createEnrollment(1L, null, 103L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(3)));

        return enrollments;
    }

    private List<Enrollment> createMultipleUsersCourseEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // User 1 enrolled in course 100
        Enrollment e1 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(10));
        e1.setEnrollmentId(1L);
        enrollments.add(e1);

        // User 2 enrolled in course 100
        Enrollment e2 = createEnrollment(2L, null, 100L, null, "IN_PROGRESS", "INDIVIDUAL", true, LocalDateTime.now().minusDays(5));
        e2.setEnrollmentId(2L);
        enrollments.add(e2);

        return enrollments;
    }

    private List<Enrollment> createMultipleUsersBundleEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // User 1 enrolled in bundle 200 (2 courses)
        enrollments.add(createEnrollment(1L, null, 100L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(10)));
        enrollments.add(createEnrollment(1L, null, 101L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(10)));

        // User 2 enrolled in bundle 200 (2 courses)
        enrollments.add(createEnrollment(2L, null, 100L, 200L, "IN_PROGRESS", "BUNDLE", true, LocalDateTime.now().minusDays(5)));
        enrollments.add(createEnrollment(2L, null, 101L, 200L, "IN_PROGRESS", "BUNDLE", true, LocalDateTime.now().minusDays(5)));

        return enrollments;
    }

    private List<Enrollment> createDuplicateCourseEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();

        // Multiple enrollments for course 100 - earliest should be selected
        Enrollment e1 = createEnrollment(1L, null, 100L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(20));
        e1.setAssignedBy(2L);

        Enrollment e2 = createEnrollment(1L, null, 100L, null, "PENDING", "GROUP", true, LocalDateTime.now().minusDays(10));
        e2.setAssignedBy(3L);

        Enrollment e3 = createEnrollment(1L, null, 100L, 200L, "PENDING", "BUNDLE", true, LocalDateTime.now().minusDays(5));
        e3.setAssignedBy(4L);

        // Single enrollment for course 101
        Enrollment e4 = createEnrollment(1L, null, 101L, null, "PENDING", "INDIVIDUAL", true, LocalDateTime.now().minusDays(15));
        e4.setAssignedBy(5L);

        enrollments.add(e1);
        enrollments.add(e2);
        enrollments.add(e3);
        enrollments.add(e4);

        return enrollments;
    }

    private List<Enrollment> createLargeEnrollmentSet() {
        List<Enrollment> enrollments = new ArrayList<>();

        // Create 50 enrollments across 5 users and 10 courses
        for (long userId = 1L; userId <= 5L; userId++) {
            for (long courseId = 100L; courseId <= 109L; courseId++) {
                enrollments.add(createEnrollment(userId, null, courseId, null, "PENDING", "INDIVIDUAL",
                        true, LocalDateTime.now().minusDays((int)(userId + courseId) % 30)));
            }
        }

        return enrollments;
    }

    private List<User> createMultipleUsers() {
        List<User> users = new ArrayList<>();

        for (long i = 1L; i <= 5L; i++) {
            User user = new User();
            user.setUserId(i);
            user.setFirstName("User" + i);
            user.setLastName("Test");
            user.setUserName("user" + i + "test");
            users.add(user);
        }

        return users;
    }

//    @Test
//    void testEnroll_WithNullDeadline() {
//        // Given
//        EnrollmentRequestInDTO requestWithNullDeadline = new EnrollmentRequestInDTO();
//        requestWithNullDeadline.setUserIds(List.of(4L));
//        requestWithNullDeadline.setCourseIds(List.of(100L));
//        requestWithNullDeadline.setAssignedBy(3L);
//        requestWithNullDeadline.setStatus("ACTIVE");
//        requestWithNullDeadline.setDeadline(null); // Null deadline
//
//        setupValidationMocks();
//        when(userRepository.existsById(3L)).thenReturn(true);
//        when(enrollmentRepository.existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
//                anyLong(), isNull(), anyLong(), isNull(), eq("INDIVIDUAL"), eq(true)))
//                .thenReturn(false);
//        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);
//
//        // When
//        List<EnrollmentOutDTO> result = enrollmentService.enroll(requestWithNullDeadline);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(enrollmentRepository).save(argThat(enrollment -> enrollment.getDeadline() == null));
//    }

    @Test
    void testGetEnrollmentStats_RecentActivityCalculation() {
        // Given
        List<Enrollment> enrollments = new ArrayList<>();

        // Recent enrollment (within 30 days)
        Enrollment recentEnrollment = createEnrollment(1L, null, 100L, null, "COMPLETED", "INDIVIDUAL",
                true, LocalDateTime.now().minusDays(15));
        recentEnrollment.setCompletedAt(LocalDateTime.now().minusDays(10)); // Recent completion
        enrollments.add(recentEnrollment);

        // Old enrollment (older than 30 days)
        Enrollment oldEnrollment = createEnrollment(2L, null, 101L, null, "COMPLETED", "INDIVIDUAL",
                true, LocalDateTime.now().minusDays(45));
        oldEnrollment.setCompletedAt(LocalDateTime.now().minusDays(40)); // Old completion
        enrollments.add(oldEnrollment);

        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // When
        EnrollmentDashBoardStatsOutDTO result = enrollmentService.getEnrollmentStats();

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getRecentEnrollments()); // Only one recent enrollment
        assertEquals(1L, result.getRecentCompletions()); // Only one recent completion
    }

    // Test for error handling in microservice communication
    @Test
    void testGetUserEnrollmentsByUserID_MicroserviceFailure() {
        // Given
        Long userId = 4L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(createUserEnrollments());

        // Mock microservice failures
        when(courseMicroserviceClient.getCourseNameById(anyLong()))
                .thenThrow(new RuntimeException("Microservice down"));
        when(courseMicroserviceClient.getBundleNameById(anyLong()))
                .thenThrow(new RuntimeException("Microservice down"));
        when(courseMicroserviceClient.getCourseProgressWithMeta(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Microservice down"));

        // When
        UserEnrollmentsOutDTO result = enrollmentService.getUserEnrollmentsByUserID(4L);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getUserId());
        // Verify that the service handles failures gracefully with default values
        assertNotNull(result.getEnrolledCoursesList());
        assertNotNull(result.getEnrolledBundlesList());

        // Course names should default to "Unknown Course"
        result.getEnrolledCoursesList().forEach(course ->
                assertEquals("Unknown Course", course.getCourseName()));

        // Progress should default to 0.0
        result.getEnrolledCoursesList().forEach(course ->
                assertEquals(0.0f, course.getProgress(), 0.001f));
    }

}