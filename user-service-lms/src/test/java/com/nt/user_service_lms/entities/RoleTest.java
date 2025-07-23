package com.nt.user_service_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        role1 = Role.builder()
                .roleId(1L)
                .name("ADMIN")
                .build();

        role2 = Role.builder()
                .roleId(1L)
                .name("ADMIN")
                .build();
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Role role = new Role();
        role.setRoleId(10L);
        role.setName("USER");

        assertEquals(10L, role.getRoleId());
        assertEquals("USER", role.getName());
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(2L, "MANAGER");
        assertEquals(2L, role.getRoleId());
        assertEquals("MANAGER", role.getName());
    }

    @Test
    void testBuilder() {
        Role role = Role.builder()
                .roleId(3L)
                .name("DEVELOPER")
                .build();

        assertEquals(3L, role.getRoleId());
        assertEquals("DEVELOPER", role.getName());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        Role different = Role.builder()
                .roleId(2L)
                .name("TESTER")
                .build();

        assertNotEquals(role1, different);
        assertNotEquals(role1.hashCode(), different.hashCode());
    }

    @Test
    void testEquals_nullAndDifferentType() {
        assertNotEquals(role1, null);
        assertNotEquals(role1, "string");
    }

    @Test
    void testEquals_selfReference() {
        assertEquals(role1, role1);
    }

    @Test
    void testEquals_nullFields() {
        Role r1 = new Role();
        Role r2 = new Role();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testEdgeCase_emptyName() {
        Role emptyNameRole = Role.builder()
                .roleId(5L)
                .name("")
                .build();

        assertEquals("", emptyNameRole.getName());
    }

    @Test
    void testEdgeCase_nullName() {
        Role nullNameRole = Role.builder()
                .roleId(6L)
                .name(null)
                .build();

        assertNull(nullNameRole.getName());
    }

    @Test
    void testRoleIdOnlyEquality() {
        Role roleA = new Role(7L, null);
        Role roleB = new Role(7L, null);

        assertEquals(roleA, roleB);
        assertEquals(roleA.hashCode(), roleB.hashCode());
    }
}
