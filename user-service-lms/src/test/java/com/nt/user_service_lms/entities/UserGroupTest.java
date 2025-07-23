package com.nt.user_service_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserGroupTest {

    private UserGroup ug1;
    private UserGroup ug2;

    @BeforeEach
    void setUp() {
        ug1 = UserGroup.builder()
                .id(1L)
                .userId(101L)
                .groupId(202L)
                .isActive(true)
                .build();

        ug2 = UserGroup.builder()
                .id(1L)
                .userId(101L)
                .groupId(202L)
                .isActive(true)
                .build();
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(10L);
        userGroup.setUserId(111L);
        userGroup.setGroupId(222L);
        userGroup.setActive(false);

        assertEquals(10L, userGroup.getId());
        assertEquals(111L, userGroup.getUserId());
        assertEquals(222L, userGroup.getGroupId());
        assertFalse(userGroup.isActive());
    }

    @Test
    void testAllArgsConstructor() {
        UserGroup userGroup = new UserGroup(5L, 66L, 77L, false);
        assertEquals(5L, userGroup.getId());
        assertEquals(66L, userGroup.getUserId());
        assertEquals(77L, userGroup.getGroupId());
        assertFalse(userGroup.isActive());
    }

    @Test
    void testCustomConstructor() {
        UserGroup userGroup = new UserGroup(99L, 88L);
        assertEquals(99L, userGroup.getUserId());
        assertEquals(88L, userGroup.getGroupId());
        assertTrue(userGroup.isActive()); // default value
    }

    @Test
    void testBuilderPattern() {
        UserGroup userGroup = UserGroup.builder()
                .id(12L)
                .userId(123L)
                .groupId(456L)
                .isActive(false)
                .build();

        assertEquals(12L, userGroup.getId());
        assertEquals(123L, userGroup.getUserId());
        assertEquals(456L, userGroup.getGroupId());
        assertFalse(userGroup.isActive());
    }

    @Test
    void testEquals_sameObject() {
        assertEquals(ug1, ug1);
    }

    @Test
    void testEquals_equalObjects() {
        assertEquals(ug1, ug2);
        assertEquals(ug1.hashCode(), ug2.hashCode());
    }

    @Test
    void testEquals_differentObjects() {
        UserGroup different = UserGroup.builder()
                .id(2L)
                .userId(105L)
                .groupId(206L)
                .isActive(false)
                .build();

        assertNotEquals(ug1, different);
        assertNotEquals(ug1.hashCode(), different.hashCode());
    }

    @Test
    void testEquals_nullAndOtherType() {
        assertNotEquals(ug1, null);
        assertNotEquals(ug1, "not a user group");
    }

    @Test
    void testEquals_nullFields() {
        UserGroup u1 = new UserGroup();
        UserGroup u2 = new UserGroup();

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testEdgeCase_negativeIds() {
        UserGroup userGroup = UserGroup.builder()
                .id(-1L)
                .userId(-5L)
                .groupId(-10L)
                .isActive(false)
                .build();

        assertEquals(-1L, userGroup.getId());
        assertEquals(-5L, userGroup.getUserId());
        assertEquals(-10L, userGroup.getGroupId());
        assertFalse(userGroup.isActive());
    }

    @Test
    void testEdgeCase_nullIds() {
        UserGroup userGroup1 = UserGroup.builder()
                .id(null)
                .userId(null)
                .groupId(null)
                .build();

        UserGroup userGroup2 = UserGroup.builder()
                .id(null)
                .userId(null)
                .groupId(null)
                .build();

        assertEquals(userGroup1, userGroup2);
        assertEquals(userGroup1.hashCode(), userGroup2.hashCode());
    }
}
