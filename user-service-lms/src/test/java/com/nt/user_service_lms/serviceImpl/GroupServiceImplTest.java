package com.nt.user_service_lms.serviceImpl;

import com.nt.user_service_lms.converter.GroupDTOConverter;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.outDTO.GroupOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.entities.Group;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.entities.UserGroup;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.GroupRepository;
import com.nt.user_service_lms.repository.UserGroupRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.serviceImpl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.*;
=======
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
>>>>>>> ae7af0f68d263d712ee993f7a0fbae96de378918

import static com.nt.user_service_lms.constants.GroupConstants.GROUP_CREATED;
import static com.nt.user_service_lms.constants.GroupConstants.GROUP_DELETED;
import static com.nt.user_service_lms.constants.GroupConstants.GROUP_NOT_FOUND;
import static com.nt.user_service_lms.constants.GroupConstants.USER_ADDED_TO_GROUP;
import static com.nt.user_service_lms.constants.GroupConstants.USER_ALREADY_PRESENT_IN_GROUP;
import static com.nt.user_service_lms.constants.GroupConstants.USER_NOT_FOUND_IN_GROUP;
import static com.nt.user_service_lms.constants.GroupConstants.USER_REMOVED_SUCCESSFULLY;
import static com.nt.user_service_lms.constants.UserConstants.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
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


    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private GroupServiceImpl groupService;




    private User admin;
    private User employee;
    private Group mockGroup;

    private UserGroup mockUserGroup;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = new User();
        admin.setUserId(1L);
        admin.setEmail("admin@example.com");
        admin.setFirstName("John");
        admin.setLastName("Doe");


        employee = new User();
        employee.setUserId(2L);
        employee.setEmail("employee@example.com");
        employee.setFirstName("Alice");
        employee.setLastName("Smith");

        mockGroup = new Group("Test Group", admin.getUserId());
        mockGroup.setGroupId(100L);


        mockUserGroup = new UserGroup();
        mockUserGroup.setGroupId(100L);
        mockUserGroup.setUserId(2L);


    }

    // ---------------------- createGroup -----------------------

    @Test
    void createGroup_ShouldCreateGroupSuccessfully_WhenGroupNameIsUnique_AndUserExists() {
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);
        StandardResponseOutDTO<MessageOutDTO> response = groupService.createGroup("Test Group", "admin@example.com", List.of());

        assertNotNull(response);
        assertEquals(GROUP_CREATED, response.getMessage());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void createGroup_ShouldCreateGroup_WhenAllParametersPassed() {
        // Arrange
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userGroupRepository.save(any(UserGroup.class))).thenReturn(mockUserGroup);

        // Act
        StandardResponseOutDTO<MessageOutDTO> response = groupService.createGroup("Test Group", "admin@example.com", List.of(2L));

        // Assert
        assertNotNull(response);
        assertEquals(GROUP_CREATED, response.getMessage());
        verify(groupRepository).save(any(Group.class));
        verify(userRepository).existsById(2L);
        verify(userGroupRepository).save(any(UserGroup.class));
    }



//@Test
//void createGroup_ShouldCreateGroup_WhenAllParametersPassed(){
//    when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
//    when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
//    when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);
//    Group mockGroup = Mockito.mock(Group.class);
//    when(mockGroup.getGroupId()).thenReturn(100L);
//    lenient().when(userRepository.existsById(2L)).thenReturn(true);
//    when(userGroupRepository.save(any(UserGroup.class))).thenReturn(mockUserGroup);
//    StandardResponseOutDTO<MessageOutDto> response = groupService.createGroup("Test Group", "admin@example.com" , List.of(2L));
//
//
//    assertNotNull(response);
//    assertEquals(GROUP_CREATED, response.getMessage());
//    verify(groupRepository).save(any(Group.class));
//}


    @Test
    void createGroup_ShouldReturnError_WhenGroupNameAlreadyExists(){
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(true);
        StandardResponseOutDTO response = groupService.createGroup("Test Group" ,"admin@example.com" , List.of());

        assertEquals("Group with same name already exists.",response.getMessage());

        verify(groupRepository,times(1)).existsByGroupName(any(String.class));
    }

    @Test
    void createGroup_ShouldReturnError_WhenUserNotFound() {
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
        when(userRepository.findByEmailIgnoreCase("invalid@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> groupService.createGroup("Test Group", "invalid@example.com", List.of())
        );

        assertEquals("User does not exist", exception.getCause().getMessage());

        verify(userRepository , times(1)).findByEmailIgnoreCase(anyString());
    }



    @Test
    void createGroup_ShouldReturnError_WhenEmployeeIdNotFound(){
        when(groupRepository.existsByGroupName("Test Group")).thenReturn(false);
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);
//        when(userRepository.existsById(3L)).thenReturn(false);


        RuntimeException exception = assertThrows(RuntimeException.class,
                ()->groupService.createGroup("Test Group" , "admin@example.com" , List.of(3L)));

        assertEquals(USER_NOT_FOUND, exception.getCause().getMessage());

        verify(userRepository , times(1)).findByEmailIgnoreCase(anyString());

    }


