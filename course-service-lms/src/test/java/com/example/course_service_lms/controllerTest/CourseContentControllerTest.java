package com.example.course_service_lms.controllerTest;

import com.example.course_service_lms.controller.CourseContentController;
import com.example.course_service_lms.inDTO.CourseContentInDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.service.CourseContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseContentControllerTest {

    @Mock
    private CourseContentService courseContentService;

    @InjectMocks
    private CourseContentController courseContentController;

    // Test for createCourseContent()
    @Test
    void testCreateCourseContent() {
        // Given
        CourseContentInDTO dto = new CourseContentInDTO();
        dto.setCourseId(1L);
        dto.setTitle("Java Basics");
        dto.setDescription("Introduction to Java Programming");
        dto.setVideoLink("https://example.com/java-video");
        dto.setResourceLink("https://example.com/java-resource");

        CourseContent courseContent = new CourseContent(1L, 1L, "Java Basics", "Introduction to Java Programming", "https://example.com/java-video", "https://example.com/java-resource");

        when(courseContentService.createCourseContent(dto)).thenReturn(courseContent);

        // When
        ResponseEntity<CourseContent> response = courseContentController.createCourseContent(dto);

        // Then
        assertEquals(ResponseEntity.ok(courseContent), response);
        verify(courseContentService, times(1)).createCourseContent(dto);
    }

    // Test for getAllCourseContents()
    @Test
    void testGetAllCourseContents() {
        // Given
        List<CourseContent> mockContents = Arrays.asList(
                new CourseContent(1L, 1L, "Title1", "Description1", "VideoLink1", "ResourceLink1"),
                new CourseContent(2L, 1L, "Title2", "Description2", "VideoLink2", "ResourceLink2")
        );

        when(courseContentService.getAllCourseContents()).thenReturn(mockContents);

        // When
        ResponseEntity<List<CourseContent>> response = courseContentController.getAllCourseContents();

        // Then
        assertEquals(ResponseEntity.ok(mockContents), response);
        verify(courseContentService, times(1)).getAllCourseContents();
    }

    // Test for getCourseContentById()
    @Test
    void testGetCourseContentById() {
        // Given
        CourseContent mockContent = new CourseContent(1L, 1L, "Title1", "Description1", "VideoLink1", "ResourceLink1");
        when(courseContentService.getCourseContentById(anyLong())).thenReturn(Optional.of(mockContent));

        // When
        ResponseEntity<Optional<CourseContent>> response = courseContentController.getCourseContentById(1L);

        // Then
        assertEquals(ResponseEntity.ok(Optional.of(mockContent)), response);
        verify(courseContentService, times(1)).getCourseContentById(1L);
    }

    @Test
    void testGetCourseContentByIdNotFound() {
        // Given
        when(courseContentService.getCourseContentById(anyLong())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Optional<CourseContent>> response = courseContentController.getCourseContentById(1L);

        // Then
        assertEquals(ResponseEntity.ok(Optional.empty()), response);
        verify(courseContentService, times(1)).getCourseContentById(1L);
    }

    // Test for getCourseContentByCourseId()
    @Test
    void testGetCourseContentByCourseId() {
        // Given
        List<CourseContent> mockContents = Arrays.asList(
                new CourseContent(1L, 1L, "Title1", "Description1", "VideoLink1", "ResourceLink1"),
                new CourseContent(2L, 1L, "Title2", "Description2", "VideoLink2", "ResourceLink2")
        );

        when(courseContentService.getAllCourseContentByCourseId(anyLong())).thenReturn(mockContents);

        // When
        ResponseEntity<List<CourseContent>> response = courseContentController.getCourseContentByCourseId(1L);

        // Then
        assertEquals(ResponseEntity.ok(mockContents), response);
        verify(courseContentService, times(1)).getAllCourseContentByCourseId(1L);
    }

    // Test for deleteCourseContent()
    @Test
    void testDeleteCourseContent() {
        // Given
        String mockMessage = "Course content deleted successfully.";
        when(courseContentService.deleteCourseContent(anyLong())).thenReturn(mockMessage);

        // When
        ResponseEntity<String> response = courseContentController.deleteCourseContent(1L);

        // Then
        assertEquals(ResponseEntity.ok(mockMessage), response);
        verify(courseContentService, times(1)).deleteCourseContent(1L);
    }

    // Test for updateCourseContent()
    @Test
    void testUpdateCourseContent() {
        // Given
        CourseContentInDTO dto = new CourseContentInDTO();
        dto.setCourseId(1L);
        dto.setTitle("Updated Title");
        dto.setDescription("Updated Description");
        dto.setVideoLink("https://example.com/updated-video");
        dto.setResourceLink("https://example.com/updated-resource");

        String mockMessage = "Course content updated successfully.";
        when(courseContentService.updateCourseContent(anyLong(), eq(dto))).thenReturn(mockMessage);

        // When
        ResponseEntity<String> response = courseContentController.updateCourseContent(1L, dto);

        // Then
        assertEquals(ResponseEntity.ok(mockMessage), response);
        verify(courseContentService, times(1)).updateCourseContent(1L, dto);
    }
}
