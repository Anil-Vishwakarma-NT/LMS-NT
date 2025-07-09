package com.nt.lms.api_gateway_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId(1L);
        user.setUserName("dummyUser");
        user.setFirstName("Dummy");
        user.setLastName("User");
        user.setEmail("dummy@example.com");
        user.setPassword("dummyPassword");
        user.setManagerId(2L);
        user.setRoleId(1L);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setActive(true);
    }

    @Test
    void testUserFieldsSetCorrectly() {
        assertEquals(1L, user.getUserId());
        assertEquals("dummyUser", user.getUserName());
        assertEquals("Dummy", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("dummy@example.com", user.getEmail());
        assertEquals("dummyPassword", user.getPassword());
        assertEquals(2L, user.getManagerId());
        assertEquals(1L, user.getRoleId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.isActive());
    }

    @Test
    void testDefaultManagerIdConstructor() {
        Users newUser = new Users();
        assertEquals(1L, newUser.getManagerId());
    }

    @Test
    void testActiveDefaultValue() {
        Users newUser = new Users();
        assertTrue(newUser.isActive());
    }
}
