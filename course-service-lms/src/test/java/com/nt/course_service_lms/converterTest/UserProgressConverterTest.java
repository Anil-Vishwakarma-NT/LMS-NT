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
        UserProgress entity = UserProgress.builder()
                .userId(1L)
                .courseId(10L)
                .contentId(100L)
                .contentType("VIDEO")
                .lastPosition(120)
                .contentCompletionPercentage(75.5)
                .lastUpdated(now)
                .build();

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
    void testToDTO_NullEntity() {
        UserProgressOutDTO dto = converter.toDTO(null);
        assertNull(dto);
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
        assertEquals(0, dto.getLastPosition()); // default int
        assertEquals(0.0, dto.getContentCompletionPercentage()); // default double
        assertNull(dto.getLastUpdated());
    }

    @Test
    void testToEntity_ValidDTO() {
        UserProgressOutDTO dto = UserProgressOutDTO.builder()
                .userId(2L)
                .courseId(20L)
                .contentId(200L)
                .contentType("QUIZ")
                .lastPosition(45)
                .contentCompletionPercentage(99.9)
                .lastUpdated(LocalDateTime.of(2023, 10, 10, 10, 10))
                .build();

        UserProgress entity = converter.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getUserId());
        assertEquals(20L, entity.getCourseId());
        assertEquals(200L, entity.getContentId());
        assertEquals("QUIZ", entity.getContentType());
        assertEquals(45, entity.getLastPosition());
        assertEquals(99.9, entity.getContentCompletionPercentage());
        assertNotNull(entity.getLastUpdated()); // Should be set to now
    }

    @Test
    void testToEntity_NullDTO() {
        UserProgress entity = converter.toEntity(null);
        assertNull(entity);
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
        assertNotNull(entity.getLastUpdated()); // Should still be set
    }
}

