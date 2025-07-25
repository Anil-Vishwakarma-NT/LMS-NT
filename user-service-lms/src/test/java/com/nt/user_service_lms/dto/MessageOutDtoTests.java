package com.nt.user_service_lms.dto;

import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class MessageOutDtoTests {

    private MessageOutDTO messageOutDto;

    @BeforeEach
    void setUp() {
        messageOutDto = new MessageOutDTO();
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
        MessageOutDTO dto1 = new MessageOutDTO("Successful");
        MessageOutDTO dto2 = new MessageOutDTO("Successful");
        MessageOutDTO dto3 = new MessageOutDTO("Something went wrong");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        MessageOutDTO dto = new MessageOutDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        MessageOutDTO dto = new MessageOutDTO("Successful");
        assertEquals("Successful", dto.getMessage());
    }
}
