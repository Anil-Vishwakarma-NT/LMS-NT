package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.constants.UserConstants;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.Role;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.RoleRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.serviceImpl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseMicroserviceClient courseMicroserviceClient;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDTOConverter userDTOConverter;

    @InjectMocks
    private AdminServiceImpl adminService;

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
    void testEmployeeDeletionForEmployee() {
        User user = new User();
        user.setUserId(2L);
        user.setRoleId(2L);
        user.setActive(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(new Role(2L, "EMPLOYEE")));

        StandardResponseOutDTO<MessageOutDTO> response = adminService.employeeDeletion(2L);
        assertEquals("User deleted successfully", response.getData().getMessage());
    }

//    @Test
//    void testGetAllActiveUsers() {
//        User user = new User();
//        user.setActive(true);
//        user.setUserId(2L);
//        user.setManagerId(3L);
//        user.setRoleId(3L);
//
//        User manager = new User();
//        manager.setActive(true);
//        manager.setUserId(3L);
//        manager.setFirstName("Manager");
//        manager.setLastName("first");
//        manager.setRoleId(2L);
//
//
//        Role role = new Role();
//        role.setRoleId(3L);
//        role.setName("Employee");
//
//        List<User> users = List.of(user);
//        when(userRepository.findAll()).thenReturn(users);
//        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
//        when(roleRepository.findById(3L)).thenReturn(Optional.of(role));
//        when(userDTOConverter.userToOutDto(any(), any(), any())).thenReturn(new UserOutDTO());
//
//         var response = adminService.getAllActiveUsers();
//        assertEquals("User fetched Successfully", response.getMessage());
//    }
////
//    @Test
//    void testChangeUserRole() {
//        User user = new User();
//        user.setUserId(2L);
//        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
//        when(roleRepository.findByName("manager")).thenReturn(Optional.of(new Role(2L, "manager")));
//
//        var response = adminService.changeUserRole(2L, "manager");
//        assertEquals("Updated", response.getMessage());
//    }

    @Test
    void testGetManagerEmployee() {
        User user = new User();
        user.setUserId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User()));
        when(userRepository.findByManagerId(3L)).thenReturn(List.of(user));
        when(userDTOConverter.userToOutDto(any(), any(), any())).thenReturn(new UserOutDTO());

        var response = adminService.getManagerEmployee(3L);
        assertEquals("Successfully fetched employee for Manager", response.getMessage());
    }

    @Test
    void testUpdateUserDetails() {
        User user = new User();
        user.setUserId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("employee")).thenReturn(Optional.of(new Role(2L, "employee")));

        UserInDTO dto = new UserInDTO(2L, "John", "Doe", "johnny", "john@example.com", "employee");
        MessageOutDTO result = adminService.updateUserDetails(dto, 2L);
        assertEquals(UserConstants.USER_UPDATED_SUCCESSFULLY, result.getMessage());
    }

    @Test
    void testDeleteBundle() {
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByBundleId(1L)).thenReturn(List.of(enrollment));
        when(courseMicroserviceClient.deleteBundle(1L)).thenReturn(null);

        var result = adminService.deleteBundle(1L);
        assertEquals("Bundle Deleted", result.getMessage());
    }

    @Test
    void testRemoveCourseFromBundle() {
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByBundleIdAndCourseId(1L, 2L)).thenReturn(List.of(enrollment));

        MessageOutDTO message = new MessageOutDTO("Removed");
        when(courseMicroserviceClient.removeCourseFromBundle(1L, 2L)).thenReturn(ResponseEntity.ok(StandardResponseOutDTO.success(message, "Removed")));

        var result = adminService.removeCourseFromBundle(1L, 2L);
        assertEquals("Removed", result.getData().getMessage());
    }
}
