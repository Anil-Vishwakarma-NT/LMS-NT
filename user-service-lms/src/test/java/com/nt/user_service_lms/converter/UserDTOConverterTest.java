package com.nt.user_service_lms.converter;

import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDTOConverterTest {

    private UserDTOConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UserDTOConverter();
    }

    @AfterEach
    void tearDown() {
        converter = null;
    }

    /**
     * Happy path: all fields populated including role and manager.
     */
    @Test
    void testUserToOutDto_withAllFields() {
        User user = User.builder()
                .userId(1L)
                .userName("john_doe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        String manager = "Alice Smith";
        String role = "ADMIN";

        UserOutDTO result = converter.userToOutDto(user, manager, role);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("john_doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Alice Smith", result.getManager());
        assertEquals("ADMIN", result.getRole());
    }

    /**
     * Role is null — should not set role field in DTO.
     */
    @Test
    void testUserToOutDto_withNullRole() {
        User user = User.builder()
                .userId(2L)
                .userName("jane_doe")
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        String manager = "Bob Manager";

        UserOutDTO result = converter.userToOutDto(user, manager, null);

        assertNotNull(result);
        assertEquals("jane_doe", result.getUsername());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Bob Manager", result.getManager());
        assertNull(result.getRole());
    }

    /**
     * Manager is null — should not set manager field in DTO.
     */
    @Test
    void testUserToOutDto_withNullManager() {
        User user = User.builder()
                .userId(3L)
                .userName("sam_smith")
                .email("sam@example.com")
                .firstName("Sam")
                .lastName("Smith")
                .build();

        String role = "USER";

        UserOutDTO result = converter.userToOutDto(user, null, role);

        assertNotNull(result);
        assertEquals("sam_smith", result.getUsername());
        assertEquals("sam@example.com", result.getEmail());
        assertEquals("Sam", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertNull(result.getManager());
        assertEquals("USER", result.getRole());
    }

    /**
     * User entity has all null fields — ensure no NPE and all fields are null in DTO.
     */
    @Test
    void testUserToOutDto_withNullUserFields() {
        User user = new User(); // all fields are null

        UserOutDTO result = converter.userToOutDto(user, "Test Manager", "MODERATOR");

        assertNull(result.getUserId());
        assertNull(result.getUsername());
        assertNull(result.getEmail());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertEquals("Test Manager", result.getManager());
        assertEquals("MODERATOR", result.getRole());
    }

    /**
     * User entity is null — should throw NullPointerException.
     */
    @Test
    void testUserToOutDto_withNullUser_shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> {
            converter.userToOutDto(null, "Manager", "Role");
        });
    }
}

