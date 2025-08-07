package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.constants.CommonConstants;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.Role;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.RoleRepository;
import com.nt.user_service_lms.repository.UserGroupRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.nt.user_service_lms.constants.CommonConstants.NUMBER_FIVE;
import static com.nt.user_service_lms.constants.UserConstants.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private CourseMicroserviceClient courseMicroserviceClient;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoleId(1L);

        Role role = new Role();
        role.setRoleId(1L);
        role.setName("ROLE_USER");

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // When
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        // Then
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByEmailIgnoreCase("notfound@example.com")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("notfound@example.com"));
        assertTrue(exception.getMessage().contains(USER_NOT_FOUND));
        assertTrue(exception.getMessage().contains("notfound@example.com"));
    }

    @Test
    void testCountActiveUsers() {
        // Given
        User user1 = new User();
        user1.setUserId(2L);
        user1.setActive(true);

        User user2 = new User();
        user2.setUserId(1L);
        user2.setActive(true); // This should be excluded (userId == 1)

        User user3 = new User();
        user3.setUserId(3L);
        user3.setActive(false); // This should be excluded (inactive)

        User user4 = new User();
        user4.setUserId(4L);
        user4.setActive(true);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4));

        // When
        long count = userService.countActiveUsers();

        // Then
        assertEquals(2, count); // Only user1 and user4 should be counted
    }

    @Test
    void testCountActiveUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        long count = userService.countActiveUsers();

        // Then
        assertEquals(0, count);
    }

    @Test
    void testGetRecentUserDetails() {
        // Given
        Object[] row1 = {1L, "John Doe", "john@example.com", "Manager", "CEO", Timestamp.valueOf("2023-07-22 10:00:00")};
        Object[] row2 = {2L, "Jane Smith", "jane@example.com", "Developer", "John Doe", Timestamp.valueOf("2023-07-23 11:00:00")};

        when(userRepository.fetchRecentUserDetails()).thenReturn(List.of(row1, row2));

        // When
        List<UsersDetailsViewDTO> result = userService.getRecentUserDetails();

        // Then
        assertEquals(2, result.size());

        UsersDetailsViewDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getUserId());
        assertEquals("John Doe", dto1.getFullName());
        assertEquals("john@example.com", dto1.getEmail());
        assertEquals("Manager", dto1.getRole());
        assertEquals(Timestamp.valueOf("2023-07-22 10:00:00"), dto1.getCreatedAt());

        UsersDetailsViewDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getUserId());
        assertEquals("Jane Smith", dto2.getFullName());
        assertEquals("jane@example.com", dto2.getEmail());
        assertEquals("Developer", dto2.getRole());
        assertEquals(Timestamp.valueOf("2023-07-23 11:00:00"), dto2.getCreatedAt());
    }

    @Test
    void testGetRecentUserDetails_EmptyList() {
        // Given
        when(userRepository.fetchRecentUserDetails()).thenReturn(Collections.emptyList());

        // When
        List<UsersDetailsViewDTO> result = userService.getRecentUserDetails();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testUserStatistics() {
        // Given
        when(enrollmentRepository.getUserTotalEnrollments(1L)).thenReturn(5L);
        when(userGroupRepository.getAllUserGroups(1L)).thenReturn(2L);

        // When
        Map<String, Long> stats = userService.userStatistics(1L);

        // Then
        assertEquals(5L, stats.get("enrollments"));
        assertEquals(2L, stats.get("groups"));
        assertEquals(2, stats.size());
    }

    @Test
    void testUserStatistics_ZeroValues() {
        // Given
        when(enrollmentRepository.getUserTotalEnrollments(1L)).thenReturn(0L);
        when(userGroupRepository.getAllUserGroups(1L)).thenReturn(0L);

        // When
        Map<String, Long> stats = userService.userStatistics(1L);

        // Then
        assertEquals(0L, stats.get("enrollments"));
        assertEquals(0L, stats.get("groups"));
    }

    @Test
    void testDeadlineCourses_Success() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setCourseId(101L);
        enrollment1.setDeadline(LocalDateTime.now().plusDays(2)); // Within 5 days

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setCourseId(102L);
        enrollment2.setDeadline(LocalDateTime.now().plusDays(10)); // Outside 5 days

        CourseInfoOutDTO courseInfo = new CourseInfoOutDTO();
        courseInfo.setCourseId(101L);
        courseInfo.setTitle("Java 101");
        courseInfo.setOwnerId(1001L);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment1, enrollment2));
        when(courseMicroserviceClient.getCourseById(101L))
                .thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success(courseInfo, null)));

        // When
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response = userService.deadlineCourses("test@example.com");

        // Then
        assertTrue(response.getStatus()=="SUCCESS");
        assertEquals(1, response.getData().size());
        assertEquals(101L, response.getData().get(0).getCourseId());
        assertEquals("Java 101", response.getData().get(0).getTitle());
        assertEquals(1001L, response.getData().get(0).getOwnerId());
        assertNotNull(response.getData().get(0).getDeadline());
    }

    @Test
    void testDeadlineCourses_UserNotFound() {
        // Given
        when(userRepository.findByEmailIgnoreCase("notfound@example.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deadlineCourses("notfound@example.com"));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void testDeadlineCourses_NoEnrollments() {
        // Given
        User user = new User();
        user.setUserId(1L);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // When
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response = userService.deadlineCourses("test@example.com");

        // Then
        assertTrue(response.getStatus()=="SUCCESS");
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testDeadlineCourses_EnrollmentsWithNullDeadlines() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setCourseId(101L);
        enrollment1.setDeadline(null); // Null deadline

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setCourseId(102L);
        enrollment2.setDeadline(LocalDateTime.now().minusDays(1)); // Past deadline

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment1, enrollment2));

        // When
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response = userService.deadlineCourses("test@example.com");

        // Then
        assertTrue(response.getStatus()=="SUCCESS");
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testDeadlineCourses_ExceptionInCourseServiceCall() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(101L);
        enrollment.setDeadline(LocalDateTime.now().plusDays(2));

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment));
        when(courseMicroserviceClient.getCourseById(101L)).thenThrow(new RuntimeException("Service unavailable"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deadlineCourses("test@example.com"));
        assertEquals("Service unavailable", exception.getCause().getMessage());
    }

    @Test
    void testGetUserEnrolledCourses() {
        // Given
        Enrollment e1 = new Enrollment();
        e1.setCourseId(1L);
        e1.setAssignedAt(LocalDateTime.now().minusDays(5)); // Earlier
        e1.setAssignedBy(100L);
        e1.setDeadline(LocalDateTime.now().plusDays(5));

        Enrollment e2 = new Enrollment();
        e2.setCourseId(1L);
        e2.setAssignedAt(LocalDateTime.now()); // Later
        e2.setAssignedBy(101L);
        e2.setDeadline(LocalDateTime.now().plusDays(10));

        Enrollment e3 = new Enrollment();
        e3.setCourseId(2L);
        e3.setAssignedAt(LocalDateTime.now().minusDays(2));
        e3.setAssignedBy(102L);
        e3.setDeadline(LocalDateTime.now().plusDays(7));

        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(List.of(e1, e2, e3));

        // When
        List<UserCourseEnrollDetails> results = userService.getUserEnrolledCourses(1L);

        // Then
        assertEquals(2, results.size());

        // Should get the earliest enrollment for course 1 (e1)
        UserCourseEnrollDetails course1Details = results.stream()
                .filter(detail -> detail.getCourseId().equals(1L))
                .findFirst().orElse(null);
        assertNotNull(course1Details);
        assertEquals(100L, course1Details.getAssignedById());

        // Should get the only enrollment for course 2 (e3)
        UserCourseEnrollDetails course2Details = results.stream()
                .filter(detail -> detail.getCourseId().equals(2L))
                .findFirst().orElse(null);
        assertNotNull(course2Details);
        assertEquals(102L, course2Details.getAssignedById());
    }

    @Test
    void testGetUserEnrolledCourses_EmptyList() {
        // Given
        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(Collections.emptyList());

        // When
        List<UserCourseEnrollDetails> results = userService.getUserEnrolledCourses(1L);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetUserEnrolledCourses_EnrollmentsWithNullCourseId() {
        // Given
        Enrollment e1 = new Enrollment();
        e1.setCourseId(null); // Null course ID
        e1.setAssignedAt(LocalDateTime.now());
        e1.setAssignedBy(100L);

        Enrollment e2 = new Enrollment();
        e2.setCourseId(1L);
        e2.setAssignedAt(LocalDateTime.now());
        e2.setAssignedBy(101L);
        e2.setDeadline(LocalDateTime.now().plusDays(5));

        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(List.of(e1, e2));

        // When
        List<UserCourseEnrollDetails> results = userService.getUserEnrolledCourses(1L);

        // Then
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getCourseId());
        assertEquals(101L, results.get(0).getAssignedById());
    }

    @Test
    void testGetUserDetailsByEmail_Success() {
        // Given
        User user = new User();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");

        when(userRepository.findByEmailIgnoreCase("john@example.com")).thenReturn(Optional.of(user));

        // When
        StandardResponseOutDTO<UserOutDTO> response = userService.getUserDetailsByEmail("john@example.com");

        // Then
        assertTrue(response.getStatus()=="SUCCESS");
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getUserId());
        assertEquals("John", response.getData().getFirstName());
        assertEquals("Doe", response.getData().getLastName());
        assertEquals("User details fetched.", response.getMessage());
    }

    @Test
    void testGetUserDetailsByEmail_UserNotFound() {
        // Given
        when(userRepository.findByEmailIgnoreCase("notfound@example.com")).thenReturn(Optional.empty());

        // When
        StandardResponseOutDTO<UserOutDTO> response = userService.getUserDetailsByEmail("notfound@example.com");

        // Then
        assertFalse(response.getStatus()=="SUCCESS");
        assertEquals(USER_NOT_FOUND, response.getMessage());
    }

    @Test
    void testGetUserDetailsByUserId_Success() {
        // Given
        User user = new User();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        StandardResponseOutDTO<UserOutDTO> response = userService.getUserDetailsByUserId(userId);

        // Then
        assertTrue(response.getStatus()=="SUCCESS");
        assertEquals("User details fetched.", response.getMessage());
    }

    @Test
    void testGetUserDetailsByUserId_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        StandardResponseOutDTO<UserOutDTO> response = userService.getUserDetailsByUserId(1L);

        // Then
        assertTrue(response.getStatus()=="SUCCESS"); // Note: The original method returns success even when user not found
        assertNull(response.getData());
        assertEquals(USER_NOT_FOUND, response.getMessage());
    }

    @Test
    void testDeadlineCourses_EdgeCaseTodayDeadline() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(101L);
        enrollment.setDeadline(LocalDate.now().atStartOfDay()); // Today's deadline

        CourseInfoOutDTO courseInfo = new CourseInfoOutDTO();
        courseInfo.setCourseId(101L);
        courseInfo.setTitle("Today Course");
        courseInfo.setOwnerId(1001L);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment));
        when(courseMicroserviceClient.getCourseById(101L))
                .thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success(courseInfo, null)));

        // When
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response = userService.deadlineCourses("test@example.com");

        // Then
        assertTrue(response.getStatus()=="SUCCESS");
        assertEquals(1, response.getData().size());
        assertEquals("Today Course", response.getData().get(0).getTitle());
    }

    @Test
    void testDeadlineCourses_EdgeCaseFifthDayDeadline() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(101L);
        enrollment.setDeadline(LocalDate.now().plusDays(5).atStartOfDay()); // 5th day deadline

        CourseInfoOutDTO courseInfo = new CourseInfoOutDTO();
        courseInfo.setCourseId(101L);
        courseInfo.setTitle("Fifth Day Course");
        courseInfo.setOwnerId(1001L);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment));
        when(courseMicroserviceClient.getCourseById(101L))
                .thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success(courseInfo, null)));

        // When
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response = userService.deadlineCourses("test@example.com");

        // Then
        assertTrue(response.getStatus() == "SUCCESS");
        assertEquals(1, response.getData().size());
        assertEquals("Fifth Day Course", response.getData().get(0).getTitle());
    }
}