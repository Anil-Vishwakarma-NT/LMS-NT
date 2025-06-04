package com.nt.LMS.serviceImpl;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.nt.LMS.constants.UserConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDTOConverter userDTOConverter;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldRegisterUserSuccessfully() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setEmail("john@example.com");
        registerDto.setUserName("johndoe");
        registerDto.setPassword("password");
        registerDto.setRoleId(1L);

        when(userRepository.findByEmailIgnoreCase(registerDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUserNameIgnoreCase(registerDto.getUserName())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

        MessageOutDto result = adminService.register(registerDto);

        assertEquals(USER_REGISTRATION_SUCCESS, result.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");

        when(userRepository.findByEmailIgnoreCase(registerDto.getEmail())).thenReturn(Optional.of(new User()));

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            adminService.register(registerDto);
        });

        assertEquals(USER_ALREADY_EXISTS, ex.getMessage());
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("unique@example.com");
        registerDto.setUserName("existingUser");

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUserNameIgnoreCase(registerDto.getUserName())).thenReturn(Optional.of(new User()));

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            adminService.register(registerDto);
        });

        assertEquals(USERNAME_ALREADY_EXISTS, ex.getMessage());
    }

    // Add more tests here: changeUserRole, employeeDeletion, etc.

    @Test
    void changeUserRole_ShouldUpdateRoleSuccessfully() {
        long userId = 1L;
        String newRole = "manager";

        Role role = new Role();
        role.setRoleId(2L);
        role.setName(newRole);

        User user = new User();
        user.setUserId(userId);
        user.setRoleId(1L);

        when(roleRepository.findByName(newRole)).thenReturn(Optional.of(role));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        MessageOutDto result = adminService.changeUserRole(userId, newRole);

        assertEquals(UPDATED, result.getMessage());
        assertEquals(2L, user.getRoleId());
    }

    @Test
    void changeUserRole_ShouldThrowException_WhenRoleNotFound() {
        when(roleRepository.findByName("fakeRole")).thenReturn(Optional.empty());

        User user = new User();
        user.setUserId(1L);
        user.setRoleId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(RuntimeException.class, () -> {
            adminService.changeUserRole(1L, "fakeRole");
        });

        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        assertEquals(INVALID_USER_ROLE, ex.getCause().getMessage());

    }

    @Test
    void changeUserRole_ShouldThrowException_WhenUserNotFound(){
        when(userRepository.findById(45L)).thenReturn(Optional.empty());

        Role role = new Role();
        role.setRoleId(2L);
        role.setName("manager");

        when(roleRepository.findByName("manager")).thenReturn(Optional.of(role));

        Exception ex = assertThrows(RuntimeException.class , () ->{
           adminService.changeUserRole(45L,"manager");
        });

        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals(USER_NOT_FOUND , ex.getCause().getMessage());
    }

    // ---------------------------
    // employeeDeletion
    // ---------------------------
    @Test
    void employeeDeletion_ShouldDeleteEmployeeSuccessfully() {
        long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setRoleId(1L);

        Role role = new Role();
        role.setRoleId(1L);
        role.setName("employee");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        MessageOutDto result = adminService.employeeDeletion(userId);

        assertEquals(USER_DELETION_MESSAGE, result.getMessage());
        verify(userRepository).deleteById(userId);
    }

    @Test
    void employeeDeletion_ShouldDeleteManagerAndReassignSubordinates() {
        // Arrange
        long userId = 2L;
        User manager = new User();
        manager.setUserId(userId);
        manager.setRoleId(2L); // Assume 2L corresponds to 'manager' role.

        Role role = new Role();
        role.setRoleId(2L);
        role.setName("manager");

        User subordinate = new User();
        subordinate.setUserId(3L);
        subordinate.setManagerId(userId); // The manager is currently assigned as their manager.

        List<User> subordinates = Arrays.asList(subordinate);

        when(userRepository.findById(userId)).thenReturn(Optional.of(manager));
        when(roleRepository.findById(manager.getRoleId())).thenReturn(Optional.of(role));
        when(userRepository.findByManagerId(userId)).thenReturn(subordinates);

        // Act
        MessageOutDto result = adminService.employeeDeletion(userId);

        // Assert
        assertEquals(USER_DELETION_MESSAGE, result.getMessage());
        verify(userRepository).deleteById(userId);
        verify(userRepository).saveAll(subordinates);
        assertEquals(UserConstants.getAdminId(), subordinate.getManagerId());  // Subordinate should now be assigned to admin
    }


    @Test
    void employeeDeletion_ShouldThrowException_WhenUserNotFound() {
        long userId = 2L;
        User manager = new User();
        manager.setUserId(userId);
        manager.setRoleId(2L); // Assume 2L corresponds to 'manager' role.

        Role role = new Role();
        role.setRoleId(2L);
        role.setName("manager");

        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.employeeDeletion(100L);
        });
    }

    @Test
    void employeeDeletion_ShouldThrowException_WhenInvalidRole() {
        // Arrange
        long userId = 3L;
        User user = new User();
        user.setUserId(userId);
        user.setRoleId(3L); // Assume 3L corresponds to an invalid role.

        Role role = new Role();
        role.setRoleId(3L);
        role.setName("invalidRole");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(user.getRoleId())).thenReturn(Optional.of(role));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            adminService.employeeDeletion(userId);
        });

        assertEquals(INVALID_USER_ROLE, ex.getMessage());
    }


    @Test
    void employeeDeletion_ShouldDeleteManagerWithNoSubordinates() {
        // Arrange
        long userId = 2L;
        User manager = new User();
        manager.setUserId(userId);
        manager.setRoleId(2L); // Assume 2L corresponds to 'manager'.

        // Create the role object
        Role role = new Role();
        role.setRoleId(2L);
        role.setName("manager");

        // Mock the userRepository.findById to return the manager object
        when(userRepository.findById(userId)).thenReturn(Optional.of(manager));

        // Mock the roleRepository.findById to return the 'manager' role
        when(roleRepository.findById(manager.getRoleId())).thenReturn(Optional.of(role));

        // Mock the userRepository.findByManagerId to return an empty list (no subordinates)
        when(userRepository.findByManagerId(userId)).thenReturn(Collections.emptyList());

        // Act
        MessageOutDto result = adminService.employeeDeletion(userId);

        // Assert
        assertEquals(USER_DELETION_MESSAGE, result.getMessage());

        // Verify that deleteById was called for the manager
        verify(userRepository).deleteById(userId);

        // Verify that saveAll was not called because there are no subordinates
        verify(userRepository, never()).saveAll(any());
    }
