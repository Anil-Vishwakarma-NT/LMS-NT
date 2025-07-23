package com.nt.user_service_lms.dto.inDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenRequestDtoTest {

    private TokenRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = new TokenRequestDto();
        dto.setRefreshToken("sample-refresh-token");
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getRefreshToken() {
        assertEquals("sample-refresh-token", dto.getRefreshToken());
    }

    @Test
    void setRefreshToken() {
        dto.setRefreshToken("new-token");
        assertEquals("new-token", dto.getRefreshToken());
    }

    @Test
    void testEquals_sameObject() {
        assertEquals(dto, dto);
    }

    @Test
    void testEquals_equalFields() {
        TokenRequestDto other = TokenRequestDto.builder()
                .refreshToken("sample-refresh-token")
                .build();
        assertEquals(dto, other);
    }

    @Test
    void testEquals_differentValues() {
        TokenRequestDto other = TokenRequestDto.builder()
                .refreshToken("different-token")
                .build();
        assertNotEquals(dto, other);
    }

    @Test
    void testEquals_nullObject() {
        assertNotEquals(dto, null);
    }

    @Test
    void testEquals_differentClass() {
        assertNotEquals(dto, "some string");
    }

    @Test
    void testEquals_bothNullTokens() {
        TokenRequestDto dto1 = new TokenRequestDto(null);
        TokenRequestDto dto2 = new TokenRequestDto(null);
        assertEquals(dto1, dto2);
    }

    @Test
    void testEquals_oneNullToken() {
        TokenRequestDto dto1 = new TokenRequestDto(null);
        TokenRequestDto dto2 = new TokenRequestDto("non-null");
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testHashCode_equalObjects() {
        TokenRequestDto other = new TokenRequestDto("sample-refresh-token");
        assertEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_differentObjects() {
        TokenRequestDto other = new TokenRequestDto("another-token");
        assertNotEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_nullToken() {
        TokenRequestDto dto1 = new TokenRequestDto(null);
        assertDoesNotThrow(() -> dto1.hashCode());
    }

    @Test
    void testToString_containsValue() {
        String result = dto.toString();
        assertTrue(result.contains("sample-refresh-token"));
    }

    @Test
    void testToString_notNull() {
        assertNotNull(dto.toString());
    }

    @Test
    void builder() {
        TokenRequestDto built = TokenRequestDto.builder()
                .refreshToken("built-token")
                .build();

        assertEquals("built-token", built.getRefreshToken());
    }
}
