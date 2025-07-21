package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.CourseContentController;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.CourseContentService;
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

public class CourseContentControllerTest {

    @InjectMocks
    private CourseContentController controller;

    @Mock
    private CourseContentService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourseContent() {
        CourseContentInDTO inDTO = new CourseContentInDTO();
        inDTO.setTitle("Intro");
        inDTO.setCourseId(1L);

        CourseContentOutDTO outDTO = new CourseContentOutDTO();
        outDTO.setCourseContentId(10L);
        outDTO.setTitle("Intro");

        when(service.createCourseContent(inDTO)).thenReturn(outDTO);

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = controller.createCourseContent(inDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Intro", response.getBody().getData().getTitle());
    }

    @Test
    void testGetAllCourseContents() {
        CourseContentOutDTO c1 = new CourseContentOutDTO();
        c1.setTitle("Topic 1");

        CourseContentOutDTO c2 = new CourseContentOutDTO();
        c2.setTitle("Topic 2");

        when(service.getAllCourseContents()).thenReturn(Arrays.asList(c1, c2));

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = controller.getAllCourseContents();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllCourseContents_Empty() {
        when(service.getAllCourseContents()).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = controller.getAllCourseContents();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetCourseContentById() {
        Long id = 1L;
        CourseContentOutDTO dto = new CourseContentOutDTO();
        dto.setCourseContentId(id);
        dto.setTitle("Unit");

        when(service.getCourseContentById(id)).thenReturn(dto);

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = controller.getCourseContentById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Unit", response.getBody().getData().getTitle());
    }

    @Test
    void testGetCourseContentByCourseId() {
        Long courseId = 101L;

        CourseContentOutDTO dto = new CourseContentOutDTO();
        dto.setCourseContentId(1L);

        when(service.getAllCourseContentByCourseId(courseId)).thenReturn(Collections.singletonList(dto));

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = controller.getCourseContentByCourseId(courseId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void testDeleteCourseContent() {
        Long id = 99L;
        when(service.deleteCourseContent(id)).thenReturn("Deleted");

        ResponseEntity<StandardResponseOutDTO<Void>> response = controller.deleteCourseContent(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deleted", response.getBody().getMessage());
        verify(service, times(1)).deleteCourseContent(id);
    }

    @Test
    void testUpdateCourseContent() {
        Long id = 1L;
        UpdateCourseContentInDTO updateDTO = new UpdateCourseContentInDTO();
        updateDTO.setTitle("Updated");

        CourseContentOutDTO updatedDTO = new CourseContentOutDTO();
        updatedDTO.setCourseContentId(id);
        updatedDTO.setTitle("Updated");

        when(service.updateCourseContent(id, updateDTO)).thenReturn(updatedDTO);

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = controller.updateCourseContent(id, updateDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getData().getTitle());
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<StandardResponseOutDTO<String>> response = controller.healthCheck();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("UP", response.getBody().getData());
    }

    @Test
    void testGetCourseContentCount() {
        Long courseId = 3L;
        List<CourseContentOutDTO> list = Arrays.asList(new CourseContentOutDTO(), new CourseContentOutDTO());

        when(service.getAllCourseContentByCourseId(courseId)).thenReturn(list);

        ResponseEntity<StandardResponseOutDTO<Integer>> response = controller.getCourseContentCount(courseId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData());
    }

    @Test
    void testGetCourseContentCount_Empty() {
        Long courseId = 3L;

        when(service.getAllCourseContentByCourseId(courseId)).thenReturn(Collections.emptyList());

        ResponseEntity<StandardResponseOutDTO<Integer>> response = controller.getCourseContentCount(courseId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().getData());
    }
}
