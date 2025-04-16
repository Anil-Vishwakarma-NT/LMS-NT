package com.nt.LMS.controller;

import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.serviceImpl.AdminServiceImpl;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import({ManagerController.class})
@AutoConfigureMockMvc(addFilters = false)
class ManagerControllerTest {

    @InjectMocks
    private ManagerController managerController;

    @Mock
    private AdminServiceImpl adminService;

    @Mock
    private GroupServiceImpl groupService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(managerController).build();

        // Mock authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("manager@example.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void testGetManagerEmp_WhenEmployeesExist() throws Exception {
        UserOutDTO employee = new UserOutDTO();
        employee.setUserId(1L);
        employee.setEmail("employee@example.com");
//        employee.setRole("employee");

        when(adminService.getEmployees("manager@example.com")).thenReturn(List.of(employee));

        mockMvc.perform(get("/manager/manager-employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("employee@example.com"));
    }

    @Test
    void testGetManagerEmp_WhenNoEmployees() throws Exception {
        when(adminService.getEmployees("manager@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/manager/manager-employees"))
                .andExpect(status().isNoContent());
    }
}
