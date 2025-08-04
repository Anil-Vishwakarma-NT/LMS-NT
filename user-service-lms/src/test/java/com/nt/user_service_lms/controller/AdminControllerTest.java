package com.nt.user_service_lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.exception.InvalidRequestException;
import com.nt.user_service_lms.exception.ResourceConflictException;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.service.serviceImpl.AdminServiceImpl;
import com.nt.user_service_lms.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminServiceImpl adminService;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /register - success")
    void testRegisterUserSuccess() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@example.com");

        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(new MessageOutDTO("Registered"), "OK");

        Mockito.when(adminService.register(Mockito.any(RegisterDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/service-api/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("POST /register - conflict")
    void testRegisterConflict() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("duplicate@example.com");

        Mockito.when(adminService.register(Mockito.any())).thenThrow(new ResourceConflictException("User exists"));

        mockMvc.perform(post("/api/service-api/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteEmployeeSuccess() throws Exception {
        long userId = 1L;
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(new MessageOutDTO("Deleted"), null);
        Mockito.when(adminService.employeeDeletion(userId)).thenReturn(response);

        mockMvc.perform(delete("/api/service-api/admin/remove-user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Deleted"));
    }

    @Test
    void testDeleteEmployeeNotFound() throws Exception {
        long userId = 999;
        Mockito.when(adminService.employeeDeletion(userId)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(delete("/api/service-api/admin/remove-user/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllEmployeesEmptyList() throws Exception {
        StandardResponseOutDTO<List<UserOutDTO>> response =
                StandardResponseOutDTO.success(Collections.emptyList(), "No Data");

        Mockito.when(adminService.getAllActiveUsers()).thenReturn(response);

        mockMvc.perform(get("/api/service-api/admin/active-employees"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testChangeUserRoleSuccess() throws Exception {
        UserInDTO dto = new UserInDTO();
        dto.setUserId(5L);
        dto.setRole("manager");

        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(
                new MessageOutDTO("Role updated"), "OK");

        Mockito.when(adminService.changeUserRole(dto.getUserId(), dto.getRole())).thenReturn(response);

        mockMvc.perform(post("/api/service-api/admin/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Role updated"));
    }

    @Test
    void testChangeUserRoleInvalidRequest() throws Exception {
        UserInDTO dto = new UserInDTO();
        dto.setUserId(0L);
        dto.setRole("admin");

        Mockito.when(adminService.changeUserRole(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new InvalidRequestException("Invalid"));

        mockMvc.perform(post("/api/service-api/admin/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetDashboardStatsSuccess() throws Exception {
        StandardResponseOutDTO<AdminDashboardStatsOutDTO> response = StandardResponseOutDTO.success(
                new AdminDashboardStatsOutDTO(), "Fetched");

        Mockito.when(adminService.getAdminStats()).thenReturn(response);

        mockMvc.perform(get("/api/service-api/admin/admin-dashboard-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Fetched"));
    }

    @Test
    void testDeleteBundle() throws Exception {
        Mockito.when(adminService.deleteBundle(1L)).thenReturn(
                StandardResponseOutDTO.success(new MessageOutDTO("Bundle Deleted"), "Bundle Deleted"));

        mockMvc.perform(delete("/api/service-api/admin/bundle?bundleId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Bundle Deleted"));
    }

    @Test
    void testRemoveCourseFromBundle() throws Exception {
        Mockito.when(adminService.removeCourseFromBundle(1L, 2L)).thenReturn(
                StandardResponseOutDTO.success(new MessageOutDTO("Removed"), "Success"));

        mockMvc.perform(delete("/api/service-api/admin/bundle/removecourse?bundleId=1&courseId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Removed"));
    }

    // Add more tests similarly for updateUserDetails(), getInactiveEmployees, getManagerEmployee, etc.
}
