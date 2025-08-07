package com.nt.user_service_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupTest {

    private Group group1;
    private Group group2;

    @BeforeEach
    void setUp() {
        group1 = Group.builder()
                .groupId(1L)
                .groupName("Study Group")
                .creatorId(100L)
                .isActive(true)
                .build();

        group2 = Group.builder()
                .groupId(1L)
                .groupName("Study Group")
                .creatorId(100L)
                .isActive(true)
                .build();
    }

    @Test
    void testGettersAndSetters() {
        Group group = new Group();
        group.setGroupId(10L);
        group.setGroupName("Dev Group");
        group.setCreatorId(200L);
        group.setActive(false);

        assertEquals(10L, group.getGroupId());
        assertEquals("Dev Group", group.getGroupName());
        assertEquals(200L, group.getCreatorId());
        assertFalse(group.isActive());
    }

    @Test
    void testAllArgsConstructor() {
        Group group = new Group(5L, "Backend Team", 300L, false);
        assertEquals(5L, group.getGroupId());
        assertEquals("Backend Team", group.getGroupName());
        assertEquals(300L, group.getCreatorId());
        assertFalse(group.isActive());
    }

    @Test
    void testCustomConstructor() {
        Group group = new Group("Frontend Team", 101L);
        assertNull(group.getGroupId());
        assertEquals("Frontend Team", group.getGroupName());
        assertEquals(101L, group.getCreatorId());
        assertTrue(group.isActive()); // Default value
    }

    @Test
    void testBuilderDefaultActiveTrue() {
        Group group = Group.builder()
                .groupName("Default Active Group")
                .creatorId(123L)
                .build();

        assertTrue(group.isActive()); // Field-level default
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        assertEquals(group1, group2);
        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        Group different = Group.builder()
                .groupId(2L)
                .groupName("New Group")
                .creatorId(102L)
                .isActive(false)
                .build();

        assertNotEquals(group1, different);
        assertNotEquals(group1.hashCode(), different.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        assertNotEquals(group1, null);
        assertNotEquals(group1, "string");
    }

    @Test
    void testEquals_selfReference() {
        assertEquals(group1, group1);
    }

    @Test
    void testTransitiveAndSymmetricEquals() {
        Group a = group1;
        Group b = Group.builder()
                .groupId(1L)
                .groupName("Study Group")
                .creatorId(100L)
                .isActive(true)
                .build();
        Group c = new Group("Study Group", 100L);
        c.setGroupId(1L);

        assertEquals(a, b); // symmetric
        assertEquals(b, c);
        assertEquals(a, c); // transitive
    }

    @Test
    void testEqualsWithNullFields() {
        Group g1 = new Group();
        Group g2 = new Group();

        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void testBuilderWithNullGroupName() {
        Group group = Group.builder()
                .groupId(20L)
                .groupName(null)
                .creatorId(300L)
                .isActive(true)
                .build();

        assertNull(group.getGroupName());
        assertEquals(300L, group.getCreatorId());
        assertEquals(20L, group.getGroupId());
        assertTrue(group.isActive());
    }

    @Test
    void testBuilderWithEmptyGroupName() {
        Group group = Group.builder()
                .groupId(30L)
                .groupName("")
                .creatorId(400L)
                .isActive(false)
                .build();

        assertEquals("", group.getGroupName());
        assertEquals(400L, group.getCreatorId());
        assertFalse(group.isActive());
    }
}
