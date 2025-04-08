package com.example.course_service_lms.serviceImplTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.course_service_lms.Enum.CourseLevel;
import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.CourseRepository;
import com.example.course_service_lms.serviceImpl.CourseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class CourseImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseImpl courseService;

    private CourseDTO courseDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        courseDTO = new CourseDTO("Java Basics", 1L, "Learn Java", "BEGINNER", "img.jpg");
        course = new Course(1L, 1L, "Java Basics", "Learn Java", "img.jpg", com.example.course_service_lms.Enum.CourseLevel.BEGINNER);
    }
    @Test
    void testCreateCourse_Success() {
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course created = courseService.createCourse(courseDTO);

        assertNotNull(created);
        assertEquals(course.getTitle(), created.getTitle());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testCreateCourse_AlreadyExists() {
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId(anyString(), anyLong())).thenReturn(Optional.of(course));

        assertThrows(ResourceAlreadyExistsException.class, () -> courseService.createCourse(courseDTO));
    }
    @Test
    void testGetAllCourses_Success() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        List<Course> result = courseService.getAllCourses();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllCourses_NotFound() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getAllCourses());
    }
    @Test
    void testGetCourseById_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.getCourseById(1L);

        assertTrue(result.isPresent());
        assertEquals("Java Basics", result.get().getTitle());
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(1L));
    }
    @Test
    void testDeleteCourse_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String result = courseService.deleteCourse(1L);

        assertEquals("Course Deleted Successfully", result);
        verify(courseRepository).delete(course);
    }

    @Test
    void testDeleteCourse_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.deleteCourse(1L));
    }
    @Test
    void testUpdateCourse_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId(anyString(), anyLong())).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        String result = courseService.updateCourse(1L, courseDTO);

        assertEquals("Course Updated Successfully", result);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testUpdateCourse_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(1L, courseDTO));
    }

    @Test
    void testUpdateCourse_DuplicateTitle() {
        // course in DB with same title/owner but different ID
        Course existingCourse = new Course(1L, 1L, "Old Title", "desc", "img.jpg", CourseLevel.BEGINNER);
        Course duplicateCourse = new Course(2L, 1L, "New Title", "desc", "img.jpg", CourseLevel.BEGINNER);

        CourseDTO dtoWithDuplicateTitle = new CourseDTO("New Title", 1L, "desc", "BEGINNER", "img.jpg");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.findByTitleIgnoreCaseAndOwnerId("New Title", 1L)).thenReturn(Optional.of(duplicateCourse));

        assertThrows(ResourceNotValidException.class, () -> courseService.updateCourse(1L, dtoWithDuplicateTitle));
    }

}
