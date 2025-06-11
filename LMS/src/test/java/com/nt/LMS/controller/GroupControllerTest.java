package com.nt.LMS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.LMS.inDTO.GroupInDTO;
import com.nt.LMS.outDTO.GroupOutDTO;
import com.nt.LMS.outDTO.MessageOutDto;
import com.nt.LMS.outDTO.UserOutDTO;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
import com.nt.LMS.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class GroupControllerTest {

    @InjectMocks
    private GroupController groupController;

    @Mock
    private GroupServiceImpl groupService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testCreateGroup() throws Exception {
        GroupInDTO dto = new GroupInDTO();
        dto.setGroupName("Engineering");

        MessageOutDto response = new MessageOutDto("Group Created");

        when(groupService.createGroup("Engineering", "testuser")).thenReturn(response);

        mockMvc.perform(post("/group/create-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Group Created"));
    }

    @Test
    void testDeleteGroup() throws Exception {
        MessageOutDto response = new MessageOutDto("Group Deleted");
        when(groupService.delGroup(1L)).thenReturn(response);

        mockMvc.perform(delete("/group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Group Deleted"));
    }

    @Test
    void testAddUserToGroup() throws Exception {
        GroupInDTO dto = new GroupInDTO();
        dto.setGroupId(1L);
        dto.setUserId(2L);

        when(groupService.addUserToGroup(2L, 1L)).thenReturn(new MessageOutDto("User added"));

        mockMvc.perform(post("/group/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User added"));
    }

    @Test
    void testRemoveUserFromGroup() throws Exception {
        GroupInDTO dto = new GroupInDTO();
        dto.setGroupId(1L);
        dto.setUserId(2L);

        when(groupService.removeUserFromGroup(2L, 1L)).thenReturn(new MessageOutDto("User removed"));

        mockMvc.perform(delete("/group/remove-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User removed"));
    }

    @Test
    void testGetUsersInGroup_WhenUsersExist() throws Exception {
        List<UserOutDTO> users = List.of(new UserOutDTO(1L, "John","John" ,"Doe", "john@example.com", "unknown",""));
        when(groupService.getUsersInGroup(1L)).thenReturn(users);

        mockMvc.perform(get("/group/group-emps/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void testGetUsersInGroup_WhenEmpty() throws Exception {
        when(groupService.getUsersInGroup(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/group/group-emps/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetGroups_WhenGroupsExist() throws Exception {
        List<GroupOutDTO> groups = List.of(new GroupOutDTO("Developers", 1L,"Dev Team"));
        when(groupService.getGroups("testuser")).thenReturn(groups);

        mockMvc.perform(get("/group/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].groupName").value("Developers"));
    }

    @Test
    void testGetGroups_WhenEmpty() throws Exception {
        when(groupService.getGroups("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/group/groups"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllGroups() throws Exception {
        List<GroupOutDTO> groups = List.of(new GroupOutDTO("Admin Group", 1L, "Admin Group"));
        when(groupService.getAllGroups()).thenReturn(groups);

        mockMvc.perform(get("/group/all-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].groupName").value("Admin Group"));
    }
}
