package com.nt.user_service_lms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.user_service_lms.config.JwtUtil;
import com.nt.user_service_lms.config.TestAuthenticationFilter;
import com.nt.user_service_lms.config.TestSecurityConfig;
import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.AdminDashboardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.dto.outDTO.UsersDetailsViewDTO;
import com.nt.user_service_lms.service.AdminService;
import com.nt.user_service_lms.service.GroupService;
import com.nt.user_service_lms.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private TestAuthenticationFilter testAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private RegisterDto registerDto;
    private UserInDTO userInDTO;
    private UserOutDTO userOutDTO;
    private StandardResponseOutDTO<MessageOutDTO> successResponse;
    private StandardResponseOutDTO<List<UserOutDTO>> userListResponse;
    private UsersDetailsViewDTO userDetailsViewDTO;
    private AdminDashboardStatsOutDTO adminDashboardStats;
    private UserCourseEnrollDetails userCourseEnrollDetails;

    @BeforeEach
    void setUp() throws Exception {
        // Configure the mocked filter to DO NOTHING but continue the chain
        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(testAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        // Initialize test data
        registerDto = RegisterDto.builder()
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .email("john.doe@nucleusteq.com")
                .password("Password@123")
                .roleId(2L)
                .build();

        userInDTO = new UserInDTO();
        userInDTO.setUserId(1L);
        userInDTO.setFirstName("John");
        userInDTO.setLastName("Doe");
        userInDTO.setUserName("johndoe");
        userInDTO.setEmail("john.doe@nucleusteq.com");
        userInDTO.setRole("employee");

        userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(1L);
        userOutDTO.setFirstName("John");
        userOutDTO.setLastName("Doe");
        userOutDTO.setUsername("johndoe");
        userOutDTO.setEmail("john.doe@nucleusteq.com");
        userOutDTO.setManager("Admin User");
        userOutDTO.setRole("employee");

        MessageOutDTO messageOutDTO = new MessageOutDTO("Operation successful");
        successResponse = StandardResponseOutDTO.success(messageOutDTO, "Success");

        userListResponse = StandardResponseOutDTO.success(Arrays.asList(userOutDTO), "Users fetched successfully");

        userDetailsViewDTO = new UsersDetailsViewDTO();
        userDetailsViewDTO.setUserId(1L);
        userDetailsViewDTO.setFullName("John Doe");
        userDetailsViewDTO.setEmail("john.doe@nucleusteq.com");
        userDetailsViewDTO.setRole("employee");
        userDetailsViewDTO.setManagerName("Admin User");
        userDetailsViewDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        adminDashboardStats = AdminDashboardStatsOutDTO.builder()
                .userCount(100L)
                .groupCount(10L)
                .courseCount(50L)
                .bundleCount(5L)
                .totalEnrollments(200L)
                .build();

        userCourseEnrollDetails = new UserCourseEnrollDetails();
        userCourseEnrollDetails.setCourseId(1L);
        userCourseEnrollDetails.setAssignedById(1L);
        userCourseEnrollDetails.setEnrollmentDate(LocalDateTime.now());
        userCourseEnrollDetails.setDeadline(LocalDateTime.now().plusDays(30));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_ShouldReturnCreated_WhenValidInput() throws Exception {
        // Given
        when(adminService.register(any(RegisterDto.class))).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/service-api/admin/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(adminService).register(any(RegisterDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid register DTO with empty first name
        RegisterDto invalidRegisterDto = RegisterDto.builder()
                .firstName("")
                .lastName("Doe")
                .userName("johndoe")
                .email("john.doe@nucleusteq.com")
                .password("Password@123")
                .roleId(2L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/admin/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegisterDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void register_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/admin/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_ShouldReturnOk_WhenUserExists() throws Exception {
        // Given
        when(adminService.employeeDeletion(1L)).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/service-api/admin/remove-user/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminService).employeeDeletion(1L);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteEmployee_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/admin/remove-user/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployees_ShouldReturnOk_WhenEmployeesExist() throws Exception {
        // Given
        when(adminService.getAllActiveUsers()).thenReturn(userListResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(1L))
                .andExpect(jsonPath("$.data[0].firstName").value("John"));

        verify(adminService).getAllActiveUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployees_ShouldReturnNoContent_WhenNoEmployeesExist() throws Exception {
        // Given
        StandardResponseOutDTO<List<UserOutDTO>> emptyResponse =
                StandardResponseOutDTO.success(Collections.emptyList(), "No employees found");
        when(adminService.getAllActiveUsers()).thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(adminService).getAllActiveUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllInactiveEmployees_ShouldReturnOk_WhenInactiveEmployeesExist() throws Exception {
        // Given
        when(adminService.getAllInactiveUsers()).thenReturn(userListResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/inactive-employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray());

        verify(adminService).getAllInactiveUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllInactiveEmployees_ShouldReturnNoContent_WhenNoInactiveEmployeesExist() throws Exception {
        // Given
        StandardResponseOutDTO<List<UserOutDTO>> emptyResponse =
                StandardResponseOutDTO.success(Collections.emptyList(), "No inactive employees found");
        when(adminService.getAllInactiveUsers()).thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/inactive-employees"))
                .andExpect(status().isNoContent());

        verify(adminService).getAllInactiveUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getManagerEmployee_ShouldReturnOk_WhenEmployeesExist() throws Exception {
        // Given
        when(adminService.getManagerEmployee(1L)).thenReturn(userListResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/manager-employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray());

        verify(adminService).getManagerEmployee(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getManagerEmployee_ShouldReturnNoContent_WhenNoEmployeesExist() throws Exception {
        // Given
        StandardResponseOutDTO<List<UserOutDTO>> emptyResponse =
                StandardResponseOutDTO.success(Collections.emptyList(), "No employees found");
        when(adminService.getManagerEmployee(1L)).thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/manager-employee/1"))
                .andExpect(status().isNoContent());

        verify(adminService).getManagerEmployee(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeRole_ShouldReturnOk_WhenValidInput() throws Exception {
        // Given
        when(adminService.changeUserRole(1L, "manager")).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/service-api/admin/change-role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminService).changeUserRole(eq(1L), eq("employee"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeRole_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid userInDTO with null userId
        UserInDTO invalidUserInDTO = new UserInDTO();
        invalidUserInDTO.setRole("manager");

        // When & Then
        mockMvc.perform(post("/api/service-api/admin/change-role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ShouldReturnOk_WhenValidInput() throws Exception {
        // Given
        MessageOutDTO messageOutDTO = new MessageOutDTO("User updated successfully");
        when(adminService.updateUserDetails(any(UserInDTO.class), eq(1L))).thenReturn(messageOutDTO);

        // When & Then
        mockMvc.perform(patch("/api/service-api/admin/update-user/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));

        verify(adminService).updateUserDetails(any(UserInDTO.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateUser_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/service-api/admin/update-user/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTotalUserCount_ShouldReturnOk_WhenCalled() throws Exception {
        // Given
        when(userService.countActiveUsers()).thenReturn(50L);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched User Count"))
                .andExpect(jsonPath("$.data").value(50L));

        verify(userService).countActiveUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRecentUsers_ShouldReturnOk_WhenCalled() throws Exception {
        // Given
        List<UsersDetailsViewDTO> recentUsers = Arrays.asList(userDetailsViewDTO);
        when(userService.getRecentUserDetails()).thenReturn(recentUsers);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/users/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched Recent Users"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(1L))
                .andExpect(jsonPath("$.data[0].fullName").value("John Doe"));

        verify(userService).getRecentUserDetails();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBundle_ShouldReturnOk_WhenValidBundleId() throws Exception {
        // Given
        when(adminService.deleteBundle(1L)).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/service-api/admin/bundle")
                        .with(csrf())
                        .param("bundleId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminService).deleteBundle(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBundle_ShouldReturnBadRequest_WhenMissingBundleId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/admin/bundle")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeCourseFromBundle_ShouldReturnOk_WhenValidIds() throws Exception {
        // Given
        when(adminService.removeCourseFromBundle(1L, 2L)).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/service-api/admin/bundle/removecourse")
                        .with(csrf())
                        .param("bundleId", "1")
                        .param("courseId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminService).removeCourseFromBundle(1L, 2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeCourseFromBundle_ShouldReturnBadRequest_WhenMissingParameters() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/admin/bundle/removecourse")
                        .with(csrf())
                        .param("bundleId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnrolledCoursesByUserId_ShouldReturnOk_WhenValidUserId() throws Exception {
        // Given
        List<UserCourseEnrollDetails> enrolledCourses = Arrays.asList(userCourseEnrollDetails);
        when(userService.getUserEnrolledCourses(1L)).thenReturn(enrolledCourses);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/userCourses/1")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Fetched enrolled courses successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L));

        verify(userService).getUserEnrolledCourses(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnrolledCoursesByUserId_ShouldReturnBadRequest_WhenMissingUserId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/service-api/admin/userCourses/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminDashboardStats_ShouldReturnOk_WhenCalled() throws Exception {
        // Given
        StandardResponseOutDTO<AdminDashboardStatsOutDTO> statsResponse =
                StandardResponseOutDTO.success(adminDashboardStats, "Dashboard stats fetched");
        when(adminService.getAdminStats()).thenReturn(statsResponse);

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/admin-dashboard-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.userCount").value(100L))
                .andExpect(jsonPath("$.data.groupCount").value(10L))
                .andExpect(jsonPath("$.data.courseCount").value(50L))
                .andExpect(jsonPath("$.data.bundleCount").value(5L))
                .andExpect(jsonPath("$.data.totalEnrollments").value(200L));

        verify(adminService).getAdminStats();
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void adminEndpoints_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // Test multiple admin-only endpoints with employee role
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/service-api/admin/inactive-employees"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/service-api/admin/count"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/service-api/admin/users/recent"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/service-api/admin/admin-dashboard-stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoints_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // Test accessing admin endpoints without authentication
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/service-api/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/service-api/admin/remove-user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoints_ShouldHandleServiceExceptions() throws Exception {
        // Given - Service throws exception
        when(adminService.getAllActiveUsers()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isInternalServerError());

        verify(adminService).getAllActiveUsers();
    }
}