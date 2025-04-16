package com.nt.LMS.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class MessageOutDtoTests {

    private MessageOutDto messageOutDto;

    @BeforeEach
    void setUp() {
        messageOutDto = new MessageOutDto();
        messageOutDto.setMessage("Successful");
    }

    @Test
    void testGetters() {
        assertEquals("Successful", messageOutDto.getMessage());
    }

    @Test
    void testSetters() {
        messageOutDto.setMessage("Successful");
        assertEquals("Successful", messageOutDto.getMessage());
    }

    @Test
    void testToString() {
        String expected = "MessageOutDto(message=Successful)";
        assertEquals(expected, messageOutDto.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        MessageOutDto dto1 = new MessageOutDto("Successful");
        MessageOutDto dto2 = new MessageOutDto("Successful");
        MessageOutDto dto3 = new MessageOutDto("Something went wrong");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        MessageOutDto dto = new MessageOutDto();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        MessageOutDto dto = new MessageOutDto("Successful");
        assertEquals("Successful", dto.getMessage());
    }
}
