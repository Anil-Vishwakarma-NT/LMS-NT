package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class UserInDtoTests {
    private UserInDTO userInDTO;

    @BeforeEach
    void setUp() {
        userInDTO = new UserInDTO(1L, "Role");
    }

    @Test
    void testGetters() {
        assertEquals(1L, userInDTO.getUserId());
        assertEquals("Role", userInDTO.getRole());
    }

    @Test
    void testSetters() {
        userInDTO.setUserId(2L);
        userInDTO.setRole("Role2");

        assertEquals(2L, userInDTO.getUserId());
        assertEquals("Role2", userInDTO.getRole());
    }

    @Test
    void testToString() {
        String expected = "UserInDTO(userId=1, Role=Role)";
        assertEquals(expected, userInDTO.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        UserInDTO dto1 = new UserInDTO(1L, "Role");
        UserInDTO dto2 = new UserInDTO(1L, "Role");
        UserInDTO dto3 = new UserInDTO(2L, "Role2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testNoArgsConstructor() {
        UserInDTO dto = new UserInDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        UserInDTO dto = new UserInDTO(10L, "Role");
        assertEquals(10L, dto.getUserId());
        assertEquals("Role", dto.getRole());
    }
}
