package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.constants.UserConstants;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.AdminDashboardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.Role;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.InvalidRequestException;
import com.nt.user_service_lms.exception.ResourceConflictException;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.RoleRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.serviceImpl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private CourseMicroserviceClient courseMicroserviceClient;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDTOConverter userDTOConverter;

    @InjectMocks private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterDto dto = new RegisterDto("John", "Doe", "john123", "john@example.com", "P@ssword1", 2L);
        when(userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUserNameIgnoreCase(dto.getUserName())).thenReturn(Optional.empty());
        when(roleRepository.findById(dto.getRoleId())).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");

        StandardResponseOutDTO<MessageOutDTO> response = adminService.register(dto);
        assertEquals("User Registration Successfully", response.getMessage());
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        RegisterDto dto = new RegisterDto("John", "Doe", "john123", "john@example.com", "P@ssword1", 2L);
        when(userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(ResourceConflictException.class, () -> adminService.register(dto));
    }

    @Test
    void testRegisterUsernameAlreadyExists() {
        RegisterDto dto = new RegisterDto("John", "Doe", "john123", "john@example.com", "P@ssword1", 2L);
        when(userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUserNameIgnoreCase(dto.getUserName())).thenReturn(Optional.of(new User()));

        assertThrows(ResourceConflictException.class, () -> adminService.register(dto));
    }

    @Test
    void testRegisterInvalidRole() {
        RegisterDto dto = new RegisterDto("John", "Doe", "john123", "john@example.com", "P@ssword1", 2L);
        when(userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUserNameIgnoreCase(dto.getUserName())).thenReturn(Optional.empty());
        when(roleRepository.findById(dto.getRoleId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.register(dto));
    }

    @Test
    void testRegisterAdminRole() {
        RegisterDto dto = new RegisterDto("John", "Doe", "john123", "john@example.com", "P@ssword1", 1L);
        when(userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUserNameIgnoreCase(dto.getUserName())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.register(dto));
    }

    @Test
    void testEmployeeDeletionForEmployee() {
        User user = new User();
        user.setUserId(2L);
        user.setRoleId(2L);
        user.setActive(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(new Role(2L , "EMPLOYEE")));

        StandardResponseOutDTO<MessageOutDTO> response = adminService.employeeDeletion(2L);
        assertEquals("User deleted successfully", response.getData().getMessage());
    }

    @Test
    void testEmployeeDeletionForManager() {
        User user = new User();
        user.setUserId(2L);
        user.setRoleId(3L);
        user.setActive(true);

        User subordinate = new User();
        subordinate.setUserId(4L);
        subordinate.setManagerId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(new Role(3L, "MANAGER")));
        when(userRepository.findByManagerId(2L)).thenReturn(Arrays.asList(subordinate));

        StandardResponseOutDTO<MessageOutDTO> response = adminService.employeeDeletion(2L);
        assertEquals("User deleted successfully", response.getData().getMessage());
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void testEmployeeDeletionUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.employeeDeletion(2L));
    }

    @Test
    void testEmployeeDeletionRoleNotFound() {
        User user = new User();
        user.setUserId(2L);
        user.setRoleId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> adminService.employeeDeletion(2L));
    }

    @Test
    void testEmployeeDeletionInvalidRole() {
        User user = new User();
        user.setUserId(2L);
        user.setRoleId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(new Role(2L, "INVALID")));

        assertThrows(IllegalStateException.class, () -> adminService.employeeDeletion(2L));
    }

    @Test
    void testEmployeeDeletionAdminId() {
        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(InvalidRequestException.class, () -> adminService.employeeDeletion(1L));
        }
    }

    @Test
    void testGetAllActiveUsers() {
        User user = new User();
        user.setActive(true);
        user.setUserId(2L);
        user.setManagerId(3L);
        user.setRoleId(3L);

        User manager = new User();
        manager.setActive(true);
        manager.setUserId(3L);
        manager.setFirstName("Manager");
        manager.setLastName("First");
        manager.setRoleId(2L);

        Role role = new Role();
        role.setRoleId(3L);
        role.setName("Employee");

        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(role));
        when(userDTOConverter.userToOutDto(any(), any(), any())).thenReturn(new UserOutDTO());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            var response = adminService.getAllActiveUsers();
            assertEquals("User fetched Successfully", response.getMessage());
        }
    }

    @Test
    void testGetAllActiveUsersEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        var response = adminService.getAllActiveUsers();
        assertEquals("No user found", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetAllActiveUsersManagerNotFound() {
        User user = new User();
        user.setActive(true);
        user.setUserId(2L);
        user.setManagerId(3L);
        user.setRoleId(3L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(ResourceNotFoundException.class, () -> adminService.getAllActiveUsers());
        }
    }

    @Test
    void testGetAllActiveUsersRoleNotFound() {
        User user = new User();
        user.setActive(true);
        user.setUserId(2L);
        user.setManagerId(3L);
        user.setRoleId(3L);

        User manager = new User();
        manager.setUserId(3L);
        manager.setFirstName("Manager");
        manager.setLastName("First");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(roleRepository.findById(3L)).thenReturn(Optional.empty());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(ResourceNotFoundException.class, () -> adminService.getAllActiveUsers());
        }
    }

    @Test
    void testGetAllActiveUsersException() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> adminService.getAllActiveUsers());
    }

    @Test
    void testGetAllInactiveUsers() {
        User user = new User();
        user.setActive(false);
        user.setUserId(2L);
        user.setManagerId(3L);
        user.setRoleId(3L);

        User manager = new User();
        manager.setUserId(3L);
        manager.setFirstName("Manager");
        manager.setLastName("First");

        Role role = new Role();
        role.setRoleId(3L);
        role.setName("Employee");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(role));
        when(userDTOConverter.userToOutDto(any(), any(), any())).thenReturn(new UserOutDTO());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            var response = adminService.getAllInactiveUsers();
            assertEquals("User fetched Successfully", response.getMessage());
        }
    }

    @Test
    void testGetAllInactiveUsersEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        var response = adminService.getAllInactiveUsers();
        assertEquals("User does not exist", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetAllInactiveUsersManagerNotFound() {
        User user = new User();
        user.setActive(false);
        user.setUserId(2L);
        user.setManagerId(3L);
        user.setRoleId(3L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(ResourceNotFoundException.class, () -> adminService.getAllInactiveUsers());
        }
    }

    @Test
    void testGetAllInactiveUsersException() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> adminService.getAllInactiveUsers());
    }

    @Test
    void testChangeUserRole() {
        User user = new User();
        user.setUserId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("manager")).thenReturn(Optional.of(new Role(2L, "manager")));

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            var response = adminService.changeUserRole(2L, "manager");
            assertEquals("Updated", response.getMessage());
        }
    }

    @Test
    void testChangeUserRoleUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(ResourceNotFoundException.class, () -> adminService.changeUserRole(2L, "manager"));
        }
    }

    @Test
    void testChangeUserRoleInvalidRole() {
        User user = new User();
        user.setUserId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("invalid")).thenReturn(Optional.empty());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(IllegalArgumentException.class, () -> adminService.changeUserRole(2L, "invalid"));
        }
    }

    @Test
    void testChangeUserRoleAdminId() {
        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(InvalidRequestException.class, () -> adminService.changeUserRole(1L, "manager"));
        }
    }

    @Test
    void testChangeUserRoleException() {
        when(userRepository.findById(2L)).thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(RuntimeException.class, () -> adminService.changeUserRole(2L, "manager"));
        }
    }

    @Test
    void testGetManagerEmployee() {
        User user = new User();
        user.setUserId(3L);
        User manager = new User();
        manager.setUserId(2L);
        manager.setFirstName("Manager");
        manager.setLastName("Name");

        when(userRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(2L)).thenReturn(Arrays.asList(user));
        when(userDTOConverter.userToOutDto(any(), any(), any())).thenReturn(new UserOutDTO());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            var response = adminService.getManagerEmployee(2L);
            assertEquals("Successfully fetched employee for Manager", response.getMessage());
        }
    }

    @Test
    void testGetManagerEmployeeManagerNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(ResourceNotFoundException.class, () -> adminService.getManagerEmployee(2L));
        }
    }

    @Test
    void testGetManagerEmployeeNoEmployees() {
        User manager = new User();
        manager.setUserId(2L);
        manager.setFirstName("Manager");
        manager.setLastName("Name");

        when(userRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(2L)).thenReturn(Collections.emptyList());

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            var response = adminService.getManagerEmployee(2L);
            assertEquals("User not found", response.getMessage());
            assertTrue(response.getData().isEmpty());
        }
    }

    @Test
    void testGetManagerEmployeeAdminId() {
        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(InvalidRequestException.class, () -> adminService.getManagerEmployee(1L));
        }
    }

    @Test
    void testGetManagerEmployeeException() {
        when(userRepository.findById(2L)).thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(RuntimeException.class, () -> adminService.getManagerEmployee(2L));
        }
    }

    @Test
    void testGetAdminStats() {
        AdminDashboardStatsOutDTO stats = new AdminDashboardStatsOutDTO();
        when(userRepository.getAdminDashboardStats()).thenReturn(stats);

        var response = adminService.getAdminStats();
        assertEquals("Dashboard data fetched.", response.getMessage());
        assertEquals(stats, response.getData());
    }

    @Test
    void testGetAdminStatsException() {
        when(userRepository.getAdminDashboardStats()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> adminService.getAdminStats());
    }

    @Test
    void testUpdateUserDetails() {
        User user = new User();
        user.setUserId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("employee")).thenReturn(Optional.of(new Role(2L, "employee")));

        UserInDTO dto = new UserInDTO(2L,"John", "Doe", "johnny", "john@example.com", "employee");

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            MessageOutDTO result = adminService.updateUserDetails(dto, 2L);
            assertEquals(UserConstants.USER_UPDATED_SUCCESSFULLY, result.getMessage());
        }
    }

    @Test
    void testUpdateUserDetailsUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        UserInDTO dto = new UserInDTO(2L,"John", "Doe", "johnny", "john@example.com", "employee");

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(ResourceNotFoundException.class, () -> adminService.updateUserDetails(dto, 2L));
        }
    }

    @Test
    void testUpdateUserDetailsEmptyFields() {
        User user = new User();
        user.setUserId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        UserInDTO dto = new UserInDTO(2L,"", "", "", "", null);

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            MessageOutDTO result = adminService.updateUserDetails(dto, 2L);
            assertEquals(UserConstants.USER_UPDATED_SUCCESSFULLY, result.getMessage());
        }
    }

    @Test
    void testUpdateUserDetailsAdminId() {
        UserInDTO dto = new UserInDTO(1L,"John", "Doe", "johnny", "john@example.com", "employee");

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(InvalidRequestException.class, () -> adminService.updateUserDetails(dto, 1L));
        }
    }

    @Test
    void testUpdateUserDetailsException() {
        when(userRepository.findById(2L)).thenThrow(new RuntimeException("Database error"));
        UserInDTO dto = new UserInDTO(2L,"John", "Doe", "johnny", "john@example.com", "employee");

        try (MockedStatic<UserConstants> userConstants = mockStatic(UserConstants.class)) {
            userConstants.when(UserConstants::getAdminId).thenReturn(1L);

            assertThrows(RuntimeException.class, () -> adminService.updateUserDetails(dto, 2L));
        }
    }

    @Test
    void testDeleteBundle() {
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByBundleId(1L)).thenReturn(Arrays.asList(enrollment));
        when(courseMicroserviceClient.deleteBundle(1L)).thenReturn(null);

        var result = adminService.deleteBundle(1L);
        assertEquals("Bundle Deleted", result.getMessage());
        verify(enrollmentRepository).save(enrollment);
        assertFalse(enrollment.getActive());
    }

    @Test
    void testDeleteBundleException() {
        when(enrollmentRepository.findByBundleId(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> adminService.deleteBundle(1L));
    }

    @Test
    void testRemoveCourseFromBundle() {
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByBundleIdAndCourseId(1L, 2L)).thenReturn(Arrays.asList(enrollment));

        MessageOutDTO message = new MessageOutDTO("Removed");
        when(courseMicroserviceClient.removeCourseFromBundle(1L, 2L))
                .thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success(message, "Removed")));

        var result = adminService.removeCourseFromBundle(1L, 2L);
        assertEquals("Removed", result.getData().getMessage());
        verify(enrollmentRepository).save(enrollment);
        assertFalse(enrollment.getActive());
    }
}