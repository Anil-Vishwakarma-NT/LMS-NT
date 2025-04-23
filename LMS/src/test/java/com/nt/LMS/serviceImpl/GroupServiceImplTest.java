package com.nt.LMS.serviceImpl;

import com.nt.LMS.converter.GroupDTOConverter;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserGroup;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.exception.UnauthorizedAccessException;
import com.nt.LMS.repository.GroupRepository;
import com.nt.LMS.repository.UserGroupRepository;
import com.nt.LMS.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static com.nt.LMS.constants.GroupConstants.*;
import static com.nt.LMS.constants.UserConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private GroupDTOConverter groupDTOConverter;

    @Mock
    private UserDTOConverter userDTOConverter;

    @InjectMocks
    private GroupServiceImpl groupService;

    private User mockUser;
    private Group mockGroup;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        mockGroup = new Group("Test Group", mockUser.getUserId());
        mockGroup.setGroupId(100L);
    }

    // ---------------------- createGroup -----------------------

    @Test
    void createGroup_ShouldCreateGroupSuccessfully() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);

        MessageOutDto response = groupService.createGroup("Test Group", "test@example.com");

        assertEquals(GROUP_CREATED, response.getMessage());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void createGroup_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.createGroup("Group", "invalid@example.com")
        );

        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());
    }

    // ---------------------- delGroup -----------------------

    @Test
    void delGroup_ShouldDeleteSuccessfully() {
        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));

        MessageOutDto result = groupService.delGroup(100L);

        assertEquals(GROUP_DELETED, result.getMessage());
        verify(userGroupRepository).deleteByGroupId(100L);
        verify(groupRepository).delete(mockGroup);
    }

    @Test
    void delGroup_ShouldThrow_WhenGroupNotFound() {
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.delGroup(999L)
        );

        assertEquals(GROUP_NOT_FOUND, ex.getCause().getMessage());
    }

    // ---------------------- addUserToGroup -----------------------

    @Test
    void addUserToGroup_ShouldAddUserSuccessfully() {
        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.empty());

        MessageOutDto result = groupService.addUserToGroup(1L, 100L);

        assertEquals(USER_ADDED_TO_GROUP, result.getMessage());
    }

    @Test
    void addUserToGroup_ShouldReturnAlreadyPresentMessage() {
        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.of(new UserGroup(1L, 100L)));

        MessageOutDto result = groupService.addUserToGroup(1L, 100L);

        assertEquals(USER_ALREADY_PRESENT_IN_GROUP, result.getMessage());
    }

    @Test
    void addUserToGroup_ShouldThrow_WhenUserNotFound() {
        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.addUserToGroup(999L, 100L)
        );

        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());
    }

    // ---------------------- removeUserFromGroup -----------------------

    @Test
    void removeUserFromGroup_ShouldRemoveSuccessfully() {
        UserGroup userGroup = new UserGroup(1L, 100L);
        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.of(userGroup));

        MessageOutDto result = groupService.removeUserFromGroup(1L, 100L);

        assertEquals(USER_REMOVED_SUCCESSFULLY, result.getMessage());
        verify(userGroupRepository).delete(userGroup);
    }

    @Test
    void removeUserFromGroup_ShouldThrow_WhenUserGroupNotFound() {
        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.removeUserFromGroup(1L, 100L)
        );

        assertEquals(USER_NOT_FOUND_IN_GROUP, ex.getCause().getMessage());
    }

    // ---------------------- getUsersInGroup -----------------------

//    @Test
//    void getUsersInGroup_ShouldReturnUserList() {
//        UserGroup userGroup = new UserGroup(4L, 100L);
//
//        User mockManager = new User();
//        mockManager.setUserId(2L);
//        mockManager.setFirstName("Jane");
//        mockManager.setLastName("Smith");
//        mockManager.setUserName("Jane");
//
//        User user = new User();
//        user.setUserId(4L);
//       user.setEmail("test@example.com");
//        user.setFirstName("Johnny");
//        user.setLastName("Doe");
//        user.setManagerId(2L);
//
//        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
//        when(userGroupRepository.findAllByGroupId(100L)).thenReturn(List.of(userGroup));
//        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
////        when(userRepository.findById(2L)).thenReturn(Optional.of(mockManager));
//        when(userDTOConverter.userToOutDto(mockUser, "JaneSmith")).thenReturn(new UserOutDTO());
//
//        List<UserOutDTO> result = groupService.getUsersInGroup(100L);
//
//        assertEquals(1, result.size());
//    }

    @Test
    void getUsersInGroup_ShouldReturnEmptyList_WhenNoUsersFound() {
        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
        when(userGroupRepository.findAllByGroupId(100L)).thenReturn(Collections.emptyList());

        List<UserOutDTO> result = groupService.getUsersInGroup(100L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUsersInGroup_ShouldThrow_WhenGroupNotFound() {
        when(groupRepository.findById(100L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.getUsersInGroup(100L)
        );

        assertEquals(GROUP_NOT_FOUND, ex.getCause().getMessage());
    }

    // ---------------------- getGroups -----------------------

    @Test
    void getGroups_ShouldReturnGroupsForNormalUser() {
        mockUser.setUserId(2L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(groupRepository.findByCreatorId(2L)).thenReturn(List.of(mockGroup));
        when(groupDTOConverter.groupToOutDto(any(), anyString())).thenReturn(new GroupOutDTO());

        List<GroupOutDTO> result = groupService.getGroups("test@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void getGroups_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.getGroups("notfound@example.com")
        );

        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());
    }

    // ---------------------- getAllGroups -----------------------

    @Test
    void getAllGroups_ShouldReturnGroupList() {
        when(groupRepository.findAll()).thenReturn(List.of(mockGroup));
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(groupDTOConverter.groupToOutDto(mockGroup, "JohnDoe")).thenReturn(new GroupOutDTO());

        List<GroupOutDTO> result = groupService.getAllGroups();

        assertEquals(1, result.size());
    }
}