//    // ---------------------- delGroup -----------------------
//
@Test
void deleteGroup_ShouldDeleteGroupSuccessfully_WhenGroupExists() {
    when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));

    StandardResponseOutDTO<MessageOutDTO> response = groupService.deleteGroup(100L);

    assertNotNull(response);
    assertEquals(GROUP_DELETED, response.getData().getMessage());
    verify(enrollmentRepository).softDeleteByGroupId(100L);
    verify(userGroupRepository).softDeleteByGroupId(100L);
    verify(groupRepository).softDeleteByGroupId(100L);
}

    @Test
    void delGroup_ShouldThrow_WhenGroupNotFound() {
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                groupService.deleteGroup(999L)
        );

        assertEquals(GROUP_NOT_FOUND, ex.getCause().getMessage());
    }
//------------------------updateGroup-----------------------------

    @Test
    void updateGroup_ShouldUpdateGroupName_WhenGroupExists() {
        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));

        StandardResponseOutDTO<MessageOutDTO> response = groupService.updateGroup(100L, "Updated Name");

        assertNotNull(response);
        assertEquals("Group updated", response.getData().getMessage());
        assertEquals("Updated Name", mockGroup.getGroupName());
        verify(groupRepository).save(mockGroup);
    }


    @Test
    void updateGroup_ShouldGiveError_WhenGroupNotExist() {
        when(groupRepository.findById(100L)).thenReturn(Optional.empty());

       Exception response = assertThrows(RuntimeException.class , ()->groupService.updateGroup(100L, "Updated Name"));
        assertEquals("Group Not found", response.getCause().getMessage());
    }

    @Test
    void removeUserFromGroup_ShouldRemoveUser_WhenUserInGroupExists() {
        when(userGroupRepository.findByUserIdAndGroupId(2L, 100L)).thenReturn(Optional.of(mockUserGroup));

        StandardResponseOutDTO<MessageOutDTO> response = groupService.removeUserFromGroup(2L, 100L);

        assertNotNull(response);
        assertEquals(USER_REMOVED_SUCCESSFULLY, response.getData().getMessage());
        verify(userGroupRepository).softDeleteByGroupIdAndUserId(100L, 2L);
        verify(enrollmentRepository).softDeleteByGroupIdAndUserId(100L, 2L);
    }

    @Test
    void getGroups_ShouldReturnGroupsForAdmin() {
        admin.setUserId(1L); // Admin ID
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(groupRepository.findAll()).thenReturn(List.of(mockGroup));
        when(groupDTOConverter.groupToOutDto(eq(mockGroup), anyString())).thenReturn(new GroupOutDTO());

        StandardResponseOutDTO<List<GroupOutDTO>> response = groupService.getGroups("admin@example.com");

        assertNotNull(response);
        assertEquals("Group fetched Successfully", response.getMessage());
        assertEquals(1, response.getData().size());
    }

    @Test
    void countGroups_ShouldReturnCorrectCount() {
        when(groupRepository.count()).thenReturn(5L);
        long count = groupService.countGroups();
        assertEquals(5L, count);
    }

    




