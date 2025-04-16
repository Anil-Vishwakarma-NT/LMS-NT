package com.nt.LMS.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
public class RefreshTokenTests {


    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = new RefreshToken();
        refreshToken.setUserId(1L);
        refreshToken.setToken("refresh-token");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
    }

    @Test
    void testGetters() {
        assertEquals(1L, refreshToken.getUserId());
        assertEquals("refresh-token", refreshToken.getToken());
        assertNotNull(refreshToken.getExpiryDate());
    }

    @Test
    void testSetters() {
        refreshToken.setUserId(2L);
        refreshToken.setToken("new-refresh-token");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7200));

        assertEquals(2L, refreshToken.getUserId());
        assertEquals("new-refresh-token", refreshToken.getToken());
        assertNotNull(refreshToken.getExpiryDate());
    }

    @Test
    void testToString() {
        String expected = "RefreshToken(id=null, userId=1, token=refresh-token, expiryDate=" + refreshToken.getExpiryDate() + ")";
        assertEquals(expected, refreshToken.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        RefreshToken token1 = new RefreshToken();
        token1.setUserId(1L);
        token1.setToken("refresh-token");
        token1.setExpiryDate(Instant.now().plusSeconds(3600));

        RefreshToken token2 = new RefreshToken();
        token2.setUserId(1L);
        token2.setToken("refresh-token");
        token2.setExpiryDate(Instant.now().plusSeconds(3600));

        RefreshToken token3 = new RefreshToken();
        token3.setUserId(2L);
        token3.setToken("refresh-token-2");
        token3.setExpiryDate(Instant.now().plusSeconds(7200));

        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());

        assertNotEquals(token1, token3);
        assertNotEquals(token1.hashCode(), token3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        RefreshToken token = new RefreshToken();
        assertNotNull(token);
    }

    @Test
    void testAllArgsConstructor() {
        RefreshToken token = new RefreshToken();
        token.setUserId(1L);
        token.setToken("token");
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        assertEquals(1L, token.getUserId());
        assertEquals("token", token.getToken());
        assertNotNull(token.getExpiryDate());
    }
}