//----------------------
    //getAllUsers
    //-----------------------

    @Test
    void getAllUsers_ShouldReturnUserDtos_WhenUsersExist() {
        // Arrange
        User user1 = new User(1L,"John", "John", "Doe","JohnDoe@gmail.com","JohnDoe", 2L,3L,null ,null,true); // User with manager ID 2
        User user2 = new User(2L, "Jane","Jane", "Doe","JaneDoe@gmail.com","JaneDoe", 3L,3L,null,null ,true); // User with manager ID 3
        User manager1 = new User(2L, "Manager" , "Manager", "One","ManagerOne@gmail.com","ManagerOne", 1L,2L,null,null,true); // Manager for user1
        User manager2 = new User(3L, "Manager" , "Manager", "Two", "ManagerTwo@gmail.com","ManagerTwo",1L,2L,null,null,true); // Manager for user2

        // Create corresponding UserOutDTOs
        UserOutDTO userDto1 = new UserOutDTO(1L, "John","John", "Doe","JohnDoe@gmail.com", "Manager One" ,null);
        UserOutDTO userDto2 = new UserOutDTO(2L,"Jane", "Jane", "Doe","JaneDoe@gmail.com", "Manager Two",null);

        // Mock repository behavior
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userRepository.findById(user1.getManagerId())).thenReturn(Optional.of(manager1));
        when(userRepository.findById(user2.getManagerId())).thenReturn(Optional.of(manager2));

        // Mock userDTOConverter behavior
        when(userDTOConverter.userToOutDto(user1, "Manager One")).thenReturn(userDto1);
        when(userDTOConverter.userToOutDto(user2, "Manager Two")).thenReturn(userDto2);

        // Act
        List<UserOutDTO> result = adminService.getAllActiveUsers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(userDto1));
        assertTrue(result.contains(userDto2));

        verify(userRepository).findAll();
        verify(userRepository).findById(user1.getManagerId());
        verify(userRepository).findById(user2.getManagerId());
        verify(userDTOConverter).userToOutDto(user1, "Manager One");
        verify(userDTOConverter).userToOutDto(user2, "Manager Two");
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserOutDTO> result = adminService.getAllActiveUsers();

        // Assert
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(userDTOConverter, never()).userToOutDto(any(), any()); // Ensure converter is not called
    }

    @Test
    void getAllUsers_ShouldThrowException_WhenManagerNotFound() {
        // Arrange
        User user = new User(1L,"John", "John", "Doe","JohnDoe@gmail.com","JohnDoe", 2L,3L,null ,null,true); // User with manager ID 2

        // Mock repository behavior
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userRepository.findById(user.getManagerId())).thenReturn(Optional.empty()); // No manager found

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> {
            adminService.getAllActiveUsers();
        });

        assertTrue(ex.getCause() instanceof  ResourceNotFoundException);
        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());

        verify(userRepository).findAll();
        verify(userRepository).findById(user.getManagerId());
        verify(userDTOConverter, never()).userToOutDto(any(), any());
    }

    @Test
    void getAllUsers_ShouldThrowException_WhenDatabaseErrorOccurs() {
        // Arrange
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            adminService.getAllActiveUsers();
        });

        assertEquals(DATABASE_ERROR, ex.getMessage());

        verify(userRepository).findAll();
        verify(userDTOConverter, never()).userToOutDto(any(), any());
    }

    @Test
    void getAllUsers_ShouldHandleMixedValidAndInvalidManagers() {
        // Arrange
        User user1 = new User(1L, "John", "John", "Doe", "JohnDoe@gmail.com", "JohnDoe", 2L, 3L, null, null,true);
        User user2 = new User(3L, "Jane", "Jane", "Doe", "JaneDoe@gmail.com", "JaneDoe", 3L, 4L, null, null,true);

        User manager1 = new User(2L, "Manager", "Manager", "One", "ManagerOne@gmail.com", "ManagerOne", 1L, 2L, null, null,true);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userRepository.findById(user1.getManagerId())).thenReturn(Optional.of(manager1));
        when(userRepository.findById(user2.getManagerId())).thenReturn(Optional.empty());

        // You may want to mock converter for user1
        when(userDTOConverter.userToOutDto(eq(user1), eq("Manager One"))).thenReturn(new UserOutDTO());

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> {
            adminService.getAllActiveUsers();
        });

        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());

        verify(userDTOConverter, times(1)).userToOutDto(eq(user1), eq("Manager One"));
    }




    // ---------------------------
    // getManagerEmployee
    // ---------------------------
    @Test
    void getManagerEmployee_ShouldReturnUserList() {
        long managerId = 1L;

        User manager = new User();
        manager.setUserId(managerId);
        manager.setFirstName("Jane");
        manager.setLastName("Smith");

        User emp = new User();
        emp.setUserId(2L);
        emp.setManagerId(managerId);

        UserOutDTO userOutDTO = new UserOutDTO(); // populate as needed

        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(managerId)).thenReturn(List.of(emp));
        when(userDTOConverter.userToOutDto(eq(emp), anyString())).thenReturn(userOutDTO);

        List<UserOutDTO> result = adminService.getManagerEmployee(managerId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userDTOConverter).userToOutDto(emp, "JaneSmith");
    }

    @Test
    void getManagerEmployee_ShouldThrowException_WhenManagerNotFound() {
        long managerId = 99L;
        when(userRepository.findById(managerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            adminService.getManagerEmployee(managerId);
        });
    }


    //--------------------
    //GetEmployees
    //---------------------



}
