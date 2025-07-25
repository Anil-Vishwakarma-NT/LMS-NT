//package com.nt.user_service_lms.serviceImpl;
//
//import com.nt.user_service_lms.constants.CommonConstants;
//import com.nt.user_service_lms.dto.inDTO.EnrollmentRequestInDTO;
//import com.nt.user_service_lms.dto.outDTO.EnrollmentDashBoardStatsOutDTO;
//import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
//import com.nt.user_service_lms.entities.Enrollment;
//import com.nt.user_service_lms.entities.User;
//import com.nt.user_service_lms.exception.ResourceNotFoundException;
//import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
//import com.nt.user_service_lms.repository.EnrollmentRepository;
//import com.nt.user_service_lms.repository.UserRepository;
//import com.nt.user_service_lms.service.serviceImpl.EnrollmentServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class EnrollmentServiceImplTest {
//
//    @Mock
//    private EnrollmentRepository enrollmentRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CourseMicroserviceClient courseMicroserviceClient;
//
//    @InjectMocks
//    private EnrollmentServiceImpl enrollmentService;
//
//    private User user;
//    private Enrollment enrollment;
//    /**
//     * Enrollment source constant for individual enrollments.
//     */
//    private static final String ENROLLMENT_SOURCE_INDIVIDUAL = "INDIVIDUAL";
//    /**
//     * Enrollment source constant for group enrollments.
//     */
//    private static final String ENROLLMENT_SOURCE_GROUP = "GROUP";
//    /**
//     * Enrollment source constant for bundle enrollments.
//     */
//    private static final String ENROLLMENT_SOURCE_BUNDLE = "BUNDLE";
//    /**
//     * Enrollment source constant for group-bundle enrollments.
//     */
//    private static final String ENROLLMENT_SOURCE_GROUP_BUNDLE = "GROUP_BUNDLE";
//
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setActive(true);
//
//        enrollment = new Enrollment();
//        enrollment.setUserId(1L);
//        enrollment.setCourseId(100L);
//        enrollment.setAssignedAt(LocalDateTime.now());
//        enrollment.setActive(true);
//    }
//
//    @Test
//    public void testEnroll_ValidUsersAndCourses_ShouldSucceed() {
//        EnrollmentRequestInDTO dto = new EnrollmentRequestInDTO();
//        dto.setUserIds(List.of(1L));
//        dto.setCourseIds(List.of(100L));
////        dto.setAssignedById(10L);
//
//        when(userRepository.findAllById(anyList())).thenReturn(List.of(user));
//        when(courseMicroserviceClient.getExistingCourseIds(anyList())).thenReturn(ResponseEntity.ok(List.of(100L)));
//        when(enrollmentRepository.existsByUserIdAndCourseIdAndIsActiveTrue(anyLong(), anyLong())).thenReturn(false);
//        when(enrollmentRepository.saveAll(anyList())).thenReturn(List.of(enrollment));
//
//        StandardResponseOutDTO<String> response = enrollmentService.enroll(dto);
//
//        assertEquals("Enrolled Successfully", response.getMessage());
//        verify(enrollmentRepository, times(1)).saveAll(anyList());
//    }
//
//    @Test
//    public void testEnroll_CourseNotFound_ShouldThrowException() {
//        EnrollmentRequestInDTO dto = new EnrollmentRequestInDTO();
//        dto.setUserIds(List.of(1L));
//        dto.setCourseIds(List.of(100L));
//        dto.setType(EnrollmentType.COURSE);
//
//        when(userRepository.findAllById(anyList())).thenReturn(List.of(user));
//        when(courseMicroserviceClient.getExistingCourseIds(anyList())).thenReturn(ResponseEntity.ok(Collections.emptyList()));
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> enrollmentService.enroll(dto));
//
//        assertTrue(exception.getMessage().contains("Courses not found"));
//    }
//
//    @Test
//    public void testEnroll_UsersNotFound_ShouldThrowException() {
//        EnrollmentRequestInDTO dto = new EnrollmentRequestInDTO();
//        dto.setUserIds(List.of(1L));
//        dto.setCourseIds(List.of(100L));
//        dto.setType(EnrollmentType.COURSE);
//
//        when(userRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> enrollmentService.enroll(dto));
//
//        assertTrue(exception.getMessage().contains("Users not found"));
//    }
//
//    @Test
//    public void testGetUserEnrollmentsByUserID_ShouldReturnActiveEnrollments() {
//        when(enrollmentRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(List.of(enrollment));
//
//        assertEquals(1, enrollmentService.getUserEnrollmentsByUserID(1L).size());
//    }
//
//    @Test
//    public void testGetEnrollmentStats_ShouldReturnCount() {
//        when(enrollmentRepository.countByStatus(Status.ACTIVE)).thenReturn(10L);
//
//        EnrollmentDashBoardStatsOutDTO stats = enrollmentService.getEnrollmentStats();
//
//        assertEquals(10L, stats.getTotalActiveEnrollments());
//    }
//
//    @Test
//    public void testEnroll_AlreadyExists_ShouldSkip() {
//        EnrollmentRequestInDTO dto = new EnrollmentRequestInDTO();
//        dto.setUserIds(List.of(1L));
//        dto.setCourseIds(List.of(100L));
//        dto.setType(EnrollmentType.COURSE);
//        dto.setAssignedById(10L);
//
//        when(userRepository.findAllById(anyList())).thenReturn(List.of(user));
//        when(courseMicroserviceClient.getExistingCourseIds(anyList())).thenReturn(ResponseEntity.ok(List.of(100L)));
//        when(enrollmentRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 100L)).thenReturn(true);
//
//        StandardResponseOutDTO<String> response = enrollmentService.enroll(dto);
//
//        assertEquals("Enrolled Successfully", response.getMessage());
//        verify(enrollmentRepository, never()).saveAll(anyList());
//    }
//}
