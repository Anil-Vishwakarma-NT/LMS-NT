package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.converters.CourseContentConverters;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.service.serviceImpl.CourseContentImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseContentImplTest {

    @Mock
    private CourseContentRepository courseContentRepository;

    @Mock
    private CourseRepository courseRepository;
    private AutoCloseable closeable;
    @InjectMocks
    private CourseContentImpl courseContentService;

    private CourseContent courseContent;
    private CourseContentInDTO courseContentInDTO;
    private UpdateCourseContentInDTO updateCourseContentInDTO;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        courseContent = CourseContent.builder()
                .courseContentId(1L)
                .courseId(101L)
                .title("Intro")
                .description("Introduction")
                .resourceLink("http://link")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        courseContentInDTO = new CourseContentInDTO(101L, "Intro", "Introduction", "http://link", true);

        updateCourseContentInDTO = new UpdateCourseContentInDTO(101L, "Intro Updated", "Updated Description", "http://link-updated", false);
    }
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testCreateCourseContent_success() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro", 101L)).thenReturn(Optional.empty());
        when(courseRepository.existsById(101L)).thenReturn(true);
        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(courseContent);

        CourseContentOutDTO result = courseContentService.createCourseContent(courseContentInDTO);

        assertNotNull(result);
        verify(courseContentRepository).save(any(CourseContent.class));
    }

    @Test
    void testCreateCourseContent_alreadyExists() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro", 101L)).thenReturn(Optional.of(courseContent));

        assertThrows(ResourceAlreadyExistsException.class, () -> courseContentService.createCourseContent(courseContentInDTO));
    }

    @Test
    void testCreateCourseContent_courseNotFound() {
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro", 101L)).thenReturn(Optional.empty());
        when(courseRepository.existsById(101L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.createCourseContent(courseContentInDTO));
    }

    @Test
    void testGetAllCourseContents_success() {
        when(courseContentRepository.findAll()).thenReturn(Arrays.asList(courseContent));

        List<CourseContentOutDTO> result = courseContentService.getAllCourseContents();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllCourseContents_empty() {
        when(courseContentRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContents());
    }

    @Test
    void testGetCourseContentById_success() {
        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));

        CourseContentOutDTO result = courseContentService.getCourseContentById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetCourseContentById_notFound() {
        when(courseContentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getCourseContentById(1L));
    }

    @Test
    void testDeleteCourseContent_success() {
        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));

        String result = courseContentService.deleteCourseContent(1L);

        assertEquals("Course content deleted successfully", result);
        verify(courseContentRepository).delete(courseContent);
    }

    @Test
    void testDeleteCourseContent_notFound() {
        when(courseContentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.deleteCourseContent(1L));
    }

    @Test
    void testUpdateCourseContent_success() {
        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));
        when(courseRepository.existsById(101L)).thenReturn(true);
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro Updated", 101L)).thenReturn(Optional.empty());
        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(courseContent);

        CourseContentOutDTO result = courseContentService.updateCourseContent(1L, updateCourseContentInDTO);

        assertNotNull(result);
    }

    @Test
    void testUpdateCourseContent_notFound() {
        when(courseContentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.updateCourseContent(1L, updateCourseContentInDTO));
    }

    @Test
    void testUpdateCourseContent_duplicate() {
        CourseContent duplicate = CourseContent.builder().courseContentId(2L).courseId(101L).title("Intro Updated").build();

        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));
        when(courseRepository.existsById(101L)).thenReturn(true);
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro Updated", 101L)).thenReturn(Optional.of(duplicate));

        assertThrows(ResourceAlreadyExistsException.class, () -> courseContentService.updateCourseContent(1L, updateCourseContentInDTO));
    }

    @Test
    void testGetAllCourseContentByCourseId_success() {
        when(courseRepository.existsById(101L)).thenReturn(true);
        when(courseContentRepository.findByCourseId(101L)).thenReturn(Arrays.asList(courseContent));

        List<CourseContentOutDTO> result = courseContentService.getAllCourseContentByCourseId(101L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllCourseContentByCourseId_noContents() {
        when(courseRepository.existsById(101L)).thenReturn(true);
        when(courseContentRepository.findByCourseId(101L)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContentByCourseId(101L));
    }

    @Test
    void testGetAllCourseContentByCourseId_courseNotFound() {
        when(courseRepository.existsById(101L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContentByCourseId(101L));
    }
} 

