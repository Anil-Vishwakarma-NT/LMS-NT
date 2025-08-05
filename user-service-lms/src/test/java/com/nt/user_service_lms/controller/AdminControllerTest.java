package com.nt.user_service_lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.service.AdminService;
import com.nt.user_service_lms.service.GroupService;
import com.nt.user_service_lms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AdminService adminService;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ServicePrincipal servicePrincipal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        // Configure ObjectMapper to handle LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Create a mock ServicePrincipal
        servicePrincipal = new ServicePrincipal.Builder()
                .serviceId("test-service")
                .userId("1")
                .userEmail("admin@test.com")
                .userFullName("Test Admin")
                .originalTokenType("USER")
                .build();

        // Set up security context with ADMIN role
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        servicePrincipal,
                        null,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setEmail("john.doe@test.com");
        registerDto.setUserName("johndoe");
        registerDto.setPassword("password123");
        registerDto.setRoleId(2L);

        MessageOutDTO messageOutDTO = new MessageOutDTO("User registered successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, "User Registration Successfully");

        when(adminService.register(any(RegisterDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/service-api/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User Registration Successfully"));
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        // Arrange
        Long userId = 5L;
        MessageOutDTO messageOutDTO = new MessageOutDTO("User deleted successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, null);

        when(adminService.employeeDeletion(userId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/service-api/admin/remove-user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetAllEmployees_Success() throws Exception {
        // Arrange
        List<UserOutDTO> employees = Arrays.asList(
                createUserOutDTO(2L, "John", "Doe", "john.doe@test.com", "employee", "Admin User"),
                createUserOutDTO(3L, "Jane", "Smith", "jane.smith@test.com", "employee", "Admin User")
        );

        StandardResponseOutDTO<List<UserOutDTO>> response = StandardResponseOutDTO.success(employees, "User fetched Successfully");

        when(adminService.getAllActiveUsers()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data[0].email").value("john.doe@test.com"));
    }

    @Test
    void testGetAllEmployees_NoContent() throws Exception {
        // Arrange
        StandardResponseOutDTO<List<UserOutDTO>> response = StandardResponseOutDTO.success(Collections.emptyList(), "No user found");

        when(adminService.getAllActiveUsers()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetAllInactiveEmployees_Success() throws Exception {
        // Arrange
        List<UserOutDTO> inactiveEmployees = Arrays.asList(
                createUserOutDTO(4L, "Bob", "Wilson", "bob.wilson@test.com", "employee", "Admin User"),
                createUserOutDTO(5L, "Alice", "Johnson", "alice.johnson@test.com", "manager", "Admin User")
        );

        StandardResponseOutDTO<List<UserOutDTO>> response = StandardResponseOutDTO.success(inactiveEmployees, "User fetched Successfully");

        when(adminService.getAllInactiveUsers()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/inactive-employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].firstName").value("Bob"))
                .andExpect(jsonPath("$.data[1].firstName").value("Alice"));
    }

    @Test
    void testGetAllInactiveEmployees_NoContent() throws Exception {
        // Arrange
        StandardResponseOutDTO<List<UserOutDTO>> response = StandardResponseOutDTO.success(Collections.emptyList(), "User does not exist");

        when(adminService.getAllInactiveUsers()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/inactive-employees"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetManagerEmployee_Success() throws Exception {
        // Arrange
        Long managerId = 3L;
        List<UserOutDTO> managerEmployees = Arrays.asList(
                createUserOutDTO(6L, "Tom", "Brown", "tom.brown@test.com", "employee", "Jane Manager"),
                createUserOutDTO(7L, "Sara", "Davis", "sara.davis@test.com", "employee", "Jane Manager")
        );

        StandardResponseOutDTO<List<UserOutDTO>> response = StandardResponseOutDTO.success(managerEmployees, "Successfully fetched employee for Manager");

        when(adminService.getManagerEmployee(managerId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/manager-employee/{userId}", managerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].firstName").value("Tom"))
                .andExpect(jsonPath("$.data[1].firstName").value("Sara"));
    }

    @Test
    void testGetManagerEmployee_NoContent() throws Exception {
        // Arrange
        Long managerId = 3L;
        StandardResponseOutDTO<List<UserOutDTO>> response = StandardResponseOutDTO.success(Collections.emptyList(), "No employees found");

        when(adminService.getManagerEmployee(managerId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/manager-employee/{userId}", managerId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testChangeRole_Success() throws Exception {
        // Arrange
        UserInDTO userInDTO = new UserInDTO();
        userInDTO.setUserId(5L);
        userInDTO.setRole("manager");

        MessageOutDTO messageOutDTO = new MessageOutDTO("Role updated successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, "Role updated successfully");

        when(adminService.changeUserRole(anyLong(), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/service-api/admin/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Role updated successfully"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Arrange
        Long userId = 5L;
        UserInDTO userInDTO = new UserInDTO();
        userInDTO.setFirstName("Updated John");
        userInDTO.setLastName("Updated Doe");
        userInDTO.setEmail("updated.john@test.com");
        userInDTO.setUserName("updatedjohn");
        userInDTO.setRole("manager");

        MessageOutDTO messageOutDTO = new MessageOutDTO("User updated successfully");

        when(adminService.updateUserDetails(any(UserInDTO.class), anyLong())).thenReturn(messageOutDTO);

        // Act & Assert
        mockMvc.perform(patch("/api/service-api/admin/update-user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    void testGetTotalUserCount_Success() throws Exception {
        // Arrange
        Long expectedCount = 25L;
        when(userService.countActiveUsers()).thenReturn(expectedCount);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(25))
                .andExpect(jsonPath("$.message").value("Fetched User Count"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetRecentUsers_Success() throws Exception {
        // Arrange
        List<UsersDetailsViewDTO> recentUsers = Arrays.asList(
                createUsersDetailsViewDTO(8L, "Recent", "User1", "recent1@test.com", "employee"),
                createUsersDetailsViewDTO(9L, "Recent", "User2", "recent2@test.com", "manager")
        );

        when(userService.getRecentUserDetails()).thenReturn(recentUsers);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/users/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Recent Users"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].fullName").value("RecentUser1"));
    }

    @Test
    void testDeleteBundle_Success() throws Exception {
        // Arrange
        Long bundleId = 101L;
        MessageOutDTO messageOutDTO = new MessageOutDTO("Bundle Deleted");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, "Bundle Deleted");

        when(adminService.deleteBundle(bundleId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/service-api/admin/bundle")
                        .param("bundleId", bundleId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bundle Deleted"));
    }

    @Test
    void testRemoveCourseFromBundle_Success() throws Exception {
        // Arrange
        Long bundleId = 101L;
        Long courseId = 201L;
        MessageOutDTO messageOutDTO = new MessageOutDTO("Course removed from bundle successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, "Course removed successfully");

        when(adminService.removeCourseFromBundle(bundleId, courseId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/service-api/admin/bundle/removecourse")
                        .param("bundleId", bundleId.toString())
                        .param("courseId", courseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course removed successfully"));
    }

    @Test
    void testGetEnrolledCoursesByUserId_Success() throws Exception {
        // Arrange
        Long userId = 5L;
        List<UserCourseEnrollDetails> enrolledCourses = Arrays.asList(
                createUserCourseEnrollDetails(101L, "Java Fundamentals", 85.5, "Completed"),
                createUserCourseEnrollDetails(102L, "Spring Boot", 72.0, "In Progress")
        );

        when(userService.getUserEnrolledCourses(userId)).thenReturn(enrolledCourses);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/userCourses/{userId}", userId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched enrolled courses successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetAdminDashboardStats_Success() throws Exception {
        // Arrange
        AdminDashboardStatsOutDTO dashboardStats = createAdminDashboardStatsOutDTO();
        StandardResponseOutDTO<AdminDashboardStatsOutDTO> response = StandardResponseOutDTO.success(dashboardStats, "Dashboard data fetched.");

        when(adminService.getAdminStats()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/admin/admin-dashboard-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Dashboard data fetched."))
                .andExpect(jsonPath("$.data").exists());
    }

    // Test for validation failures
    @Test
    void testRegister_ValidationFailure() throws Exception {
        // Arrange - RegisterDto with missing required fields
        RegisterDto registerDto = new RegisterDto();
        // Missing required fields like email, firstName, etc.

        // Act & Assert
        mockMvc.perform(post("/api/service-api/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangeRole_ValidationFailure() throws Exception {
        // Arrange - UserInDTO with missing required fields
        UserInDTO userInDTO = new UserInDTO();
        // Missing userId and role

        // Act & Assert
        mockMvc.perform(post("/api/service-api/admin/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO)))
                .andExpect(status().isBadRequest());
    }

    // Helper methods to create DTOs for testing
    private UserOutDTO createUserOutDTO(Long userId, String firstName, String lastName, String email, String role, String managerName) {
        UserOutDTO dto = new UserOutDTO();
        dto.setUserId(userId);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setRole(role);
        dto.setManager(managerName);
        return dto;
    }

    private UsersDetailsViewDTO createUsersDetailsViewDTO(Long userId, String firstName, String lastName, String email, String role) {
        UsersDetailsViewDTO dto = new UsersDetailsViewDTO();
        dto.setUserId(userId);
        dto.setFullName(firstName + lastName);
        dto.setEmail(email);
        dto.setRole(role);
        return dto;
    }

    private UserCourseEnrollDetails createUserCourseEnrollDetails(Long courseId, String courseName, Double progress, String status) {
        UserCourseEnrollDetails dto = new UserCourseEnrollDetails();
        dto.setCourseId(courseId);
        dto.setAssignedById(1L);
        return dto;
    }

    private AdminDashboardStatsOutDTO createAdminDashboardStatsOutDTO() {
        AdminDashboardStatsOutDTO dto = new AdminDashboardStatsOutDTO();
        dto.setUserCount(50L);
        dto.setCourseCount(45L);
        dto.setGroupCount(5L);
        dto.setBundleCount(20L);
        dto.setTotalEnrollments(150L);
        return dto;
    }
}