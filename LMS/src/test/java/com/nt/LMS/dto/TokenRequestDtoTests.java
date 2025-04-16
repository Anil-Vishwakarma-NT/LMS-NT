package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class TokenRequestDtoTests {

    private TokenRequestDto tokenRequestDto;

    @BeforeEach
    public void setUp() {
        tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setRefreshToken("refresh-token");
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals("refresh-token", tokenRequestDto.getRefreshToken());
        tokenRequestDto.setRefreshToken("new-token");
        assertEquals("new-token", tokenRequestDto.getRefreshToken());
    }

    @Test
    public void testToString() {
        String expectedStart = "TokenRequestDto(";
        assertTrue(tokenRequestDto.toString().startsWith(expectedStart));
    }

    @Test
    public void testEqualsAndHashCode() {
        TokenRequestDto dto1 = new TokenRequestDto();
        dto1.setRefreshToken("refresh-token");

        TokenRequestDto dto2 = new TokenRequestDto();
        dto2.setRefreshToken("refresh-token");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

}
