package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class UserOutDtoTests {

    private UserOutDTO userOutDTO;

    @BeforeEach
    public void setUp() {
        userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(1L);
        userOutDTO.setUsername("username");
        userOutDTO.setFirstName("first");
        userOutDTO.setLastName("last");
        userOutDTO.setEmail("user@example.com");
        userOutDTO.setManager("manager");
        userOutDTO.setMessage("message");
    }

    @Test
    public void testNoArgsConstructor() {
        UserOutDTO dto = new UserOutDTO();
        assertNotNull(dto);
    }

    @Test
    public void testAllArgsConstructor() {
        UserOutDTO dto = new UserOutDTO(1L, "username", "first", "last", "user@example.com", "manager", "message");
        assertEquals(1L, dto.getUserId());
        assertEquals("username", dto.getUsername());
        assertEquals("first", dto.getFirstName());
        assertEquals("last", dto.getLastName());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("manager", dto.getManager());
        assertEquals("message", dto.getMessage());
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(1L, userOutDTO.getUserId());
        assertEquals("username", userOutDTO.getUsername());
        assertEquals("first", userOutDTO.getFirstName());
        assertEquals("last", userOutDTO.getLastName());
        assertEquals("user@example.com", userOutDTO.getEmail());
        assertEquals("manager", userOutDTO.getManager());
        assertEquals("message", userOutDTO.getMessage());

        userOutDTO.setUserId(2L);
        userOutDTO.setUsername("username2");
        userOutDTO.setFirstName("first2");
        userOutDTO.setLastName("last2");
        userOutDTO.setEmail("user2@example.com");
        userOutDTO.setManager("manager2");
        userOutDTO.setMessage("message2");

        assertEquals(2L, userOutDTO.getUserId());
        assertEquals("username2", userOutDTO.getUsername());
        assertEquals("first2", userOutDTO.getFirstName());
        assertEquals("last2", userOutDTO.getLastName());
        assertEquals("user2@example.com", userOutDTO.getEmail());
        assertEquals("manager2", userOutDTO.getManager());
        assertEquals("message2", userOutDTO.getMessage());
    }

    @Test
    public void testToString() {
        String expectedStart = "UserOutDTO(userId=1, username=username, firstName=first, lastName=last, email=user@example.com, manager=manager, message=message)";
        assertEquals(expectedStart, userOutDTO.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        UserOutDTO dto1 = new UserOutDTO(1L, "username", "first", "last", "user@example.com", "manager", "message");
        UserOutDTO dto2 = new UserOutDTO(1L, "username", "first", "last", "user@example.com", "manager", "message");
        UserOutDTO dto3 = new UserOutDTO(2L, "username2", "first2", "last2", "user2@example.com", "manager2", "message2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }
}
