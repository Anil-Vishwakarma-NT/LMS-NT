package com.nt.LMS.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class UserTests {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserName("username");
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setManagerId(1L);
        user.setRoleId(2L);
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
    }

    @Test
    public void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertEquals(Long.valueOf(3), user.getManagerId());
    }

    @Test
    public void testAllArgsConstructor() {
        User user = new User(1L, "username", "first", "last", "user@example.com", "password", 2L, 3L, new java.util.Date(), new java.util.Date());
        assertEquals(1L, user.getUserId());
        assertEquals("username", user.getUserName());
        assertEquals("first", user.getFirstName());
        assertEquals("last", user.getLastName());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(2L, user.getManagerId());
        assertEquals(3L, user.getRoleId());
    }

    @Test
    public void testGettersAndSetters() {
        user.setUserName("username2");
        user.setFirstName("first2");
        user.setLastName("last2");
        user.setEmail("user2@example.com");
        user.setPassword("password");
        user.setManagerId(2L);
        user.setRoleId(3L);

        assertEquals("username2", user.getUserName());
        assertEquals("first2", user.getFirstName());
        assertEquals("last2", user.getLastName());
        assertEquals("user2@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(2L, user.getManagerId());
        assertEquals(3L, user.getRoleId());
    }

    @Test
    public void testToString() {
        String expectedStart = "User(userId=0, userName=username, firstName=first, lastName=last, email=user@example.com, password=password, managerId=1, roleId=2, createdAt=";
        assertTrue(user.toString().startsWith(expectedStart));
    }

    @Test
    public void testEqualsAndHashCode() {
        User user1 = new User(1L, "username", "first", "last", "user1@example.com", "password", 2L, 3L, new java.util.Date(), new java.util.Date());
        User user2 = new User(1L, "username", "first", "last", "user1@example.com", "password", 2L, 3L, new java.util.Date(), new java.util.Date());
        User user3 = new User(2L, "username2", "first", "last", "user2@example.com", "Password", 3L, 4L, new java.util.Date(), new java.util.Date());

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}
