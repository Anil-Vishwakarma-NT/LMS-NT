package com.nt.LMS.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class RoleTests {


    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("Role");
    }

    @Test
    void testGetters() {
        assertEquals("Role", role.getName());
    }

    @Test
    void testSetters() {
        role.setName("Role");
        assertEquals("Role", role.getName());
    }

    @Test
    void testToString() {
        String expected = "Role(roleId=null, name=Role)";
        assertEquals(expected, role.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role();
        role1.setName("Role");

        Role role2 = new Role();
        role2.setName("Role");

        Role role3 = new Role();
        role3.setName("Role2");

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());

        assertNotEquals(role1, role3);
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();
        assertNotNull(role);
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role();
        role.setName("Role");

        assertEquals("Role", role.getName());
    }
}
