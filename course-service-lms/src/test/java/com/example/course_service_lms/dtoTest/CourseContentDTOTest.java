package com.example.course_service_lms.dtoTest;

import com.example.course_service_lms.dto.CourseContentDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseContentDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Given
        CourseContentDTO dto = new CourseContentDTO(1L, "Java Basics", "Introduction to Java Programming",
                "https://example.com/java-video", "https://example.com/java-resource");

        // Then
        assertEquals(1L, dto.getCourseId());
        assertEquals("Java Basics", dto.getTitle());
        assertEquals("Introduction to Java Programming", dto.getDescription());
        assertEquals("https://example.com/java-video", dto.getVideoLink());
        assertEquals("https://example.com/java-resource", dto.getResourceLink());
    }

    @Test
    void testSetters() {
        // Given
        CourseContentDTO dto = new CourseContentDTO();

        // When
        dto.setCourseId(2L);
        dto.setTitle("Advanced Java");
        dto.setDescription("Deep dive into Java programming concepts");
        dto.setVideoLink("https://example.com/advanced-java-video");
        dto.setResourceLink("https://example.com/advanced-java-resource");

        // Then
        assertEquals(2L, dto.getCourseId());
        assertEquals("Advanced Java", dto.getTitle());
        assertEquals("Deep dive into Java programming concepts", dto.getDescription());
        assertEquals("https://example.com/advanced-java-video", dto.getVideoLink());
        assertEquals("https://example.com/advanced-java-resource", dto.getResourceLink());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        CourseContentDTO dto1 = new CourseContentDTO(1L, "Java Basics", "Introduction to Java Programming",
                "https://example.com/java-video", "https://example.com/java-resource");
        CourseContentDTO dto2 = new CourseContentDTO(1L, "Java Basics", "Introduction to Java Programming",
                "https://example.com/java-video", "https://example.com/java-resource");
        CourseContentDTO dto3 = new CourseContentDTO(2L, "Advanced Java", "Deep dive into Java concepts",
                "https://example.com/advanced-java-video", "https://example.com/advanced-java-resource");

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        // Given
        CourseContentDTO dto = new CourseContentDTO();

        // Then
        assertNotNull(dto);
    }
}