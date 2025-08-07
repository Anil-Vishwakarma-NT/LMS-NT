package com.nt.user_service_lms.dto.inDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterDtoTest {

    private RegisterDto dto;

    @BeforeEach
    void setUp() {
        dto = new RegisterDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUserName("john.doe");
        dto.setEmail("john.doe@nucleusteq.com");
        dto.setPassword("Password@1");
        dto.setRoleId(2L);
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    // Setters
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
        dto.setUserName("jsmith");
        assertEquals("jsmith", dto.getUserName());
    }

    @Test
    void setEmail() {
        dto.setEmail("jsmith@nucleusteq.com");
        assertEquals("jsmith@nucleusteq.com", dto.getEmail());
    }

    @Test
    void setPassword() {
        dto.setPassword("Secure@123");
        assertEquals("Secure@123", dto.getPassword());
    }

    @Test
    void setRoleId() {
        dto.setRoleId(5L);
        assertEquals(5L, dto.getRoleId());
    }

    // Getters
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
        assertEquals("john.doe", dto.getUserName());
    }

    @Test
    void getEmail() {
        assertEquals("john.doe@nucleusteq.com", dto.getEmail());
    }

    @Test
    void getPassword() {
        assertEquals("Password@1", dto.getPassword());
    }

    @Test
    void getRoleId() {
        assertEquals(2L, dto.getRoleId());
    }

    // Equals
    @Test
    void testEquals_sameObject() {
        assertEquals(dto, dto);
    }

    @Test
    void testEquals_equalFields() {
        RegisterDto other = RegisterDto.builder()
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe")
                .email("john.doe@nucleusteq.com")
                .password("Password@1")
                .roleId(2L)
                .build();

        assertEquals(dto, other);
    }

    @Test
    void testEquals_notEqualFields() {
        RegisterDto other = RegisterDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .userName("jane.doe")
                .email("jane.doe@nucleusteq.com")
                .password("Password@2")
                .roleId(3L)
                .build();

        assertNotEquals(dto, other);
    }

    @Test
    void testEquals_null() {
        assertNotEquals(dto, null);
    }

    @Test
    void testEquals_differentClass() {
        assertNotEquals(dto, "Not a DTO");
    }

    // HashCode
    @Test
    void testHashCode_equalObjects() {
        RegisterDto other = RegisterDto.builder()
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe")
                .email("john.doe@nucleusteq.com")
                .password("Password@1")
                .roleId(2L)
                .build();

        assertEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_unequal() {
        RegisterDto other = RegisterDto.builder().firstName("Diff").build();
        assertNotEquals(dto.hashCode(), other.hashCode());
    }

    // ToString
    @Test
    void testToString_notNull() {
        assertNotNull(dto.toString());
    }

    @Test
    void testToString_containsValues() {
        String str = dto.toString();
        assertTrue(str.contains("John"));
        assertTrue(str.contains("john.doe@nucleusteq.com"));
        assertTrue(str.contains("Password@1"));
    }

    // Builder
    @Test
    void builder() {
        RegisterDto built = RegisterDto.builder()
                .firstName("Alice")
                .lastName("Wonder")
                .userName("alice.w")
                .email("alice.w@nucleusteq.com")
                .password("Alic3@123")
                .roleId(9L)
                .build();

        assertEquals("Alice", built.getFirstName());
        assertEquals("Wonder", built.getLastName());
        assertEquals("alice.w", built.getUserName());
        assertEquals("alice.w@nucleusteq.com", built.getEmail());
        assertEquals("Alic3@123", built.getPassword());
        assertEquals(9L, built.getRoleId());
    }

    // Edge case: null fields
    @Test
    void testNullFields() {
        RegisterDto empty = new RegisterDto(null, null, null, null, null, null);

        assertNull(empty.getFirstName());
        assertNull(empty.getLastName());
        assertNull(empty.getUserName());
        assertNull(empty.getEmail());
        assertNull(empty.getPassword());
        assertNull(empty.getRoleId());
    }
}
