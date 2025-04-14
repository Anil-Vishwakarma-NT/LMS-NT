package com.example.course_service_lms.serviceImplTest;

import com.example.course_service_lms.Enum.CourseLevel;
import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseBundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.repository.CourseBundleRepository;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.serviceImpl.CourseBundleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseBundleServiceImplTest {

    @Mock
    private CourseBundleRepository courseBundleRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private BundleRepository bundleRepository;

    @InjectMocks
    private CourseBundleServiceImpl service;

    // Test for getAllCourseBundles()
    @Test
    void testGetAllCourseBundlesSuccess() {
        List<CourseBundle> mockBundles = List.of(new CourseBundle(1L, 2L, 3L));

        when(courseBundleRepository.findAll()).thenReturn(mockBundles);
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.of(new Bundle(2L, "MockBundle")));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(new Course(3L, 1001L, "MockTitle", "MockDescription", "MockImage", CourseLevel.INTERMEDIATE)));

        List<CourseBundleDTO> result = service.getAllCourseBundles();

        assertEquals(1, result.size());
        assertEquals("MockBundle", result.get(0).getBundleName());
        assertEquals("MockTitle", result.get(0).getCourseName());
    }

    @Test
    void testGetAllCourseBundlesNoRecords() {
        when(courseBundleRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> service.getAllCourseBundles());
    }

    @Test
    void testGetAllCourseBundlesBundleNotFound() {
        List<CourseBundle> mockBundles = List.of(new CourseBundle(1L, 2L, 3L));
        when(courseBundleRepository.findAll()).thenReturn(mockBundles);
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.empty()); // Simulate missing Bundle

        assertThrows(ResourceNotFoundException.class, () -> service.getAllCourseBundles());
    }

    @Test
    void testGetAllCourseBundlesCourseNotFound() {
        List<CourseBundle> mockBundles = List.of(new CourseBundle(1L, 2L, 3L));
        when(courseBundleRepository.findAll()).thenReturn(mockBundles);
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.of(new Bundle(2L, "MockBundle")));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty()); // Simulate missing Course

        assertThrows(ResourceNotFoundException.class, () -> service.getAllCourseBundles());
    }

    @Test
    void testGetAllCourseBundlesUnexpectedException() {
        when(courseBundleRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.getAllCourseBundles());
        assertEquals("Failed to fetch course-bundle records", thrown.getMessage());
    }

    // Test for getCourseBundleById()
    @Test
    void testGetCourseBundleByIdSuccess() {
        CourseBundle mockBundle = new CourseBundle(1L, 2L, 3L);

        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.of(mockBundle));
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.of(new Bundle(2L, "MockBundle")));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(new Course(3L, 1001L, "MockTitle", "MockDescription", "MockImage", CourseLevel.INTERMEDIATE)));

        CourseBundleDTO result = service.getCourseBundleById(1L);

        assertNotNull(result);
        assertEquals("MockBundle", result.getBundleName());
        assertEquals("MockTitle", result.getCourseName());
    }

    @Test
    void testGetCourseBundleByIdNotFound() {
        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCourseBundleById(1L));
    }

    @Test
    void testGetCourseBundleByIdBundleNotFound() {
        CourseBundle mockBundle = new CourseBundle(1L, 2L, 3L);
        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.of(mockBundle));
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.empty()); // Simulate missing Bundle

        assertThrows(ResourceNotFoundException.class, () -> service.getCourseBundleById(1L));
    }

    @Test
    void testGetCourseBundleByIdCourseNotFound() {
        CourseBundle mockBundle = new CourseBundle(1L, 2L, 3L);
        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.of(mockBundle));
        when(bundleRepository.findById(anyLong())).thenReturn(Optional.of(new Bundle(2L, "MockBundle")));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty()); // Simulate missing Course

        assertThrows(ResourceNotFoundException.class, () -> service.getCourseBundleById(1L));
    }

    @Test
    void testGetCourseBundleByIdUnexpectedException() {
        when(courseBundleRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.getCourseBundleById(1L));
        assertEquals("Failed to fetch course-bundle record with ID: 1", thrown.getMessage());
    }

    // Test for createCourseBundle()
    @Test
    void testCreateCourseBundleSuccess() {
        CourseBundleDTO dto = new CourseBundleDTO(1L, 2L, null, 3L, null);
        CourseBundle mockEntity = new CourseBundle(1L, 2L, 3L);

        when(bundleRepository.existsById(anyLong())).thenReturn(true);
        when(courseRepository.existsById(anyLong())).thenReturn(true);
        when(courseBundleRepository.existsByBundleIdAndCourseId(anyLong(), anyLong())).thenReturn(false);
        when(courseBundleRepository.save(any())).thenReturn(mockEntity);

        CourseBundleDTO result = service.createCourseBundle(dto);

        assertNotNull(result);
        assertEquals(2L, result.getBundleId());
        assertEquals(3L, result.getCourseId());
    }

    @Test
    void testCreateCourseBundleAlreadyExists() {
        when(courseBundleRepository.existsByBundleIdAndCourseId(anyLong(), anyLong())).thenReturn(true);
        CourseBundleDTO dto = new CourseBundleDTO(1L, 2L, null, 3L, null);

        assertThrows(ResourceAlreadyExistsException.class, () -> service.createCourseBundle(dto));
    }

    @Test
    void testCreateCourseBundleInvalidBundleId() {
        CourseBundleDTO dto = new CourseBundleDTO(1L, 2L, null, 3L, null);
        when(bundleRepository.existsById(anyLong())).thenReturn(false); // Simulate invalid Bundle ID

        assertThrows(ResourceNotValidException.class, () -> service.createCourseBundle(dto));
    }

    @Test
    void testCreateCourseBundleInvalidCourseId() {
        CourseBundleDTO dto = new CourseBundleDTO(1L, 2L, null, 3L, null);
        when(bundleRepository.existsById(anyLong())).thenReturn(true);
        when(courseRepository.existsById(anyLong())).thenReturn(false); // Simulate invalid Course ID

        assertThrows(ResourceNotValidException.class, () -> service.createCourseBundle(dto));
    }

    @Test
    void testCreateCourseBundleUnexpectedException() {
        when(bundleRepository.existsById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.createCourseBundle(new CourseBundleDTO(1L, 2L, null, 3L, null)));
        assertEquals("Something went wrong while creating course-bundle mapping", thrown.getMessage());
    }


