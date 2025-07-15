package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.entity.Bundle;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseBundle;
import com.nt.course_service_lms.repository.BundleRepository;
import com.nt.course_service_lms.repository.CourseBundleRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.dto.inDTO.CourseBundleInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseBundleInDTO;
import com.nt.course_service_lms.dto.outDTO.*;
import com.nt.course_service_lms.exception.*;
import com.nt.course_service_lms.service.serviceImpl.CourseBundleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseBundleServiceImplTest {

    @Mock CourseBundleRepository courseBundleRepo;
    @Mock BundleRepository bundleRepo;
    @Mock CourseRepository courseRepo;

    @InjectMocks
    CourseBundleServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test void getAllCourseBundles_empty_throws() {
        when(courseBundleRepo.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, service::getAllCourseBundles);
    }

    @Test void getAllCourseBundles_bundleMissing_throws() {
        CourseBundle cb = new CourseBundle(); cb.setBundleId(99L); cb.setCourseBundleId(1L); cb.setCourseId(88L);
        when(courseBundleRepo.findAll()).thenReturn(Arrays.asList(cb));
        when(bundleRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, service::getAllCourseBundles);
    }

    @Test void getAllCourseBundles_courseMissing_throws() {
        CourseBundle cb = new CourseBundle(); cb.setBundleId(11L); cb.setCourseBundleId(1L); cb.setCourseId(22L);
        when(courseBundleRepo.findAll()).thenReturn(Arrays.asList(cb));
        when(bundleRepo.findById(11L)).thenReturn(Optional.of(new Bundle()));
        when(courseRepo.findById(11L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, service::getAllCourseBundles);
    }

    @Test void getAllCourseBundles_success() {
        CourseBundle cb = new CourseBundle(); cb.setBundleId(11L); cb.setCourseBundleId(10L); cb.setCourseId(11L);
        Bundle b = new Bundle(); b.setBundleId(11L); b.setBundleName("BundleX");
        Course c = new Course(); c.setCourseId(11L); c.setTitle("CourseY");

        when(courseBundleRepo.findAll()).thenReturn(Arrays.asList(cb));
        when(bundleRepo.findById(11L)).thenReturn(Optional.of(b));
        when(courseRepo.findById(11L)).thenReturn(Optional.of(c));

        var result = service.getAllCourseBundles();
        assertEquals(1, result.size());
        CourseBundleOutDTO dto = result.get(0);
        assertEquals("BundleX", dto.getBundleName());
        assertEquals("CourseY", dto.getCourseName());
    }

    @Test void getCourseBundleById_notFound_throws() {
        when(courseBundleRepo.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCourseBundleById(100L));
    }

    @Test void getCourseBundleById_bundleMissing_throws() {
        CourseBundle cb = new CourseBundle(); cb.setBundleId(5L); cb.setCourseBundleId(200L); cb.setCourseId(6L);
        when(courseBundleRepo.findById(200L)).thenReturn(Optional.of(cb));
        when(bundleRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCourseBundleById(200L));
    }

    @Test void getCourseBundleById_courseMissing_throws() {
        CourseBundle cb = new CourseBundle(); cb.setBundleId(7L); cb.setCourseBundleId(300L); cb.setCourseId(7L);
        when(courseBundleRepo.findById(300L)).thenReturn(Optional.of(cb));
        when(bundleRepo.findById(7L)).thenReturn(Optional.of(new Bundle()));
        when(courseRepo.findById(7L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCourseBundleById(300L));
    }

    @Test void getCourseBundleById_success() {
        CourseBundle cb = new CourseBundle(); cb.setBundleId(8L); cb.setCourseBundleId(400L); cb.setCourseId(8L);
        Bundle b = new Bundle(); b.setBundleId(8L); b.setBundleName("BX");
        Course c = new Course(); c.setCourseId(8L); c.setTitle("CX");

        when(courseBundleRepo.findById(400L)).thenReturn(Optional.of(cb));
        when(bundleRepo.findById(8L)).thenReturn(Optional.of(b));
        when(courseRepo.findById(8L)).thenReturn(Optional.of(c));

        CourseBundleOutDTO dto = service.getCourseBundleById(400L);
        assertEquals("BX", dto.getBundleName());
        assertEquals("CX", dto.getCourseName());
    }

    @Test void deleteCourseBundle_notFound_throws() {
        when(courseBundleRepo.findById(500L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteCourseBundle(500L));
    }

    @Test void deleteCourseBundle_success() {
        CourseBundle cb = new CourseBundle(); cb.setCourseBundleId(600L);
        when(courseBundleRepo.findById(600L)).thenReturn(Optional.of(cb));
        service.deleteCourseBundle(600L);
        verify(courseBundleRepo).delete(cb);
    }

    @Test void updateCourseBundle_notFound_throws() {
        when(courseBundleRepo.findById(700L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCourseBundle(700L, new UpdateCourseBundleInDTO(1L,2L,true)));
    }

    @Test void updateCourseBundle_success() {
        CourseBundle existing = new CourseBundle();
        existing.setCourseBundleId(800L);
        existing.setBundleId(1L);
        existing.setCourseId(2L);
        existing.setActive(false);

        UpdateCourseBundleInDTO u = new UpdateCourseBundleInDTO(3L, 4L, true);
        when(courseBundleRepo.findById(800L)).thenReturn(Optional.of(existing));
        when(courseBundleRepo.save(existing)).thenReturn(existing);

        String res = service.updateCourseBundle(800L, u);
        assertEquals("Course Bundle Updated Successfully", res);
        assertEquals(true, existing.isActive());
        assertEquals(3L, existing.getBundleId());
        assertEquals(4L, existing.getCourseId());
        verify(courseBundleRepo).save(existing);
    }

    @Test void updateCourseBundle_setActiveFalse() {
        CourseBundle existing = new CourseBundle();
        existing.setCourseBundleId(901L);
        existing.setBundleId(1L);
        existing.setCourseId(2L);
        existing.setActive(true);

        UpdateCourseBundleInDTO u = new UpdateCourseBundleInDTO(3L, 4L, false);
        when(courseBundleRepo.findById(901L)).thenReturn(Optional.of(existing));
        when(courseBundleRepo.save(existing)).thenReturn(existing);

        String res = service.updateCourseBundle(901L, u);
        assertEquals("Course Bundle Updated Successfully", res);
        assertFalse(existing.isActive());
        verify(courseBundleRepo).save(existing);
    }

    @Test void createCourseBundle_existing_throws() {
        CourseBundleInDTO in = new CourseBundleInDTO(0, 11L, 22L, true);
        when(courseBundleRepo.existsByBundleIdAndCourseId(11L,22L)).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> service.createCourseBundle(in));
    }

    @Test void createCourseBundle_invalidBundle_throws() {
        CourseBundleInDTO in = new CourseBundleInDTO(0, 11L, 22L, true);
        when(courseBundleRepo.existsByBundleIdAndCourseId(11L,22L)).thenReturn(false);
        when(bundleRepo.existsById(11L)).thenReturn(false);
        assertThrows(ResourceNotValidException.class, () -> service.createCourseBundle(in));
    }

    @Test void createCourseBundle_invalidCourse_throws() {
        CourseBundleInDTO in = new CourseBundleInDTO(0, 11L, 22L, true);
        when(courseBundleRepo.existsByBundleIdAndCourseId(11L,22L)).thenReturn(false);
        when(bundleRepo.existsById(11L)).thenReturn(true);
        when(courseRepo.existsById(22L)).thenReturn(false);
        assertThrows(ResourceNotValidException.class, () -> service.createCourseBundle(in));
    }

    @Test void createCourseBundle_success() {
        CourseBundleInDTO in = new CourseBundleInDTO(0, 11L, 22L, true);
        when(courseBundleRepo.existsByBundleIdAndCourseId(11L,22L)).thenReturn(false);
        when(bundleRepo.existsById(11L)).thenReturn(true);
        when(courseRepo.existsById(22L)).thenReturn(true);

        CourseBundle saved = new CourseBundle();
        saved.setCourseBundleId(999L);
        when(courseBundleRepo.save(any())).thenReturn(saved);

        CourseBundle out = service.createCourseBundle(in);
        assertEquals(999L, out.getCourseBundleId());
    }

    @Test void getBundlesInfo_empty_throws() {
        when(bundleRepo.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, service::getBundlesInfo);
    }

    @Test void getBundlesInfo_success() {
        Bundle b = new Bundle();
        b.setBundleId(12L);
        b.setBundleName("Bundle12");
        b.setActive(true);
        b.setCreatedAt(LocalDateTime.now());
        b.setUpdatedAt(LocalDateTime.now());

        when(bundleRepo.findAll()).thenReturn(Arrays.asList(b));
        when(bundleRepo.findById(12L)).thenReturn(Optional.of(b));
        when(courseBundleRepo.countByBundleId(12L)).thenReturn(5L);

        var infos = service.getBundlesInfo();
        assertEquals(1, infos.size());
        BundleInfoOutDTO bi = infos.get(0);
        assertEquals(12L, bi.getBundleId());
        assertEquals("Bundle12", bi.getBundleName());
        assertEquals(5L, bi.getTotalCourses());
        assertTrue(bi.isActive());
    }

    @Test void getAllCoursesByBundle_empty_throws() {
        when(courseBundleRepo.findByBundleId(20L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> service.getAllCoursesByBundle(20L));
    }

    @Test void getAllCoursesByBundle_success() {
        CourseBundle cb1 = new CourseBundle(); cb1.setCourseBundleId(101L);
        CourseBundle cb2 = new CourseBundle(); cb2.setCourseBundleId(102L);
        when(courseBundleRepo.findByBundleId(15L)).thenReturn(Arrays.asList(cb1, cb2));

        var list = service.getAllCoursesByBundle(15L);
        assertEquals(2, list.size());
    }

    @Test void getRecentBundleSummaries_success() {
        Bundle b1 = new Bundle(); b1.setBundleId(21L); b1.setBundleName("B1"); b1.setCreatedAt(LocalDateTime.now()); b1.setUpdatedAt(LocalDateTime.now());
        Bundle b2 = new Bundle(); b2.setBundleId(22L); b2.setBundleName("B2"); b2.setCreatedAt(LocalDateTime.now()); b2.setUpdatedAt(LocalDateTime.now());

        when(bundleRepo.findTop5ByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(b1, b2));
        when(courseBundleRepo.countByBundleId(21L)).thenReturn(2L);
        when(courseBundleRepo.countByBundleId(22L)).thenReturn(3L);

        var sums = service.getRecentBundleSummaries();
        assertEquals(2, sums.size());
        assertEquals(21L, sums.get(0).getBundleId());
        assertEquals(2L, sums.get(0).getCourseCount());
    }

    @Test void findCourseIdsByBundleId_empty_throws() {
        when(courseBundleRepo.findCourseIdsByBundleId(30L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> service.findCourseIdsByBundleId(30L));
    }

    @Test void findCourseIdsByBundleId_success() {
        List<Long> ids = Arrays.asList(101L, 102L);
        when(courseBundleRepo.findCourseIdsByBundleId(40L)).thenReturn(ids);
        var result = service.findCourseIdsByBundleId(40L);
        assertEquals(ids, result);
    }

    @Test void getAllCourseBundles_runtimeWrapped() {
        when(courseBundleRepo.findAll()).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, service::getAllCourseBundles);
    }

    @Test void getCourseBundleById_runtimeWrapped() {
        when(courseBundleRepo.findById(50L)).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.getCourseBundleById(50L));
    }

    @Test void createCourseBundle_runtimeWrapped() {
        CourseBundleInDTO in = new CourseBundleInDTO(0, 51L, 52L, true);
        when(courseBundleRepo.existsByBundleIdAndCourseId(51L,52L)).thenReturn(false);
        when(bundleRepo.existsById(51L)).thenReturn(true);
        when(courseRepo.existsById(52L)).thenReturn(true);
        when(courseBundleRepo.save(any())).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.createCourseBundle(in));
    }

    @Test void getBundlesInfo_runtimeWrapped() {
        when(bundleRepo.findAll()).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, service::getBundlesInfo);
    }

    @Test void getAllCoursesByBundle_runtimeWrapped() {
        when(courseBundleRepo.findByBundleId(60L)).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.getAllCoursesByBundle(60L));
    }

    @Test void findCourseIdsByBundleId_runtimeWrapped() {
        when(courseBundleRepo.findCourseIdsByBundleId(70L)).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.findCourseIdsByBundleId(70L));
    }
}
