package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.CourseController;
import com.nt.course_service_lms.dto.inDTO.CourseInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.nt.course_service_lms.dto.outDTO.*;
import com.nt.course_service_lms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        CourseInDTO input = new CourseInDTO();
        input.setTitle("Java 101");

        CourseOutDTO output = new CourseOutDTO();
        output.setTitle("Java 101");

        when(courseService.createCourse(input)).thenReturn(output);

        ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> response = courseController.createCourse(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Java 101", response.getBody().getData().getTitle());
    }

    @Test
    void testGetAllCourses() {
        CourseOutDTO c1 = new CourseOutDTO(); c1.setTitle("A");
        CourseOutDTO c2 = new CourseOutDTO(); c2.setTitle("B");

        when(courseService.getAllCourses()).thenReturn(Arrays.asList(c1, c2));

        ResponseEntity<StandardResponseOutDTO<List<CourseOutDTO>>> response = courseController.getAllCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllCoursesEmpty() {
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<CourseOutDTO>>> response = courseController.getAllCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetCourseById() {
        Long id = 1L;
        CourseInfoOutDTO dto = new CourseInfoOutDTO();
        dto.setCourseId(id);

        when(courseService.getCourseById(id)).thenReturn(dto);

        ResponseEntity<StandardResponseOutDTO<CourseInfoOutDTO>> response = courseController.getCourseById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(id, response.getBody().getData().getCourseId());
    }

    @Test
    void testDeleteCourse() {
        Long id = 5L;

        when(courseService.deleteCourse(id)).thenReturn("Deleted");

        ResponseEntity<StandardResponseOutDTO<Void>> response = courseController.deleteCourse(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deleted", response.getBody().getMessage());
        verify(courseService, times(1)).deleteCourse(id);
    }

    @Test
    void testUpdateCourse() {
        Long id = 7L;
        UpdateCourseInDTO update = new UpdateCourseInDTO();
        update.setTitle("Updated Course");

        CourseOutDTO updated = new CourseOutDTO();
        updated.setTitle("Updated Course");

        when(courseService.updateCourse(id, update)).thenReturn(updated);

        ResponseEntity<StandardResponseOutDTO<CourseOutDTO>> response = courseController.updateCourse(id, update);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Course", response.getBody().getData().getTitle());
    }

    @Test
    void testCheckIfCourseExists_True() {
        Long id = 10L;
        when(courseService.courseExistsById(id)).thenReturn(true);

        ResponseEntity<Boolean> response = courseController.checkIfCourseExists(id);

        assertTrue(response.getBody());
    }

    @Test
    void testCheckIfCourseExists_False() {
        Long id = 11L;
        when(courseService.courseExistsById(id)).thenReturn(false);

        ResponseEntity<Boolean> response = courseController.checkIfCourseExists(id);

        assertFalse(response.getBody());
    }

    @Test
    void testGetCourseCount() {
        when(courseService.countCourses()).thenReturn(42L);

        ResponseEntity<StandardResponseOutDTO<Long>> response = courseController.getCourseCount();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(42L, response.getBody().getData());
    }

    @Test
    void testGetRecentCourses() {
        CourseSummaryOutDTO dto1 = new CourseSummaryOutDTO();
        dto1.setTitle("Summary 1");

        when(courseService.getRecentCourseSummaries()).thenReturn(Collections.singletonList(dto1));

        ResponseEntity<StandardResponseOutDTO<List<CourseSummaryOutDTO>>> response = courseController.getRecentCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Summary 1", response.getBody().getData().get(0).getTitle());
    }

    @Test
    void testGetCourseNameById() {
        Long id = 100L;
        when(courseService.getCourseNameById(id)).thenReturn("Course Name");

        ResponseEntity<String> response = courseController.getCourseNameById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Course Name", response.getBody());
    }

    @Test
    void testGetCoursesInfo() {
        CourseInfoOutDTO courseInfo = new CourseInfoOutDTO();
        courseInfo.setTitle("Full Info");

        when(courseService.getCoursesInfo()).thenReturn(Collections.singletonList(courseInfo));

        ResponseEntity<StandardResponseOutDTO<List<CourseInfoOutDTO>>> response = courseController.getCoursesInfo();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Full Info", response.getBody().getData().get(0).getTitle());
    }

    @Test
    void testGetExistingCourseIds() {
        List<Long> inputIds = Arrays.asList(1L, 2L, 3L);
        List<Long> existing = Arrays.asList(1L, 3L);

        when(courseService.findExistingIds(inputIds)).thenReturn(existing);

        ResponseEntity<List<Long>> response = courseController.getExistingCourseIds(inputIds);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(1L));
        assertFalse(response.getBody().contains(2L));
    }
}
