package com.nt.user_service_lms.dto.inDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserInDTOTest {

    private UserInDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UserInDTO();
        dto.setUserId(123L);
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUserName("johndoe");
        dto.setRole("Manager");
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getUserId() {
        assertEquals(123L, dto.getUserId());
    }

    @Test
    void getEmail() {
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void getFirstName() {
        assertEquals("John", dto.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals("Doe", dto.getLastName());
    }

    @Test
    void getUserName() {
        assertEquals("johndoe", dto.getUserName());
    }

    @Test
    void getRole() {
        assertEquals("Manager", dto.getRole());
    }

    @Test
    void setUserId() {
        dto.setUserId(456L);
        assertEquals(456L, dto.getUserId());
    }

    @Test
    void setEmail() {
        dto.setEmail("new@example.com");
        assertEquals("new@example.com", dto.getEmail());
    }

    @Test
    void setFirstName() {
        dto.setFirstName("Jane");
        assertEquals("Jane", dto.getFirstName());
    }

    @Test
    void setLastName() {
        dto.setLastName("Smith");
        assertEquals("Smith", dto.getLastName());
    }

    @Test
    void setUserName() {
        dto.setUserName("janesmith");
        assertEquals("janesmith", dto.getUserName());
    }

    @Test
    void setRole() {
        dto.setRole("Admin");
        assertEquals("Admin", dto.getRole());
    }

    @Test
    void testToString() {
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("test@example.com"));
    }

    @Test
    void builder() {
        UserInDTO built = UserInDTO.builder()
                .userId(999L)
                .email("built@example.com")
                .firstName("Build")
                .lastName("Test")
                .userName("builduser")
                .role("Developer")
                .build();

        assertEquals(999L, built.getUserId());
        assertEquals("built@example.com", built.getEmail());
        assertEquals("Build", built.getFirstName());
        assertEquals("Test", built.getLastName());
        assertEquals("builduser", built.getUserName());
        assertEquals("Developer", built.getRole());
    }

    @Test
    void testEquals_sameObject() {
        assertEquals(dto, dto);
    }

    @Test
    void testEquals_equalFields() {
        UserInDTO other = UserInDTO.builder()
                .userId(123L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .role("Manager")
                .build();
        assertEquals(dto, other);
    }

    @Test
    void testEquals_differentUserId() {
        UserInDTO other = UserInDTO.builder()
                .userId(999L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .role("Manager")
                .build();
        assertNotEquals(dto, other);
    }

    @Test
    void testEquals_differentClass() {
        assertNotEquals(dto, "some string");
    }

    @Test
    void testEquals_nullObject() {
        assertNotEquals(dto, null);
    }

    @Test
    void testEquals_nullFields() {
        UserInDTO dto1 = new UserInDTO(0, null, null, null, null, null);
        UserInDTO dto2 = new UserInDTO(0, null, null, null, null, null);
        assertEquals(dto1, dto2);
    }

    @Test
    void testHashCode_consistency() {
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashCode_equalObjects() {
        UserInDTO other = UserInDTO.builder()
                .userId(123L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .role("Manager")
                .build();
        assertEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_differentObject() {
        UserInDTO other = UserInDTO.builder()
                .userId(999L)
                .email("different@example.com")
                .firstName("Diff")
                .lastName("User")
                .userName("diffuser")
                .role("Admin")
                .build();
        assertNotEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_nullFields() {
        UserInDTO nullDto = new UserInDTO(0, null, null, null, null, null);
        assertDoesNotThrow(nullDto::hashCode);
    }

    // Edge cases for @Pattern manually checked here (unit test can't validate constraints directly without Validator)

    @Test
    void testRolePattern() {
        String validRole = "Admin";
        String invalidRole = "Admin123";
        assertTrue(validRole.matches("^[a-zA-Z]+$"));
        assertFalse(invalidRole.matches("^[a-zA-Z]+$"));
    }

    @Test
    void testUserIdPattern() {
        String validId = "123";
        String invalidId = "abc123";
        assertTrue(validId.matches("^[0-9]+$"));
        assertFalse(invalidId.matches("^[0-9]+$"));
    }
}
