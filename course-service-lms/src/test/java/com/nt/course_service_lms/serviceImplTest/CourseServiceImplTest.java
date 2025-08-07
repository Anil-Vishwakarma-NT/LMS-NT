package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.CourseBundleRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.service.serviceImpl.CourseServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseBundleRepository courseBundleRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private CourseInDTO courseInDTO;
    private UpdateCourseInDTO updateCourseInDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        course = Course.builder()
                .courseId(1L)
                .ownerId(100L)
                .title("Java Basics")
                .description("Learn Java")
                .level("BEGINNER")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        courseInDTO = CourseInDTO.builder()
                .title("Java Basics")
                .ownerId(100L)
                .description("Learn Java")
                .courseLevel("BEGINNER")
                .isActive(true)
                .build();

        updateCourseInDTO = UpdateCourseInDTO.builder()
                .title("Java Advanced")
                .ownerId(100L)
                .description("Deep dive into Java")
                .courseLevel("ADVANCED")
                .Active(false)
                .build();
    }

    @Test
    void testCreateCourse_success() {
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId("Java Basics", 100L))
                .thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseOutDTO result = courseService.createCourse(courseInDTO);

        assertNotNull(result);
        assertEquals("Java Basics", result.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testCreateCourse_alreadyExists() {
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId("Java Basics", 100L))
                .thenReturn(Optional.of(course));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> courseService.createCourse(courseInDTO));
    }

    @Test
    void testGetAllCourses_success() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        List<CourseOutDTO> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
    }

    @Test
    void testGetAllCourses_emptyList() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getAllCourses());
    }

    @Test
    void testGetCourseById_success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseInfoOutDTO result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals("Java Basics", result.getTitle());
    }

    @Test
    void testGetCourseById_notFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(1L));
    }

    @Test
    void testGetCourseNameById_success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String name = courseService.getCourseNameById(1L);

        assertEquals("Java Basics", name);
    }

    @Test
    void testGetCourseNameById_notFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseNameById(1L));
    }

    @Test
    void testDeleteCourse_success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String result = courseService.deleteCourse(1L);

        assertEquals("Course Deleted Successfully", result);
        verify(courseRepository).delete(course);
    }

    @Test
    void testDeleteCourse_notFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.deleteCourse(1L));
    }

    @Test
    void testGetCoursesInfo_success() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        List<CourseInfoOutDTO> result = courseService.getCoursesInfo();

        assertEquals(1, result.size());
    }

    @Test
    void testGetCoursesInfo_empty() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCoursesInfo());
    }

    @Test
    void testFindExistingIds_success() {
        List<Long> inputIds = Arrays.asList(1L, 2L);
        List<Long> foundIds = Arrays.asList(1L);

        when(courseRepository.findExistingIds(inputIds)).thenReturn(foundIds);

        List<Long> result = courseService.findExistingIds(inputIds);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    void testFindExistingIds_noneFound() {
        when(courseRepository.findExistingIds(anyList())).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseService.findExistingIds(Arrays.asList(1L)));
    }

    @Test
    void testFindExistingIds_internalError() {
        when(courseRepository.findExistingIds(anyList())).thenThrow(new RuntimeException("DB failure"));

        assertThrows(RuntimeException.class, () -> courseService.findExistingIds(Arrays.asList(1L)));
    }

    @Test
    void testUpdateCourse_success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId("Java Advanced", 100L)).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseOutDTO result = courseService.updateCourse(1L, updateCourseInDTO);

        assertNotNull(result);
        assertEquals("Java Advanced", result.getTitle()); // OutDTO comes from existing saved course mock
    }

    @Test
    void testUpdateCourse_notFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(1L, updateCourseInDTO));
    }

    @Test
    void testUpdateCourse_duplicateTitleOwner() {
        Course duplicate = Course.builder().courseId(2L).title("Java Advanced").ownerId(100L).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId("Java Advanced", 100L)).thenReturn(Optional.of(duplicate));

        assertThrows(ResourceAlreadyExistsException.class, () -> courseService.updateCourse(1L, updateCourseInDTO));
    }

    @Test
    void testCourseExistsById_true() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        assertTrue(courseService.courseExistsById(1L));
    }

    @Test
    void testCourseExistsById_false() {
        when(courseRepository.existsById(1L)).thenReturn(false);

        assertFalse(courseService.courseExistsById(1L));
    }

    @Test
    void testCountCourses() {
        when(courseRepository.count()).thenReturn(5L);

        assertEquals(5L, courseService.countCourses());
    }

    @Test
    void testGetRecentCourseSummaries_success() {
        when(courseRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(course));

        List<CourseSummaryOutDTO> result = courseService.getRecentCourseSummaries();

        assertEquals(1, result.size());
    }

    @Test
    void testGetRecentCourseSummaries_empty() {
        when(courseRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getRecentCourseSummaries());
    }
}


