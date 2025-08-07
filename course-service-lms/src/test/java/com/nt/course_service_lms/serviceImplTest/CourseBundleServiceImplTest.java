package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.BundleInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.BundleSummaryOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.BundleRepository;
import com.nt.course_service_lms.repository.CourseBundleRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.service.serviceImpl.CourseBundleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CourseBundleServiceImplTest {

    @Mock
    private CourseBundleRepository courseBundleRepository;
    @Mock
    private BundleRepository bundleRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseBundleServiceImpl courseBundleService;

    private AutoCloseable closeable;
    private CourseBundle courseBundle;
    private Bundle bundle;
    private Course course;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        courseBundle = CourseBundle.builder()
                .courseBundleId(1L)
                .bundleId(2L)
                .courseId(3L)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bundle = Bundle.builder()
                .bundleId(2L)
                .bundleName("Dev Bundle")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        course = Course.builder()
                .courseId(3L)
                .title("Java Mastery")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createCourseBundle_success() {
        CourseBundleInDTO inDTO = new CourseBundleInDTO(0L, 2L, 3L, true);
        when(courseBundleRepository.existsByBundleIdAndCourseId(2L, 3L)).thenReturn(false);
        when(bundleRepository.existsById(2L)).thenReturn(true);
        when(courseRepository.existsById(3L)).thenReturn(true);
        when(courseBundleRepository.save(any(CourseBundle.class))).thenReturn(courseBundle);

        CourseBundle result = courseBundleService.createCourseBundle(inDTO);

        assertEquals(2L, result.getBundleId());
        verify(courseBundleRepository).save(any());
    }

    @Test
    void createCourseBundle_alreadyExists() {
        CourseBundleInDTO inDTO = new CourseBundleInDTO(0L, 2L, 3L, true);
        when(courseBundleRepository.existsByBundleIdAndCourseId(2L, 3L)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> courseBundleService.createCourseBundle(inDTO));
    }

    @Test
    void createCourseBundle_invalidBundleId() {
        CourseBundleInDTO inDTO = new CourseBundleInDTO(0L, 2L, 3L, true);
        when(courseBundleRepository.existsByBundleIdAndCourseId(2L, 3L)).thenReturn(false);
        when(bundleRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotValidException.class, () -> courseBundleService.createCourseBundle(inDTO));
    }

    @Test
    void createCourseBundle_invalidCourseId() {
        CourseBundleInDTO inDTO = new CourseBundleInDTO(0L, 2L, 3L, true);
        when(courseBundleRepository.existsByBundleIdAndCourseId(2L, 3L)).thenReturn(false);
        when(bundleRepository.existsById(2L)).thenReturn(true);
        when(courseRepository.existsById(3L)).thenReturn(false);

        assertThrows(ResourceNotValidException.class, () -> courseBundleService.createCourseBundle(inDTO));
    }

    @Test
    void getAllCourseBundles_success() {
        when(courseBundleRepository.findAll()).thenReturn(Arrays.asList(courseBundle));
        when(bundleRepository.findById(2L)).thenReturn(Optional.of(bundle));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course)); // Intentional mismatch in impl

        List<CourseBundleOutDTO> result = courseBundleService.getAllCourseBundles();

        assertEquals(1, result.size());
        assertEquals("Dev Bundle", result.get(0).getBundleName());
    }

    @Test
    void getAllCourseBundles_empty_throws() {
        when(courseBundleRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.getAllCourseBundles());
    }

    @Test
    void getCourseBundleById_success() {
        when(courseBundleRepository.findById(1L)).thenReturn(Optional.of(courseBundle));
        when(bundleRepository.findById(2L)).thenReturn(Optional.of(bundle));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));

        CourseBundleOutDTO result = courseBundleService.getCourseBundleById(1L);
        assertEquals("Dev Bundle", result.getBundleName());
        assertEquals("Java Mastery", result.getCourseName());
    }

    @Test
    void getCourseBundleById_notFound() {
        when(courseBundleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.getCourseBundleById(1L));
    }

    @Test
    void deleteCourseBundle_success() {
        when(courseBundleRepository.findById(1L)).thenReturn(Optional.of(courseBundle));
        assertDoesNotThrow(() -> courseBundleService.deleteCourseBundle(1L));
        verify(courseBundleRepository).delete(courseBundle);
    }

    @Test
    void deleteCourseBundle_notFound() {
        when(courseBundleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.deleteCourseBundle(1L));
    }

    @Test
    void updateCourseBundle_success() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(2L, 3L, true);
        when(courseBundleRepository.findById(1L)).thenReturn(Optional.of(courseBundle));
        when(courseBundleRepository.save(any())).thenReturn(courseBundle);

        String result = courseBundleService.updateCourseBundle(1L, dto);
        assertEquals("Course Bundle Updated Successfully", result);
    }

    @Test
    void updateCourseBundle_notFound() {
        UpdateCourseBundleInDTO dto = new UpdateCourseBundleInDTO(2L, 3L, true);
        when(courseBundleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.updateCourseBundle(1L, dto));
    }

    @Test
    void getBundlesInfo_success() {
        when(bundleRepository.findAll()).thenReturn(Arrays.asList(bundle));
        when(bundleRepository.findById(2L)).thenReturn(Optional.of(bundle));
        when(courseBundleRepository.countByBundleId(2L)).thenReturn(5L);

        List<BundleInfoOutDTO> result = courseBundleService.getBundlesInfo();
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getTotalCourses());
    }

    @Test
    void getBundlesInfo_empty() {
        when(bundleRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.getBundlesInfo());
    }

    @Test
    void getAllCoursesByBundle_success() {
        when(courseBundleRepository.findByBundleId(2L)).thenReturn(Arrays.asList(courseBundle));
        when(courseRepository.findById(3L)).thenReturn(Optional.of(course));
        List<CourseInfoOutDTO> result = courseBundleService.getAllCoursesByBundle(2L);
        assertEquals(1, result.size());
        assertEquals("Java Mastery", result.get(0).getTitle());
    }

    @Test
    void getAllCoursesByBundle_empty() {
        when(courseBundleRepository.findByBundleId(2L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.getAllCoursesByBundle(2L));
    }

    @Test
    void getRecentBundleSummaries_success() {
        when(bundleRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(bundle));
        when(courseBundleRepository.countByBundleId(2L)).thenReturn(3L);

        List<BundleSummaryOutDTO> result = courseBundleService.getRecentBundleSummaries();
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getCourseCount());
    }

    @Test
    void findCourseIdsByBundleId_success() {
        when(courseBundleRepository.findCourseIdsByBundleId(2L)).thenReturn(Arrays.asList(3L));
        List<Long> result = courseBundleService.findCourseIdsByBundleId(2L);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0));
    }

    @Test
    void findCourseIdsByBundleId_notFound() {
        when(courseBundleRepository.findCourseIdsByBundleId(2L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> courseBundleService.findCourseIdsByBundleId(2L));
    }
}

