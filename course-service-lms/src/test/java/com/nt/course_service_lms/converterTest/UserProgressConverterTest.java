package com.nt.course_service_lms.converterTest;

import com.nt.course_service_lms.converters.UserProgressConverter;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.entity.UserProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserProgressConverterTest {

    private UserProgressConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UserProgressConverter();
    }

    @Test
    void testToDTO_ValidEntity() {
        LocalDateTime now = LocalDateTime.now();
        UserProgress entity = new UserProgress();
        entity.setUserId(1L);
        entity.setCourseId(10L);
        entity.setContentId(100L);
        entity.setContentType("VIDEO");
        entity.setLastPosition(120);
        entity.setContentCompletionPercentage(75.5);
        entity.setLastUpdated(now);

        UserProgressOutDTO dto = converter.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getUserId());
        assertEquals(10L, dto.getCourseId());
        assertEquals(100L, dto.getContentId());
        assertEquals("VIDEO", dto.getContentType());
        assertEquals(120, dto.getLastPosition());
        assertEquals(75.5, dto.getContentCompletionPercentage());
        assertEquals(now, dto.getLastUpdated());
    }


    @Test
    void testToDTO_EntityWithNullFields() {
        UserProgress entity = new UserProgress();
        UserProgressOutDTO dto = converter.toDTO(entity);

        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getCourseId());
        assertNull(dto.getContentId());
        assertNull(dto.getContentType());
        assertEquals(0, dto.getLastPosition());
        assertEquals(0.0, dto.getContentCompletionPercentage());
    }

    @Test
    void testToEntity_ValidDTO() {
        UserProgressOutDTO dto = new UserProgressOutDTO();
        dto.setUserId(2L);
        dto.setCourseId(20L);
        dto.setContentId(200L);
        dto.setContentType("QUIZ");
        dto.setLastPosition(45);
        dto.setContentCompletionPercentage(99.9);
        dto.setLastUpdated(LocalDateTime.of(2023, 10, 10, 10, 10));

        UserProgress entity = converter.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getUserId());
        assertEquals(20L, entity.getCourseId());
        assertEquals(200L, entity.getContentId());
        assertEquals("QUIZ", entity.getContentType());
        assertEquals(45, entity.getLastPosition());
        assertEquals(99.9, entity.getContentCompletionPercentage());
        assertNotNull(entity.getLastUpdated());
    }

    @Test
    void testToEntity_DTOWithNullFields() {
        UserProgressOutDTO dto = new UserProgressOutDTO();
        UserProgress entity = converter.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getUserId());
        assertNull(entity.getCourseId());
        assertNull(entity.getContentId());
        assertNull(entity.getContentType());
        assertEquals(0, entity.getLastPosition());
        assertEquals(0.0, entity.getContentCompletionPercentage());
        assertNotNull(entity.getLastUpdated());
    }
}

