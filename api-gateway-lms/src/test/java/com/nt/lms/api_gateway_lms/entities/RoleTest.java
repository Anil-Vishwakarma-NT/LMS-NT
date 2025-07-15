package com.nt.lms.api_gateway_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRoleId(1L);
        role.setName("ADMIN");
    }

    @Test
    void testRoleFieldsSetCorrectly() {
        assertEquals(1L, role.getRoleId());
        assertEquals("ADMIN", role.getName());
    }
}
