package com.nt.LMS.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class GroupTests {

    private Group group;

    @BeforeEach
    public void setUp() {
        group = new Group("Development Team", 1L);
    }

    @Test
    public void testNoArgsConstructor() {
        Group group = new Group();
        assertNotNull(group);
    }

    @Test
    public void testAllArgsConstructor() {
        Group group = new Group(1L, "Test", 1L);
        assertEquals(1L, group.getGroupId());
        assertEquals("Test", group.getGroupName());
        assertEquals(1L, group.getCreatorId());
    }

    @Test
    public void testConstructorWithNameAndCreatorId() {
        Group group = new Group("Test", 2L);
        assertEquals("Test", group.getGroupName());
        assertEquals(2L, group.getCreatorId());
    }

    @Test
    public void testGettersAndSetters() {
        group.setGroupName("Test2");
        group.setCreatorId(3L);

        assertEquals("Test2", group.getGroupName());
        assertEquals(3L, group.getCreatorId());
    }

}
