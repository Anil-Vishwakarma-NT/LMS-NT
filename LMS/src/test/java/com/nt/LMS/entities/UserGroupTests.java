package com.nt.LMS.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class UserGroupTests {

    private UserGroup userGroup;

    @BeforeEach
    public void setUp() {
        userGroup = new UserGroup(1L, 2L);
    }

    @Test
    public void testNoArgsConstructor() {
        UserGroup userGroup = new UserGroup();
        assertNotNull(userGroup);
    }

    @Test
    public void testAllArgsConstructor() {
        UserGroup userGroup = new UserGroup(1L,11L, 2L);
        assertEquals(1L, userGroup.getId());
        assertEquals(11L, userGroup.getUserId());
        assertEquals(2L, userGroup.getGroupId());
    }

    @Test
    public void testGettersAndSetters() {
        userGroup.setUserId(3L);
        userGroup.setGroupId(4L);

        assertEquals(3L, userGroup.getUserId());
        assertEquals(4L, userGroup.getGroupId());
    }

    @Test
    public void testToString() {
        String expectedToString = "UserGroup(id=null, userId=1, groupId=2)";
        assertTrue(userGroup.toString().contains("userId=1"));
        assertTrue(userGroup.toString().contains("groupId=2"));
    }

    @Test
    public void testEqualsAndHashCode() {
        UserGroup group1 = new UserGroup(1L, 2L);
        UserGroup group2 = new UserGroup(1L, 2L);
        UserGroup group3 = new UserGroup(3L, 4L);

        assertEquals(group1, group2);
        assertEquals(group1.hashCode(), group2.hashCode());

        assertNotEquals(group1, group3);
        assertNotEquals(group1.hashCode(), group3.hashCode());
    }
}
