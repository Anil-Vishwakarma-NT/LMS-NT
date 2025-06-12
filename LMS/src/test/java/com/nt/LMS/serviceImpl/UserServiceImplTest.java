package com.nt.LMS.serviceImpl;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UserServiceImpl class.
 * <p>
 * These tests verify the functionality of the `loadUserByUsername` method in different scenarios, such as:
 * - When the user exists.
 * - When the user is not found.
 * - When the role is not found for a user.
 * </p>
 */
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private Role mockRole;

    /**
     * Setup mock data before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize mockUser with data
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");
        mockUser.setRoleId(2L);

        // Initialize mockRole with data
        mockRole = new Role();
        mockRole.setRoleId(2L);
        mockRole.setName("ROLE_USER");
    }

    /**
     * Test case for successfully loading user details when the user exists.
     */
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(mockRole));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        // Assert
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    /**
     * Test case when the user is not found in the repository.
     */
    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act + Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("notfound@example.com"));

        assertTrue(exception.getMessage().contains(UserConstants.USER_NOT_FOUND));
    }

    /**
     * Test case when the role is not found for the user.
     */
    @Test
    void loadUserByUsername_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        // Act + Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                userService.loadUserByUsername("test@example.com"));

        assertNotNull(exception);
    }
}
