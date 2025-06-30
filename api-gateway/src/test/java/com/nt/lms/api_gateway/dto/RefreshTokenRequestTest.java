package com.nt.lms.api_gateway.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenRequestTest {

    @Test
    void testConstructorAndGetter() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh123");
        assertEquals("refresh123", request.getRefreshToken());
    }

    @Test
    void testSetters() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("token456");
        assertEquals("token456", request.getRefreshToken());
    }

    @Test
    void testEqualsHashCodeToString() {
        RefreshTokenRequest r1 = new RefreshTokenRequest("tokenABC");
        RefreshTokenRequest r2 = new RefreshTokenRequest("tokenABC");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("tokenABC"));
    }
}
