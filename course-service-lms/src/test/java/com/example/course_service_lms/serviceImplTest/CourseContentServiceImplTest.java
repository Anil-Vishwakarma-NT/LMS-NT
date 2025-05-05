package com.example.course_service_lms.serviceImplTest;

import com.example.course_service_lms.converters.CourseContentConverters;
import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.CourseContentRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.serviceImpl.CourseContentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseContentImplTest {

    @Mock
    private CourseContentRepository courseContentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseContentImpl courseContentService;

    private CourseContentDTO validDTO;
    private CourseContent courseContent;

    @BeforeEach
    void setUp() {
        validDTO = new CourseContentDTO();
        validDTO.setCourseId(1L);
        validDTO.setTitle("Java Basics");
        validDTO.setDescription("Introduction to Java");
        validDTO.setVideoLink("https://example.com/video");
        validDTO.setResourceLink("https://example.com/resource");

        courseContent = new CourseContent(1L, 1L, "Java Basics", "Introduction to Java", "https://example.com/video", "https://example.com/resource");
    }

    // Test for createCourseContent()
    @Test
    void testCreateCourseContentSuccess() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(courseRepository.existsById(anyLong())).thenReturn(true);
        when(courseContentRepository.save(any())).thenReturn(courseContent);

        CourseContent result = courseContentService.createCourseContent(validDTO);

        assertNotNull(result);
        assertEquals(courseContent.getTitle(), result.getTitle());
        verify(courseContentRepository, times(1)).save(any());
    }

    @Test
    void testCreateCourseContentAlreadyExists() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(anyString(), anyLong())).thenReturn(Optional.of(courseContent));

        assertThrows(ResourceAlreadyExistsException.class, () -> courseContentService.createCourseContent(validDTO));
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testCreateCourseContentCourseNotFound() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(courseRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.createCourseContent(validDTO));
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testCreateCourseContentRuntimeException() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(anyString(), anyLong()))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseContentService.createCourseContent(validDTO));
        assertEquals("Something went wrong", exception.getMessage());
        verify(courseContentRepository, never()).save(any());
    }

    // Test for getAllCourseContents()
    @Test
    void testGetAllCourseContentsSuccess() {
        List<CourseContent> contents = List.of(courseContent, courseContent);
        when(courseContentRepository.findAll()).thenReturn(contents);

        List<CourseContent> result = courseContentService.getAllCourseContents();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseContentRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCourseContentsEmpty() {
        when(courseContentRepository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContents());
        verify(courseContentRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCourseContentsRuntimeException() {
        when(courseContentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseContentService.getAllCourseContents());
        assertEquals("Something went wrong", exception.getMessage());
        verify(courseContentRepository, times(1)).findAll();
    }

    // Test for getCourseContentById()
    @Test
    void testGetCourseContentByIdSuccess() {
        when(courseContentRepository.findById(anyLong())).thenReturn(Optional.of(courseContent));

        Optional<CourseContent> result = courseContentService.getCourseContentById(1L);

        assertTrue(result.isPresent());
        assertEquals(courseContent.getTitle(), result.get().getTitle());
        verify(courseContentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCourseContentByIdNotFound() {
        when(courseContentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getCourseContentById(1L));
        verify(courseContentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCourseContentByIdRuntimeException() {
        when(courseContentRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseContentService.getCourseContentById(1L));
        assertEquals("Something went wrong", exception.getMessage());
        verify(courseContentRepository, times(1)).findById(anyLong());
    }

    // Test for deleteCourseContent()
    @Test
    void testDeleteCourseContentSuccess() {
        when(courseContentRepository.findById(anyLong())).thenReturn(Optional.of(courseContent));

        String result = courseContentService.deleteCourseContent(1L);

        assertEquals("Course Content Deleted Successfully", result);
        verify(courseContentRepository, times(1)).delete(any());
    }

    @Test
    void testDeleteCourseContentNotFound() {
        when(courseContentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.deleteCourseContent(1L));
        verify(courseContentRepository, never()).delete(any());
    }

    @Test
    void testDeleteCourseContentRuntimeException() {
        when(courseContentRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseContentService.deleteCourseContent(1L));
        assertEquals("Something went wrong", exception.getMessage());
        verify(courseContentRepository, times(1)).findById(anyLong());
    }


@Test
void testUpdateCourseContentSuccess() {
    when(courseContentRepository.findById(anyLong())).thenReturn(Optional.of(courseContent));
    when(courseRepository.existsById(anyLong())).thenReturn(true);

    String result = courseContentService.updateCourseContent(1L, validDTO);

    assertEquals("Course content updated successfully", result);
    verify(courseContentRepository, times(1)).save(any());
}

    @Test
    void testUpdateCourseContentNotFound() {
        when(courseContentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> courseContentService.updateCourseContent(1L, validDTO));
        assertEquals("Course Content Not Found", exception.getMessage());
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testUpdateCourseContentCourseNotFound() {
        when(courseContentRepository.findById(anyLong())).thenReturn(Optional.of(courseContent));
        when(courseRepository.existsById(anyLong())).thenReturn(false); // Simulate course not existing

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> courseContentService.updateCourseContent(1L, validDTO));
        assertEquals("Course does not exists", exception.getMessage());

        verify(courseContentRepository, times(1)).findById(anyLong());
        verify(courseRepository, times(1)).existsById(anyLong());
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testUpdateCourseContentDuplicate() {
        // Mock the existing content
        when(courseContentRepository.findById(anyLong()))
                .thenReturn(Optional.of(new CourseContent(1L, 1L, "Old Title", "Description", "Video", "Resource")));

        when(courseRepository.existsById(anyLong())).thenReturn(true);

        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(anyString(), anyLong()))
                .thenReturn(Optional.of(new CourseContent(2L, 2L, "New Title", "Duplicate Description", "Duplicate Video", "Duplicate Resource")));

        validDTO.setTitle("New Title");
        validDTO.setCourseId(2L);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> courseContentService.updateCourseContent(1L, validDTO));
        assertEquals("Course content with the same title already exists for this course.", exception.getMessage());

        verify(courseContentRepository, times(1)).findById(anyLong());
        verify(courseRepository, times(1)).existsById(anyLong());
        verify(courseContentRepository, times(1)).findByTitleIgnoreCaseAndCourseId(anyString(), anyLong());
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testUpdateCourseContentRuntimeException() {
        when(courseContentRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseContentService.updateCourseContent(1L, validDTO));
        assertEquals("Something went wrong", exception.getMessage());
        verify(courseContentRepository, times(1)).findById(anyLong());
    }


    // Test for getAllCourseContentByCourseId()
    @Test
    void testGetAllCourseContentByCourseIdSuccess() {
        List<CourseContent> contents = List.of(courseContent, courseContent);
        when(courseRepository.existsById(anyLong())).thenReturn(true);
        when(courseContentRepository.findByCourseId(anyLong())).thenReturn(contents);

        List<CourseContent> result = courseContentService.getAllCourseContentByCourseId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).existsById(anyLong());
        verify(courseContentRepository, times(1)).findByCourseId(anyLong());
    }

    @Test
    void testGetAllCourseContentByCourseIdNotFound() {
        when(courseRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContentByCourseId(1L));
        verify(courseRepository, times(1)).existsById(anyLong());
        verify(courseContentRepository, never()).findByCourseId(anyLong());
    }

    @Test
    void testGetAllCourseContentByCourseIdEmpty() {
        when(courseRepository.existsById(anyLong())).thenReturn(true);
        when(courseContentRepository.findByCourseId(anyLong())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContentByCourseId(1L));
        verify(courseContentRepository, times(1)).findByCourseId(anyLong());
    }

    @Test
    void testGetAllCourseContentByCourseIdRuntimeException() {
        when(courseRepository.existsById(anyLong())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseContentService.getAllCourseContentByCourseId(1L));
        assertEquals("Something went wrong", exception.getMessage());
        verify(courseRepository, times(1)).existsById(anyLong());
        verify(courseContentRepository, never()).findByCourseId(anyLong());
    }
}



