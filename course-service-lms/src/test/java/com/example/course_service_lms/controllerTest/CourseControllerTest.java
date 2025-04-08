package com.example.course_service_lms.controllerTest;

import com.example.course_service_lms.controller.CourseController;
import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourse() {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setTitle("Java Basics");

        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("Java Basics");

        when(courseService.createCourse(courseDTO)).thenReturn(course);

        ResponseEntity<Course> response = courseController.createCourse(courseDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Java Basics", response.getBody().getTitle());
    }

    @Test
    void testGetAllCourses() {
        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setTitle("Java");

        Course course2 = new Course();
        course2.setCourseId(2L);
        course2.setTitle("Spring Boot");

        List<Course> courseList = Arrays.asList(course1, course2);

        when(courseService.getAllCourses()).thenReturn(courseList);

        ResponseEntity<List<Course>> response = courseController.getAllCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetCourseById() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("Spring Security");

        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course));

        ResponseEntity<Optional<Course>> response = courseController.getCourseById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isPresent());
        assertEquals("Spring Security", response.getBody().get().getTitle());
    }

    @Test
    void testDeleteCourse() {
        Long courseId = 1L;
        String expectedMsg = "Course with ID 1 deleted successfully.";

        when(courseService.deleteCourse(courseId)).thenReturn(expectedMsg);

        ResponseEntity<String> response = courseController.deleteCourse(courseId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedMsg, response.getBody());
    }

    @Test
    void testUpdateCourse() {
        Long courseId = 1L;
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setTitle("Updated Course");

        String updateResponse = "Course updated successfully";

        when(courseService.updateCourse(courseId, courseDTO)).thenReturn(updateResponse);

        ResponseEntity<String> response = courseController.updateCourse(courseId, courseDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updateResponse, response.getBody());
    }
}
