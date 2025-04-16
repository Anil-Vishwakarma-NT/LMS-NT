package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class TokenResponseDtoTests {

    private TokenResponseDto tokenResponseDto;

    @BeforeEach
    void setUp() {
        tokenResponseDto = new TokenResponseDto("access", "refresh", "Bearer");
    }

    @Test
    void testGetters() {
        assertEquals("access", tokenResponseDto.getAccessToken());
        assertEquals("refresh", tokenResponseDto.getRefreshToken());
        assertEquals("Bearer", tokenResponseDto.getTokenType());
    }

    @Test
    void testSetters() {
        tokenResponseDto.setAccessToken("new-access");
        tokenResponseDto.setRefreshToken("new-refresh");
        tokenResponseDto.setTokenType("NewType");

        assertEquals("new-access", tokenResponseDto.getAccessToken());
        assertEquals("new-refresh", tokenResponseDto.getRefreshToken());
        assertEquals("NewType", tokenResponseDto.getTokenType());
    }

    @Test
    void testToString() {
        String expected = "TokenResponseDto(accessToken=access, refreshToken=refresh, tokenType=Bearer)";
        assertEquals(expected, tokenResponseDto.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        TokenResponseDto dto1 = new TokenResponseDto("access", "refresh", "Bearer");
        TokenResponseDto dto2 = new TokenResponseDto("access", "refresh", "Bearer");
        TokenResponseDto dto3 = new TokenResponseDto("access2", "refresh2", "Other");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
    }

    @Test
    void testAllArgsConstructor() {
        TokenResponseDto dto = new TokenResponseDto("access", "refresh", "Bearer");
        assertEquals("access", dto.getAccessToken());
        assertEquals("refresh", dto.getRefreshToken());
        assertEquals("Bearer", dto.getTokenType());
    }
}
