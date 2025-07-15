package com.nt.course_service_lms.serviceImplTest;


import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.service.serviceImpl.CourseContentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseContentImplTest {

    @Mock
    private CourseContentRepository contentRepo;

    @Mock
    private CourseRepository courseRepo;

    @InjectMocks
    private CourseContentImpl service;

    private CourseContentInDTO inDTO;
    private UpdateCourseContentInDTO updateDTO;
    private CourseContent entity;

    @BeforeEach
    void setUp() {
        inDTO = new CourseContentInDTO(1L, "Title", "Desc", "https://example.com", true);
        updateDTO = new UpdateCourseContentInDTO(1L, "Updated Title", "Updated Desc", "https://updated.com", true);
        entity = new CourseContent(1L, 1L, "Title", "Desc", "https://example.com", true, LocalDateTime.now(), LocalDateTime.now());
    }

    // CREATE
    @Test
    void createCourseContent_success() {
        when(courseRepo.existsById(1L)).thenReturn(true);
        when(contentRepo.findByTitleIgnoreCaseAndCourseId("Title", 1L)).thenReturn(Optional.empty());
        when(contentRepo.save(any())).thenReturn(entity);

        CourseContentOutDTO result = service.createCourseContent(inDTO);

        assertEquals("Title", result.getTitle());
        verify(contentRepo).save(any());
    }

    @Test
    void createCourseContent_courseNotFound() {
        when(courseRepo.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.createCourseContent(inDTO));
    }

    @Test
    void createCourseContent_duplicateTitle() {
        when(courseRepo.existsById(1L)).thenReturn(true);
        when(contentRepo.findByTitleIgnoreCaseAndCourseId("Title", 1L)).thenReturn(Optional.of(entity));
        assertThrows(ResourceAlreadyExistsException.class, () -> service.createCourseContent(inDTO));
    }

    // GET ALL
    @Test
    void getAllCourseContents_success() {
        when(contentRepo.findAll()).thenReturn(Arrays.asList(entity));
        List<CourseContentOutDTO> result = service.getAllCourseContents();
        assertEquals(1, result.size());
    }

    @Test
    void getAllCourseContents_empty() {
        when(contentRepo.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> service.getAllCourseContents());
    }

    // GET BY ID
    @Test
    void getCourseContentById_success() {
        when(contentRepo.findById(1L)).thenReturn(Optional.of(entity));
        CourseContentOutDTO result = service.getCourseContentById(1L);
        assertEquals("Title", result.getTitle());
    }

    @Test
    void getCourseContentById_notFound() {
        when(contentRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCourseContentById(1L));
    }

    // DELETE
    @Test
    void deleteCourseContent_success() {
        when(contentRepo.findById(1L)).thenReturn(Optional.of(entity));
        String result = service.deleteCourseContent(1L);
        assertEquals("Course content deleted successfully.", result);
        verify(contentRepo).delete(entity);
    }

    @Test
    void deleteCourseContent_notFound() {
        when(contentRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteCourseContent(1L));
    }

    // UPDATE
    @Test
    void updateCourseContent_success() {
        when(contentRepo.findById(1L)).thenReturn(Optional.of(entity));
        when(courseRepo.existsById(1L)).thenReturn(true);
        when(contentRepo.findByTitleIgnoreCaseAndCourseId("Updated Title", 1L)).thenReturn(Optional.empty());
        when(contentRepo.save(any())).thenReturn(entity);

        CourseContentOutDTO result = service.updateCourseContent(1L, updateDTO);
        assertNotNull(result);
    }

    @Test
    void updateCourseContent_notFound() {
        when(contentRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateCourseContent(1L, updateDTO));
    }

    @Test
    void updateCourseContent_courseNotFound() {
        when(contentRepo.findById(1L)).thenReturn(Optional.of(entity));
        when(courseRepo.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.updateCourseContent(1L, updateDTO));
    }

    @Test
    void updateCourseContent_duplicate() {
        CourseContent other = new CourseContent(2L, 1L, "Updated Title", "Other", "https://updated.com", true, LocalDateTime.now(), LocalDateTime.now());
        when(contentRepo.findById(1L)).thenReturn(Optional.of(entity));
        when(courseRepo.existsById(1L)).thenReturn(true);
        when(contentRepo.findByTitleIgnoreCaseAndCourseId("Updated Title", 1L)).thenReturn(Optional.of(other));
        assertThrows(ResourceAlreadyExistsException.class, () -> service.updateCourseContent(1L, updateDTO));
    }

    // GET BY COURSE ID
    @Test
    void getAllCourseContentByCourseId_success() {
        when(courseRepo.existsById(1L)).thenReturn(true);
        when(contentRepo.findByCourseId(1L)).thenReturn(Arrays.asList(entity));

        List<CourseContentOutDTO> result = service.getAllCourseContentByCourseId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getAllCourseContentByCourseId_courseNotFound() {
        when(courseRepo.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.getAllCourseContentByCourseId(1L));
    }

    @Test
    void getAllCourseContentByCourseId_noContent() {
        when(courseRepo.existsById(1L)).thenReturn(true);
        when(contentRepo.findByCourseId(1L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> service.getAllCourseContentByCourseId(1L));
    }
}
