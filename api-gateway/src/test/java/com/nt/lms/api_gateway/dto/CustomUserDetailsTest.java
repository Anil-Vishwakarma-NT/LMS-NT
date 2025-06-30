package com.nt.lms.api_gateway.dto;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CustomUserDetailsTest {

    @Test
    void testGetters() {
        CustomUserDetails userDetails = new CustomUserDetails(
                "1", "test@nucleusteq.com", "pass123",
                "Test User", true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertEquals("1", userDetails.getUserId());
        assertEquals("test@nucleusteq.com", userDetails.getEmail());
        assertEquals("pass123", userDetails.getPassword());
        assertEquals("test@nucleusteq.com", userDetails.getUsername());
        assertEquals("Test User", userDetails.getFullName());
        assertTrue(userDetails.isActive());
        assertEquals(1, userDetails.getAuthorities().size());
    }
}
