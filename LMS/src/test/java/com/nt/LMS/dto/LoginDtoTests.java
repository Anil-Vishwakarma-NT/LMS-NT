package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class LoginDtoTests {


    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("password");
    }

    @Test
    void testGetters() {
        assertEquals("user@example.com", loginDto.getEmail());
        assertEquals("password", loginDto.getPassword());
    }

    @Test
    void testSetters() {
        loginDto.setEmail("newuser@example.com");
        loginDto.setPassword("newPassword");

        assertEquals("newuser@example.com", loginDto.getEmail());
        assertEquals("newPassword", loginDto.getPassword());
    }

    @Test
    void testToString() {
        String expected = "LoginDto(email=user@example.com, password=password)";
        assertEquals(expected, loginDto.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginDto dto1 = new LoginDto("user@example.com", "password");
        LoginDto dto2 = new LoginDto("user@example.com", "password");
        LoginDto dto3 = new LoginDto("other@example.com", "otherPassword");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        LoginDto dto = new LoginDto();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        LoginDto dto = new LoginDto("test@example.com", "password123");

        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }
}
