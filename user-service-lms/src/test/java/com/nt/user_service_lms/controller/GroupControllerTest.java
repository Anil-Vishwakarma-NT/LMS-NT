package com.nt.user_service_lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.user_service_lms.config.JwtUtil;
import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.config.TestAuthenticationFilter;
import com.nt.user_service_lms.config.TestSecurityConfig;
import com.nt.user_service_lms.dto.inDTO.GroupInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.GroupService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.nt.user_service_lms.constants.GroupConstants.GROUP_CREATED;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(GroupController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private GroupService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TestAuthenticationFilter serviceAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private GroupInDTO sampleDTO;
    private final String email = "admin@nucleusteq.com";





    @BeforeEach
    void setup() throws Exception {


        doAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(serviceAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));

        sampleDTO = new GroupInDTO();
        sampleDTO.setGroupId(1L);
        sampleDTO.setGroupName("Test Group");
        sampleDTO.setUserId(2L);
        sampleDTO.setEmployees(List.of(3L));
    }

    // CREATE
    @Test
    @DisplayName("Should create group and return 201 CREATED")
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateGroup() throws Exception {
        var response =  StandardResponseOutDTO.success(new MessageOutDTO(GROUP_CREATED), GROUP_CREATED);
        when(groupService.createGroup(anyString(), anyString(), anyList())).thenReturn(response);

        mockMvc.perform(post("/api/service-api/group/create-group")
                        .header("X-Test-Role", "ADMIN") // Required for authentication
                        .header("X-Test-User", "JUnit Admin") // Optional, sets display name
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO))
                        .with(csrf()))
                .andExpect(status().isCreated());    }

    @Test
    @DisplayName("Should return 403 when creating group without auth")
    void shouldNotCreateGroup_Unauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/api/service-api/group/create-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isForbidden());
    }
//
//    // DELETE
//    @Test
//    void shouldDeleteGroup() throws Exception {
//        var response = StandardResponseOutDTO.success(new MessageOutDTO("Deleted"), "success");
//        Mockito.when(groupService.deleteGroup(1L)).thenReturn(response);
//
//        mockMvc.perform(delete("/api/service-api/group/remove/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.message").value("Deleted"));
//    }
//
//    // UPDATE
//    @Test
//    void shouldUpdateGroup() throws Exception {
//        var response = StandardResponseOutDTO.success(new MessageOutDTO("Updated"), "success");
//        Mockito.when(groupService.updateGroup(anyLong(), anyString())).thenReturn(response);
//
//        mockMvc.perform(put("/api/service-api/group/update-group")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.message").value("Updated"));
//    }
//
//    // ADD USER
//    @Test
//    void shouldAddUserToGroup() throws Exception {
//        var response = StandardResponseOutDTO.success(new MessageOutDTO("Added"), "success");
//        Mockito.when(groupService.addUserToGroup(any(), anyString())).thenReturn(response);
//
//        mockMvc.perform(post("/api/service-api/group/add-user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.message").value("Added"));
//    }
//
//    // REMOVE USER
//    @Test
//    void shouldRemoveUserFromGroup() throws Exception {
//        var response = StandardResponseOutDTO.success(new MessageOutDTO("Removed"), "success");
//        Mockito.when(groupService.removeUserFromGroup(2L, 1L)).thenReturn(response);
//
//        mockMvc.perform(delete("/api/service-api/group/remove-user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.message").value("Removed"));
//    }
//
//    // USERS IN GROUP
//    @Test
//    void shouldReturnNoContentForEmptyUserList() throws Exception {
//        Mockito.when(groupService.getUserDetail(1L))
//                .thenReturn(StandardResponseOutDTO.success(Collections.emptyList(), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/group-emps/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    // COURSES
//    @Test
//    void shouldReturnGroupCourses() throws Exception {
//        Mockito.when(groupService.getCourseDetail(1L))
//                .thenReturn(StandardResponseOutDTO.success(List.of(new GroupCourseOutDTO()), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/group-courses/1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnGroupEmpCourses() throws Exception {
//        Mockito.when(groupService.getCourseEmpDetail(1L))
//                .thenReturn(StandardResponseOutDTO.success(List.of(new GroupCourseOutDTO()), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/emp/group-courses/1"))
//                .andExpect(status().isOk());
//    }
//
//    // GET GROUPS
//    @Test
//    void shouldReturnNoContentIfNoGroups() throws Exception {
//        Mockito.when(groupService.getGroups(email))
//                .thenReturn( StandardResponseOutDTO.success(Collections.emptyList(), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/groups"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void shouldReturnAllGroups() throws Exception {
//        Mockito.when(groupService.getAllGroups())
//                .thenReturn(StandardResponseOutDTO.success(List.of(new GroupOutDTO()), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/Allgroups"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnAllActiveGroups() throws Exception {
//        Mockito.when(groupService.getAllActiveGroups())
//                .thenReturn(StandardResponseOutDTO.success(List.of(new GroupOutDTO()), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/all-active-groups"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnGroupCount() throws Exception {
//        Mockito.when(groupService.countGroups()).thenReturn(5L);
//
//        mockMvc.perform(get("/api/service-api/group/count"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(5));
//    }
//
//    @Test
//    void shouldReturnRecentGroups() throws Exception {
//        Mockito.when(groupService.getRecentGroupSummaries())
//                .thenReturn(StandardResponseOutDTO.success(List.of(new GroupSummaryOutDTO()), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/recent"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnUserCoursesInGroups() throws Exception {
//        Mockito.when(groupService.getUserCourses(anyLong(), anyLong()))
//                .thenReturn(StandardResponseOutDTO.success(List.of(new CourseInfoOutDTO()), "success"));
//
//        mockMvc.perform(post("/api/service-api/group/user-courses")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleDTO)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnUserBundlesInGroups() throws Exception {
//        Mockito.when(groupService.getUserBundles(anyLong(), anyLong()))
//                .thenReturn(StandardResponseOutDTO.success(List.of(new BundleOutDTO()), "success"));
//
//        mockMvc.perform(post("/api/service-api/group/user-bundles")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleDTO)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnUserGroupDetails() throws Exception {
//        Mockito.when(groupService.getUserGroupDetail(email))
//                .thenReturn(StandardResponseOutDTO.success(List.of(new UserGroupOutDTO()), "success"));
//
//        mockMvc.perform(get("/api/service-api/group/user-groups"))
//                .andExpect(status().isOk());
//    }
//
////    @Test
////    void shouldReturnGroupBundles() throws Exception {
////        Mockito.when(groupService.getGroupBundles(1L))
////                .thenReturn(StandardResponseOutDTO.success(List.of(new GroupBundleOutDTO()), "success"));
////
////        mockMvc.perform(get("/api/service-api/group/bundles")
////                        .param("groupId", "1"))
////                .andExpect(status().isOk());
////    }
}
