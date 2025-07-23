package com.nt.user_service_lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private RefreshToken token1;
    private RefreshToken token2;
    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        token1 = RefreshToken.builder()
                .id(1L)
                .userId(100L)
                .token("abc123")
                .expiryDate(now)
                .build();

        token2 = RefreshToken.builder()
                .id(1L)
                .userId(100L)
                .token("abc123")
                .expiryDate(now)
                .build();
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        RefreshToken token = new RefreshToken();
        Instant expiry = Instant.now().plusSeconds(3600);
        token.setId(10L);
        token.setUserId(200L);
        token.setToken("xyz789");
        token.setExpiryDate(expiry);

        assertEquals(10L, token.getId());
        assertEquals(200L, token.getUserId());
        assertEquals("xyz789", token.getToken());
        assertEquals(expiry, token.getExpiryDate());
    }

    @Test
    void testAllArgsConstructor() {
        Instant expiry = Instant.now().plusSeconds(600);
        RefreshToken token = new RefreshToken(2L, 101L, "def456", expiry);

        assertEquals(2L, token.getId());
        assertEquals(101L, token.getUserId());
        assertEquals("def456", token.getToken());
        assertEquals(expiry, token.getExpiryDate());
    }

    @Test
    void testBuilderPattern() {
        RefreshToken token = RefreshToken.builder()
                .id(3L)
                .userId(150L)
                .token("builderToken")
                .expiryDate(now)
                .build();

        assertEquals(3L, token.getId());
        assertEquals(150L, token.getUserId());
        assertEquals("builderToken", token.getToken());
        assertEquals(now, token.getExpiryDate());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        RefreshToken different = RefreshToken.builder()
                .id(2L)
                .userId(300L)
                .token("different")
                .expiryDate(now.plusSeconds(1000))
                .build();

        assertNotEquals(token1, different);
        assertNotEquals(token1.hashCode(), different.hashCode());
    }

    @Test
    void testEquals_nullAndDifferentType() {
        assertNotEquals(token1, null);
        assertNotEquals(token1, "string");
    }

    @Test
    void testEquals_sameReference() {
        assertEquals(token1, token1);
    }

    @Test
    void testEquals_withNullFields() {
        RefreshToken t1 = new RefreshToken();
        RefreshToken t2 = new RefreshToken();

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testEdgeCase_emptyTokenString() {
        RefreshToken token = RefreshToken.builder()
                .id(5L)
                .userId(999L)
                .token("")
                .expiryDate(now)
                .build();

        assertEquals("", token.getToken());
        assertEquals(999L, token.getUserId());
    }

    @Test
    void testExpiredTokenValue() {
        Instant past = Instant.now().minusSeconds(1000);
        RefreshToken expiredToken = RefreshToken.builder()
                .id(6L)
                .userId(888L)
                .token("expired")
                .expiryDate(past)
                .build();

        assertTrue(expiredToken.getExpiryDate().isBefore(Instant.now()));
    }
}