//     Test for deleteCourseBundle()
    @Test
    void testDeleteCourseBundleSuccess() {
        CourseBundle mockBundle = new CourseBundle(1L, 2L, 3L);
        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.of(mockBundle));

        service.deleteCourseBundle(1L);

        verify(courseBundleRepository, times(1)).delete(mockBundle);
    }

    @Test
    void testDeleteCourseBundleNotFound() {
        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteCourseBundle(1L));
    }

    @Test
    void testDeleteCourseBundleUnexpectedException() {
        when(courseBundleRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.deleteCourseBundle(1L));
        assertEquals("Something went wrong while deleting course-bundle record with ID: 1", thrown.getMessage());
    }

    // Test for updateCourseBundle()
    @Test
    void testUpdateCourseBundleSuccess() {
        CourseBundleDTO dto = new CourseBundleDTO(1L, 2L, null, 3L, null);
        CourseBundle mockEntity = new CourseBundle(1L, 2L, 3L);

        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.of(mockEntity));
        when(courseBundleRepository.save(any())).thenReturn(mockEntity);

        CourseBundleDTO result = service.updateCourseBundle(1L, dto);

        assertNotNull(result);
        assertEquals(2L, result.getBundleId());
        assertEquals(3L, result.getCourseId());
    }

    @Test
    void testUpdateCourseBundleNotFound() {
        when(courseBundleRepository.findById(anyLong())).thenReturn(Optional.empty());

        CourseBundleDTO dto = new CourseBundleDTO(1L, 2L, null, 3L, null);
        assertThrows(ResourceNotFoundException.class, () -> service.updateCourseBundle(1L, dto));
    }

    @Test
    void testUpdateCourseBundleUnexpectedException() {
        when(courseBundleRepository.findById(anyLong())).thenThrow(new RuntimeException("Update error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.updateCourseBundle(1L, new CourseBundleDTO(1L, 2L, null, 3L, null)));
        assertEquals("Something went wrong while updating course-bundle record with ID: 1", thrown.getMessage());
    }
}