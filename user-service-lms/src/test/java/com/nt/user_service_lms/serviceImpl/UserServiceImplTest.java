package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.CourseDeadlinesDTO;
import com.nt.user_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.Role;
import com.nt.user_service_lms.entities.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoleId(1L);

        Role role = new Role();
        role.setRoleId(1L);
        role.setName("ROLE_USER");

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmailIgnoreCase("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.loadUserByUsername("notfound@example.com"));
    }

    @Test
    void testCountActiveUsers() {
        User user1 = new User();
        user1.setUserId(2L);
        user1.setActive(true);
        User user2 = new User();
        user2.setUserId(1L);
        user2.setActive(true);
        User user3 = new User();
        user3.setUserId(3L);
        user3.setActive(false);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));
        long count = userService.countActiveUsers();
        assertEquals(1, count);
    }

//    @Test
//    void testGetRecentUserDetails() {
//        Object[] row = {"John Doe", "john@example.com", "Manager", "CEO", Timestamp.valueOf("2023-07-22 10:00:00")};
//        when(userRepository.fetchRecentUserDetails()).thenReturn(List.of(row));
//
//        List<UsersDetailsViewDTO> result = userService.getRecentUserDetails();
//
//        assertEquals(1, result.size());
//        assertEquals("John Doe", result.get(0).getFullName());
//        assertEquals("john@example.com", result.get(0).getEmail());
//    }

    @Test
    void testUserStatistics() {
        when(enrollmentRepository.getUserTotalEnrollments(1L)).thenReturn(5L);
        when(userGroupRepository.getAllUserGroups(1L)).thenReturn(2L);
        Map<String, Long> stats = userService.userStatistics(1L);
        assertEquals(5L, stats.get("enrollments"));
        assertEquals(2L, stats.get("groups"));
    }

    @Test
    void testDeadlineCourses_Success() {
        User user = new User();
        user.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(101L);
        enrollment.setDeadline(LocalDateTime.now().plusDays(2));

        CourseInfoOutDTO courseInfo = new CourseInfoOutDTO();
        courseInfo.setCourseId(101L);
        courseInfo.setTitle("Java 101");
        courseInfo.setOwnerId(1001L);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment));
        when(courseMicroserviceClient.getCourseById(101L))
                .thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success(courseInfo, null)));

        StandardResponseOutDTO<List<CourseDeadlinesDTO>> response = userService.deadlineCourses("test@example.com");

        assertEquals(1, response.getData().size());
        assertEquals(101L, response.getData().get(0).getCourseId());
    }

    @Test
    void testDeadlineCourses_UserNotFound() {
        when(userRepository.findByEmailIgnoreCase("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.deadlineCourses("notfound@example.com"));
    }

    @Test
    void testGetUserEnrolledCourses() {
        Enrollment e1 = new Enrollment();
        e1.setCourseId(1L);
        e1.setAssignedAt(LocalDateTime.now().minusDays(5));
        e1.setAssignedBy(100L);
        e1.setDeadline(LocalDateTime.now().plusDays(5));

        Enrollment e2 = new Enrollment();
        e2.setCourseId(1L);
        e2.setAssignedAt(LocalDateTime.now());
        e2.setAssignedBy(101L);
        e2.setDeadline(LocalDateTime.now().plusDays(10));

        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(List.of(e1, e2));

        List<UserCourseEnrollDetails> results = userService.getUserEnrolledCourses(1L);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getCourseId());
        assertEquals(100L, results.get(0).getAssignedById());
    }
}
