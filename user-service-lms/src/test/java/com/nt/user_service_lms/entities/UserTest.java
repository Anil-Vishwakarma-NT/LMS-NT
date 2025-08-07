package com.nt.user_service_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User user1;
    private User user2;
    private Date now;

    @BeforeEach
    void setUp() {
        now = new Date();

        user1 = User.builder()
                .userId(1L)
                .userName("johndoe")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securepassword")
                .managerId(100L)
                .roleId(2L)
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build();

        user2 = User.builder()
                .userId(1L)
                .userName("johndoe")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securepassword")
                .managerId(100L)
                .roleId(2L)
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build();
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = new User();
        user.setUserId(2L);
        user.setUserName("janedoe");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("anotherpass");
        user.setManagerId(101L);
        user.setRoleId(3L);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setActive(false);

        assertEquals(2L, user.getUserId());
        assertEquals("janedoe", user.getUserName());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("jane.doe@example.com", user.getEmail());
        assertEquals("anotherpass", user.getPassword());
        assertEquals(101L, user.getManagerId());
        assertEquals(3L, user.getRoleId());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertFalse(user.isActive());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(3L, "alice", "Alice", "Smith", "alice@example.com", "pw123",
                102L, 4L, now, now, true);

        assertEquals(3L, user.getUserId());
        assertEquals("alice", user.getUserName());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("pw123", user.getPassword());
        assertEquals(102L, user.getManagerId());
        assertEquals(4L, user.getRoleId());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertTrue(user.isActive());
    }

    @Test
    void testBuilder() {
        User user = User.builder()
                .userId(4L)
                .userName("bob")
                .firstName("Bob")
                .lastName("Jones")
                .email("bob@example.com")
                .password("bobpass")
                .managerId(null)
                .roleId(5L)
                .createdAt(now)
                .updatedAt(null)
                .active(false)
                .build();

        assertEquals("bob", user.getUserName());
        assertNull(user.getManagerId());
        assertNull(user.getUpdatedAt());
        assertFalse(user.isActive());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        User differentUser = User.builder()
                .userId(9L)
                .userName("someone")
                .firstName("Some")
                .lastName("One")
                .email("someone@example.com")
                .password("pass")
                .managerId(1L)
                .roleId(1L)
                .createdAt(new Date())
                .updatedAt(new Date())
                .active(false)
                .build();

        assertNotEquals(user1, differentUser);
        assertNotEquals(user1.hashCode(), differentUser.hashCode());
    }

    @Test
    void testEqualsWithNullAndOtherClass() {
        assertNotEquals(user1, null);
        assertNotEquals(user1, "notAUser");
    }

    @Test
    void testEqualsSelfReference() {
        assertEquals(user1, user1);
    }

    @Test
    void testEqualsNullFields() {
        User u1 = new User();
        User u2 = new User();

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testEdgeCase_emptyStrings() {
        User user = User.builder()
                .userName("")
                .firstName("")
                .lastName("")
                .email("")
                .password("")
                .build();

        assertEquals("", user.getUserName());
        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPassword());
    }

    @Test
    void testEdgeCase_nullDates() {
        User user = User.builder()
                .createdAt(null)
                .updatedAt(null)
                .build();

        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }
}