// ---------------------- addUserToGroup -----------------------
//
//    @Test
//    void addUserToGroup_ShouldAddUserSuccessfully() {
//        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
//        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.empty());
//
//        MessageOutDto result = groupService.addUserToGroup(1L, 100L);
//
//        assertEquals(USER_ADDED_TO_GROUP, result.getMessage());
//    }
//
//    @Test
//    void addUserToGroup_ShouldReturnAlreadyPresentMessage() {
//        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
//        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.of(new UserGroup(1L, 100L)));
//
//        MessageOutDto result = groupService.addUserToGroup(1L, 100L);
//
//        assertEquals(USER_ALREADY_PRESENT_IN_GROUP, result.getMessage());
//    }
//
//    @Test
//    void addUserToGroup_ShouldThrow_WhenUserNotFound() {
//        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                groupService.addUserToGroup(999L, 100L)
//        );
//
//        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());
//    }
//
//    // ---------------------- removeUserFromGroup -----------------------
//
//    @Test
//    void removeUserFromGroup_ShouldRemoveSuccessfully() {
//        UserGroup userGroup = new UserGroup(1L, 100L);
//        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.of(userGroup));
//
//        MessageOutDto result = groupService.removeUserFromGroup(1L, 100L);
//
//        assertEquals(USER_REMOVED_SUCCESSFULLY, result.getMessage());
//        verify(userGroupRepository).delete(userGroup);
//    }
//
//    @Test
//    void removeUserFromGroup_ShouldThrow_WhenUserGroupNotFound() {
//        when(userGroupRepository.findByUserIdAndGroupId(1L, 100L)).thenReturn(Optional.empty());
//
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                groupService.removeUserFromGroup(1L, 100L)
//        );
//
//        assertEquals(USER_NOT_FOUND_IN_GROUP, ex.getCause().getMessage());
//    }
//
//    // ---------------------- getUsersInGroup -----------------------
//
////    @Test
////    void getUsersInGroup_ShouldReturnUserList() {
////        UserGroup userGroup = new UserGroup(4L, 100L);
////
////        User mockManager = new User();
////        mockManager.setUserId(2L);
////        mockManager.setFirstName("Jane");
////        mockManager.setLastName("Smith");
////        mockManager.setUserName("Jane");
////
////        User user = new User();
////        user.setUserId(4L);
////       user.setEmail("test@example.com");
////        user.setFirstName("Johnny");
////        user.setLastName("Doe");
////        user.setManagerId(2L);
////
////        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
////        when(userGroupRepository.findAllByGroupId(100L)).thenReturn(List.of(userGroup));
////        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
//////        when(userRepository.findById(2L)).thenReturn(Optional.of(mockManager));
////        when(userDTOConverter.userToOutDto(mockUser, "JaneSmith")).thenReturn(new UserOutDTO());
////
////        List<UserOutDTO> result = groupService.getUsersInGroup(100L);
////
////        assertEquals(1, result.size());
////    }
//
//    @Test
//    void getUsersInGroup_ShouldReturnEmptyList_WhenNoUsersFound() {
//        when(groupRepository.findById(100L)).thenReturn(Optional.of(mockGroup));
//        when(userGroupRepository.findAllByGroupId(100L)).thenReturn(Collections.emptyList());
//
//        List<UserOutDTO> result = groupService.getUsersInGroup(100L);
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void getUsersInGroup_ShouldThrow_WhenGroupNotFound() {
//        when(groupRepository.findById(100L)).thenReturn(Optional.empty());
//
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                groupService.getUsersInGroup(100L)
//        );
//
//        assertEquals(GROUP_NOT_FOUND, ex.getCause().getMessage());
//    }
//
//    // ---------------------- getGroups -----------------------
//
//    @Test
//    void getGroups_ShouldReturnGroupsForNormalUser() {
//        mockUser.setUserId(2L);
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
//        when(groupRepository.findByCreatorId(2L)).thenReturn(List.of(mockGroup));
//        when(groupDTOConverter.groupToOutDto(any(), anyString())).thenReturn(new GroupOutDTO());
//
//        List<GroupOutDTO> result = groupService.getGroups("test@example.com");
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getGroups_ShouldThrow_WhenUserNotFound() {
//        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
//
//        Exception ex = assertThrows(RuntimeException.class, () ->
//                groupService.getGroups("notfound@example.com")
//        );
//
//        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
//        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());
//    }
//
//    // ---------------------- getAllGroups -----------------------
//
//    @Test
//    void getAllGroups_ShouldReturnGroupList() {
//        when(groupRepository.findAll()).thenReturn(List.of(mockGroup));
//        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
//        when(groupDTOConverter.groupToOutDto(mockGroup, "JohnDoe")).thenReturn(new GroupOutDTO());
//
//        List<GroupOutDTO> result = groupService.getAllGroups();
//
//        assertEquals(1, result.size());
//    }
}
