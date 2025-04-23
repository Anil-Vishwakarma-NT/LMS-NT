package com.nt.LMS.controller;

import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.dto.UserInDTO;
import com.nt.LMS.serviceImpl.AdminServiceImpl;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
import com.nt.LMS.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Unit tests for the AdminController class.
 * <p>
 * These tests verify the functionality of the AdminController's endpoints, including:
 * - Registering an admin.
 * - Deleting an employee.
 * - Retrieving all employees.
 * - Retrieving employees managed by a specific manager.
 * - Changing user roles.
 * </p>
 */
class AdminControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AdminServiceImpl adminService;

    @Mock
    private GroupServiceImpl groupService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    /**
     * Setup mock data before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    /**
     * Test case for registering an admin.
     */
    @Test
    void testRegister_ShouldReturnSuccessMessage_WhenAdminRegistered() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");

        MessageOutDto messageOutDto = new MessageOutDto();
        messageOutDto.setMessage("Admin registered successfully");

        when(adminService.register(registerDto)).thenReturn(messageOutDto);

        mockMvc.perform(post("/admin/register")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Admin registered successfully"));

        verify(adminService, times(1)).register(registerDto);
    }

    /**
     * Test case for deleting an employee.
     */
    @Test
    void testDeleteEmployee_ShouldReturnSuccessMessage_WhenEmployeeDeleted() throws Exception {
        long userId = 1L;

        MessageOutDto messageOutDto = new MessageOutDto();
        messageOutDto.setMessage("User deleted successfully");

        when(adminService.employeeDeletion(userId)).thenReturn(messageOutDto);

        mockMvc.perform(delete("/admin/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(adminService, times(1)).employeeDeletion(userId);
    }

    /**
     * Test case for retrieving all employees.
     */
    @Test
    void testGetAllEmployees_ShouldReturnEmployeeList_WhenEmployeesExist() throws Exception {
        List<UserOutDTO> employees = new ArrayList<>();
        UserOutDTO user = new UserOutDTO();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        employees.add(user);

        when(adminService.getAllActiveUsers()).thenReturn(employees);

        mockMvc.perform(get("/admin/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(adminService, times(1)).getAllActiveUsers();
    }

    /**
     * Test case when there are no employees.
     */
    @Test
    void testGetAllEmployees_ShouldReturnNoContent_WhenNoEmployeesExist() throws Exception {
        List<UserOutDTO> employees = new ArrayList<>();

        when(adminService.getAllActiveUsers()).thenReturn(employees);

        mockMvc.perform(get("/admin/employees"))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).getAllActiveUsers();
    }

    /**
     * Test case for retrieving employees managed by a specific manager.
     */
    @Test
    void testGetManagerEmployee_ShouldReturnEmployeeList_WhenEmployeesExist() throws Exception {
        long userId = 1L;
        List<UserOutDTO> employees = new ArrayList<>();
        UserOutDTO user = new UserOutDTO();
        user.setUserId(2L);
        employees.add(user);

        when(adminService.getManagerEmployee(userId)).thenReturn(employees);

        mockMvc.perform(get("/admin/manager-employee/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].userId").value(2));

        verify(adminService, times(1)).getManagerEmployee(userId);
    }

    /**
     * Test case when a manager has no employees.
     */
    @Test
    void testGetManagerEmployee_ShouldReturnNoContent_WhenNoEmployeesExist() throws Exception {
        long userId = 1L;
        List<UserOutDTO> employees = new ArrayList<>();

        when(adminService.getManagerEmployee(userId)).thenReturn(employees);

        mockMvc.perform(get("/admin/manager-employee/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).getManagerEmployee(userId);
    }

    /**
     * Test case for changing a user's role.
     */
    @Test
    void testChangeRole_ShouldReturnSuccessMessage_WhenRoleChanged() throws Exception {
        UserInDTO userInDTO = new UserInDTO();
        userInDTO.setUserId(1L);
        userInDTO.setRole("manager");

        MessageOutDto messageOutDto = new MessageOutDto();
        messageOutDto.setMessage("Role changed successfully");

        when(adminService.changeUserRole(userInDTO.getUserId(), userInDTO.getRole())).thenReturn(messageOutDto);

        mockMvc.perform(post("/admin/change-role")
                        .contentType("application/json")
                        .content("{\"userId\":1,\"role\":\"manager\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role changed successfully"));

        verify(adminService, times(1)).changeUserRole(userInDTO.getUserId(), userInDTO.getRole());
    }
}
