package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class RegisterDtoTests {

    private RegisterDto registerDto;

    @BeforeEach
    public void setUp() {

        registerDto = new RegisterDto();
        registerDto.setFirstName("first");
        registerDto.setLastName("last");
        registerDto.setUserName("username");
        registerDto.setEmail("user@example.com");
        registerDto.setPassword("password");
        registerDto.setRoleId(1L);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals("first", registerDto.getFirstName());
        assertEquals("last", registerDto.getLastName());
        assertEquals("username", registerDto.getUserName());
        assertEquals("user@example.com", registerDto.getEmail());
        assertEquals("password", registerDto.getPassword());
        assertEquals(1L, registerDto.getRoleId());
    }

    @Test
    public void testToString() {
        String expectedStart = "RegisterDto(";
        assertTrue(registerDto.toString().startsWith(expectedStart));
    }

    @Test
    public void testEqualsAndHashCode() {
        RegisterDto dto1 = new RegisterDto();
        dto1.setFirstName("first");
        dto1.setLastName("last");
        dto1.setUserName("username");
        dto1.setEmail("user@example.com");
        dto1.setPassword("password");
        dto1.setRoleId(1L);

        assertEquals(dto1, registerDto);
        assertEquals(dto1.hashCode(), registerDto.hashCode());
    }

}
