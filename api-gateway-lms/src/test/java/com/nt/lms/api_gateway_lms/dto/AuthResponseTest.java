package com.nt.lms.api_gateway_lms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        AuthResponse response = new AuthResponse("accessToken", "refreshToken", "Bearer", 3600L);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void testBuilderPattern() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("token1")
                .refreshToken("token2")
                .tokenType("Bearer")
                .expiresIn(7200L)
                .build();

        assertEquals("token1", response.getAccessToken());
        assertEquals("token2", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(7200L, response.getExpiresIn());
    }

    @Test
    void testSetters() {
        AuthResponse response = new AuthResponse();
        response.setAccessToken("a");
        response.setRefreshToken("r");
        response.setTokenType("type");
        response.setExpiresIn(123L);

        assertEquals("a", response.getAccessToken());
        assertEquals("r", response.getRefreshToken());
        assertEquals("type", response.getTokenType());
        assertEquals(123L, response.getExpiresIn());
    }
    @Test
    void testAuthResponseBuilder() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access123")
                .refreshToken("refresh456")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();

        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh456", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

}
