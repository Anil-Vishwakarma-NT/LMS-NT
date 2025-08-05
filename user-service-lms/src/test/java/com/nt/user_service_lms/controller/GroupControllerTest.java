package com.nt.user_service_lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dto.inDTO.GroupInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.service.GroupService;
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
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ServicePrincipal servicePrincipal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();

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

        // Set up security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        servicePrincipal,
                        null,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testCreateGroup_Success() throws Exception {
        // Arrange
        GroupInDTO groupInDTO = new GroupInDTO();
        groupInDTO.setGroupName("Test Group");
        groupInDTO.setEmployees(Arrays.asList(1L, 2L));

        MessageOutDTO messageOutDTO = new MessageOutDTO("Group created successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, "Group created successfully");

        when(groupService.createGroup(anyString(), anyString(), anyList())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/service-api/group/create-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Group created successfully"));
    }

    @Test
    void testDeleteGroup_Success() throws Exception {
        // Arrange
        Long groupId = 1L;
        MessageOutDTO messageOutDTO = new MessageOutDTO("Group deleted successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, null);

        when(groupService.deleteGroup(groupId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/service-api/group/remove/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testUpdateGroup_Success() throws Exception {
        // Arrange
        GroupInDTO groupInDTO = new GroupInDTO();
        groupInDTO.setGroupId(1L);
        groupInDTO.setGroupName("Updated Group Name");

        MessageOutDTO messageOutDTO = new MessageOutDTO("Group updated successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, null);

        when(groupService.updateGroup(anyLong(), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/service-api/group/update-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testAddUserToGroup_Success() throws Exception {
        // Arrange
        GroupInDTO groupInDTO = new GroupInDTO();
        groupInDTO.setGroupId(1L);
        groupInDTO.setEmployees(Arrays.asList(1L, 2L));
        groupInDTO.setCourses(Arrays.asList(101L, 102L));
        groupInDTO.setAssignedAt(LocalDateTime.now());
        groupInDTO.setDeadline(LocalDateTime.now().plusDays(30));

        MessageOutDTO messageOutDTO = new MessageOutDTO("User added to group successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, "User added to group successfully");

        when(groupService.addUserToGroup(any(GroupInDTO.class), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/service-api/group/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User added to group successfully"));
    }

    @Test
    void testRemoveUserFromGroup_Success() throws Exception {
        // Arrange
        GroupInDTO groupInDTO = new GroupInDTO();
        groupInDTO.setUserId(1L);
        groupInDTO.setGroupId(1L);

        MessageOutDTO messageOutDTO = new MessageOutDTO("User removed from group successfully");
        StandardResponseOutDTO<MessageOutDTO> response = StandardResponseOutDTO.success(messageOutDTO, null);

        when(groupService.removeUserFromGroup(anyLong(), anyLong())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/service-api/group/remove-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testGetUsersInGroup_Success() throws Exception {
        // Arrange
        Long groupId = 1L;
        List<GroupUserOutDTO> users = Arrays.asList(
                createGroupUserOutDTO(1L, "John", "Doe", 5, 85.5),
                createGroupUserOutDTO(2L, "Jane", "Smith", 3, 92.0)
        );

        StandardResponseOutDTO<List<GroupUserOutDTO>> response = StandardResponseOutDTO.success(users, "Users found");

        when(groupService.getUserDetail(groupId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/group-emps/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"));
    }

    @Test
    void testGetUsersInGroup_NoContent() throws Exception {
        // Arrange
        Long groupId = 1L;
        StandardResponseOutDTO<List<GroupUserOutDTO>> response = StandardResponseOutDTO.success(Collections.emptyList(), "No users found");

        when(groupService.getUserDetail(groupId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/group-emps/{groupId}", groupId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetCourseDetails_Success() throws Exception {
        // Arrange
        Long groupId = 1L;
        List<GroupCourseOutDTO> courses = Arrays.asList(
                createGroupCourseOutDTO(101L, "Java Basics", 10, 75.5),
                createGroupCourseOutDTO(102L, "Spring Framework", 8, 88.2)
        );

        StandardResponseOutDTO<List<GroupCourseOutDTO>> response = StandardResponseOutDTO.success(courses, "Courses found");

        when(groupService.getCourseDetail(groupId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/group-courses/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].courseName").value("Java Basics"));
    }

    @Test
    void testGetCourseEmpDetails_Success() throws Exception {
        // Arrange
        Long groupId = 1L;
        List<GroupCourseOutDTO> courses = Arrays.asList(
                createGroupCourseOutDTO(101L, "Java Basics", 0, 0.0),
                createGroupCourseOutDTO(102L, "Spring Framework", 0, 0.0)
        );

        StandardResponseOutDTO<List<GroupCourseOutDTO>> response = StandardResponseOutDTO.success(courses, "Employee courses found");

        when(groupService.getCourseEmpDetail(groupId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/emp/group-courses/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetGroups_Success() throws Exception {
        // Arrange
        List<GroupOutDTO> groups = Arrays.asList(
                createGroupOutDTO(1L, "Development Team", "John Doe"),
                createGroupOutDTO(2L, "Testing Team", "Jane Smith")
        );

        StandardResponseOutDTO<List<GroupOutDTO>> response = StandardResponseOutDTO.success(groups, "Groups found");

        when(groupService.getGroups(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].groupName").value("Development Team"));
    }

    @Test
    void testGetGroups_NoContent() throws Exception {
        // Arrange
        StandardResponseOutDTO<List<GroupOutDTO>> response = StandardResponseOutDTO.success(Collections.emptyList(), "No groups found");

        when(groupService.getGroups(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/groups"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetAllGroups_Success() throws Exception {
        // Arrange
        List<GroupOutDTO> groups = Arrays.asList(
                createGroupOutDTO(1L, "All Groups Test 1", "Admin User"),
                createGroupOutDTO(2L, "All Groups Test 2", "Manager User")
        );

        StandardResponseOutDTO<List<GroupOutDTO>> response = StandardResponseOutDTO.success(groups, "All groups found");

        when(groupService.getAllGroups()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/Allgroups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetAllActiveGroups_Success() throws Exception {
        // Arrange
        List<GroupOutDTO> activeGroups = Arrays.asList(
                createGroupOutDTO(1L, "Active Group 1", "Admin User"),
                createGroupOutDTO(3L, "Active Group 2", "Manager User")
        );

        StandardResponseOutDTO<List<GroupOutDTO>> response = StandardResponseOutDTO.success(activeGroups, "Active groups found");

        when(groupService.getAllActiveGroups()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/all-active-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetGroupCount_Success() throws Exception {
        // Arrange
        Long expectedCount = 15L;
        when(groupService.countGroups()).thenReturn(expectedCount);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(15))
                .andExpect(jsonPath("$.message").value("Group count retrieved successfully."))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testGetRecentGroups_Success() throws Exception {
        // Arrange
        List<GroupSummaryOutDTO> recentGroups = Arrays.asList(
                createGroupSummaryOutDTO(1L, "Recent Group 1", "Creator 1", 5L),
                createGroupSummaryOutDTO(2L, "Recent Group 2", "Creator 2", 8L)
        );

        StandardResponseOutDTO<List<GroupSummaryOutDTO>> response = StandardResponseOutDTO.success(recentGroups, "Recent groups found");

        when(groupService.getRecentGroupSummaries()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetUserCoursesInGroups_Success() throws Exception {
        // Arrange
        GroupInDTO groupInDTO = new GroupInDTO();
        groupInDTO.setGroupId(1L);
        groupInDTO.setUserId(1L);

        List<CourseInfoOutDTO> courses = Arrays.asList(
                createCourseInfoOutDTO(101L, "Java Course"),
                createCourseInfoOutDTO(102L, "Spring Course")
        );

        StandardResponseOutDTO<List<CourseInfoOutDTO>> response = StandardResponseOutDTO.success(courses, "User courses found");

        when(groupService.getUserCourses(anyLong(), anyLong())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/service-api/group/user-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetUserBundlesInGroups_Success() throws Exception {
        // Arrange
        GroupInDTO groupInDTO = new GroupInDTO();
        groupInDTO.setGroupId(1L);
        groupInDTO.setUserId(1L);

        List<BundleOutDTO> bundles = Arrays.asList(
                createBundleOutDTO(201L, "Java Bundle"),
                createBundleOutDTO(202L, "Spring Bundle")
        );

        StandardResponseOutDTO<List<BundleOutDTO>> response = StandardResponseOutDTO.success(bundles, "User bundles found");

        when(groupService.getUserBundles(anyLong(), anyLong())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/service-api/group/user-bundles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetUserGroupDetails_Success() throws Exception {
        // Arrange
        List<UserGroupOutDTO> userGroups = Arrays.asList(
                createUserGroupOutDTO(1L, "User Group 1", 5, 85.5),
                createUserGroupOutDTO(2L, "User Group 2", 3, 92.0)
        );

        StandardResponseOutDTO<List<UserGroupOutDTO>> response = StandardResponseOutDTO.success(userGroups, "User groups found");

        when(groupService.getUserGroupDetail(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/user-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetGroupBundles_Success() throws Exception {
        // Arrange
        Long groupId = 1L;
        List<GroupBundleOutDTO> groupBundles = Arrays.asList(
                createGroupBundleOutDTO(201L, "Group Bundle 1", 10, 78.5),
                createGroupBundleOutDTO(202L, "Group Bundle 2", 15, 82.3)
        );

        StandardResponseOutDTO<List<GroupBundleOutDTO>> response = StandardResponseOutDTO.success(groupBundles, "Group bundles found");

        when(groupService.getGroupBundles(groupId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/service-api/group/bundles")
                        .param("groupId", groupId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    // Helper methods to create DTOs for testing
    private GroupUserOutDTO createGroupUserOutDTO(Long userId, String firstName, String lastName, long enrols, double progress) {
        GroupUserOutDTO dto = new GroupUserOutDTO();
        dto.setUserId(userId);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEnrols(enrols);
        dto.setProgress(progress);
        return dto;
    }

    private GroupCourseOutDTO createGroupCourseOutDTO(Long courseId, String courseName, long enrols, double progress) {
        GroupCourseOutDTO dto = new GroupCourseOutDTO();
        dto.setCourseId(courseId);
        dto.setCourseName(courseName);
        dto.setEnrols(enrols);
        dto.setProgress(progress);
        return dto;
    }

    private GroupOutDTO createGroupOutDTO(Long groupId, String groupName, String creatorName) {
        GroupOutDTO dto = new GroupOutDTO();
        dto.setGroupId(groupId);
        dto.setGroupName(groupName);
        dto.setCreatorName(creatorName);
        return dto;
    }

    private GroupSummaryOutDTO createGroupSummaryOutDTO(Long groupId, String groupName, String creatorName, Long memberCount) {
        return new GroupSummaryOutDTO(groupId, groupName, creatorName, memberCount);
    }

    private CourseInfoOutDTO createCourseInfoOutDTO(Long courseId, String courseName) {
        CourseInfoOutDTO dto = new CourseInfoOutDTO();
        dto.setCourseId(courseId);
        dto.setTitle(courseName);
        return dto;
    }

    private BundleOutDTO createBundleOutDTO(Long bundleId, String bundleName) {
        BundleOutDTO dto = new BundleOutDTO();
        dto.setBundleId(bundleId);
        dto.setBundleName(bundleName);
        return dto;
    }

    private UserGroupOutDTO createUserGroupOutDTO(Long groupId, String groupName, long enrols, double progress) {
        UserGroupOutDTO dto = new UserGroupOutDTO();
        dto.setGroupId(groupId);
        dto.setGroupName(groupName);
        dto.setEnrols(enrols);
        dto.setProgress(progress);
        return dto;
    }

    private GroupBundleOutDTO createGroupBundleOutDTO(Long bundleId, String bundleName, long enrols, double progress) {
        GroupBundleOutDTO dto = new GroupBundleOutDTO();
        dto.setBundleId(bundleId);
        dto.setBundleName(bundleName);
        dto.setEnrols(enrols);
        dto.setProgress(progress);
        dto.setActive(true);
        return dto;
    }
}