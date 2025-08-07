package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.constants.GroupConstants;
import com.nt.user_service_lms.constants.UserConstants;
import com.nt.user_service_lms.converter.GroupDTOConverter;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.inDTO.GroupInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.entities.*;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.*;
import com.nt.user_service_lms.service.serviceImpl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock private GroupRepository groupRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserDTOConverter userDTOConverter;
    @Mock private UserGroupRepository userGroupRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private CourseMicroserviceClient courseMicroserviceClient;
    @Mock private GroupDTOConverter groupDTOConverter;

    private User user;
    private Group group;
    private GroupInDTO groupInDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("admin@example.com");
        user.setFirstName("Admin");
        user.setLastName("User");

        group = new Group("Test Group", user.getUserId());
        group.setGroupId(10L);

        groupInDTO = new GroupInDTO();
        groupInDTO.setGroupId(10L);
        groupInDTO.setEmployees(List.of(2L));
        groupInDTO.setCourses(List.of(100L));
        groupInDTO.setBundles(List.of(200L));
        groupInDTO.setAssignedAt(LocalDateTime.now());
        groupInDTO.setDeadline(LocalDateTime.now().plusDays(10));
    }

    @Test
    void testCreateGroup_Success() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true); // ✅
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user)); // ✅
        when(groupRepository.existsByGroupName(anyString())).thenReturn(false); // ✅
        when(groupRepository.save(any(Group.class))).thenReturn(group); // ✅


        var result = groupService.createGroup("Test Group", "admin@example.com", List.of(userId));
        assertTrue(result.getStatus()=="SUCCESS");
        assertEquals(GroupConstants.GROUP_CREATED, result.getMessage());
    }

    @Test
    void testDeleteGroup_Success() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        doNothing().when(enrollmentRepository).softDeleteByGroupId(10L);
        doNothing().when(userGroupRepository).softDeleteByGroupId(10L);
        doNothing().when(groupRepository).softDeleteByGroupId(10L);

        var response = groupService.deleteGroup(10L);
        assertTrue(response.getStatus()=="SUCCESS");
        assertEquals(GroupConstants.GROUP_DELETED, response.getData().getMessage());
    }




    @Test
    void testUpdateGroup_Success() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        var result = groupService.updateGroup(10L, "Updated Name");
        assertTrue(result.getStatus()=="SUCCESS");
    }

    @Test
    void testRemoveUserFromGroup_Success() {
        UserGroup ug = new UserGroup(2L, 10L);
        when(userGroupRepository.findByUserIdAndGroupId(2L, 10L)).thenReturn(Optional.of(ug));
        doNothing().when(userGroupRepository).softDeleteByGroupIdAndUserId(10L, 2L);
        doNothing().when(enrollmentRepository).softDeleteByGroupIdAndUserId(10L, 2L);

        var result = groupService.removeUserFromGroup(2L, 10L);
        assertTrue(result.getStatus()=="SUCCESS");
    }

    @Test
    void testGetGroups_Admin() {
        user.setUserId(UserConstants.getAdminId());
        GroupOutDTO gout = new GroupOutDTO();

        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));
        when(groupRepository.findAll()).thenReturn(List.of(group));
        when(groupDTOConverter.groupToOutDto(any(), anyString())).thenReturn(gout);

        var result = groupService.getGroups(user.getEmail());
        assertTrue(result.getStatus()=="SUCCESS");
    }

    @Test
    void testGetAllGroups_Success() {
        when(groupRepository.findAll()).thenReturn(List.of(group));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(groupDTOConverter.groupToOutDto(any(), anyString())).thenReturn(new GroupOutDTO());

        var result = groupService.getAllGroups();
        assertTrue(result.getStatus()=="SUCCESS");
    }

    @Test
    void testCountGroups() {
        when(groupRepository.count()).thenReturn(5L);
        assertEquals(5, groupService.countGroups());
    }

    @Test
    void testGetAllActiveGroups() {
        when(groupRepository.findByIsActiveTrue()).thenReturn(List.of(group));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(groupDTOConverter.groupToOutDto(any(), anyString())).thenReturn(new GroupOutDTO());

        var result = groupService.getAllActiveGroups();
        assertTrue(result.getStatus()=="SUCCESS");
    }

    @Test
    void testGetRecentGroupSummaries() {
        when(groupRepository.findTop5ByOrderByGroupIdDesc()).thenReturn(List.of(group));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userGroupRepository.findAllByGroupId(anyLong())).thenReturn(List.of());

        var result = groupService.getRecentGroupSummaries();
        assertTrue(result.getStatus()=="SUCCESS");
    }
}
