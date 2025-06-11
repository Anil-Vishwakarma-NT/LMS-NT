package com.example.course_service_lms.converterTest;

import com.example.course_service_lms.converters.CourseContentConverters;
import com.example.course_service_lms.inDTO.CourseContentInDTO;
import com.example.course_service_lms.entity.CourseContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseContentConvertersTest {

    @Test
    void testCourseContentDtoToCourseContent() {
        // Given
        CourseContentInDTO dto = new CourseContentInDTO();
        dto.setCourseId(1L);
        dto.setTitle("Java Basics");
        dto.setDescription("Introduction to Java Programming");
        dto.setVideoLink("https://example.com/java-video");
        dto.setResourceLink("https://example.com/java-resource");

        // When
        CourseContent courseContent = CourseContentConverters.courseContentDtoToCourseContent(dto);

        // Then
        assertNotNull(courseContent);
        assertEquals(dto.getCourseId(), courseContent.getCourseId());
        assertEquals(dto.getTitle(), courseContent.getTitle());
        assertEquals(dto.getDescription(), courseContent.getDescription());
        assertEquals(dto.getVideoLink(), courseContent.getVideoLink());
        assertEquals(dto.getResourceLink(), courseContent.getResourceLink());
    }

    @Test
    void testEmptyCourseContentDto() {
        // Given
        CourseContentInDTO dto = new CourseContentInDTO();

        // When
        CourseContent courseContent = CourseContentConverters.courseContentDtoToCourseContent(dto);

        // Then
        assertNotNull(courseContent);
        assertEquals(0, courseContent.getCourseId());
        assertNull(courseContent.getTitle());
        assertNull(courseContent.getDescription());
        assertNull(courseContent.getVideoLink());
        assertNull(courseContent.getResourceLink());
    }

    @Test
    void testNullCourseContentDto() {
        // Given
        CourseContentInDTO dto = null;

        // When / Then
        assertThrows(NullPointerException.class, () -> CourseContentConverters.courseContentDtoToCourseContent(dto));
    }
}
