package com.nt.course_service_lms.controllerTest;

import com.nt.course_service_lms.controller.UserProgressController;
import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.service.UserProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserProgressControllerTest {

    @InjectMocks
    private UserProgressController controller;

    @Mock
    private UserProgressService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateProgress() {
        UserProgressOutDTO progressDTO = UserProgressOutDTO.builder()
                .userId(1L)
                .courseId(2L)
                .contentId(3L)
                .contentType("video")
                .lastPosition(80.5)
                .contentCompletionPercentage(70.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        doNothing().when(service).updateProgress(progressDTO);

        // This method returns void, so we just call it and verify
        controller.updateProgress(progressDTO);

        verify(service, times(1)).updateProgress(progressDTO);
    }

    @Test
    void testGetCourseProgressWithMetaWithId() {
        Long userId = 1L;
        Long courseId = 2L;
        CourseProgressWithMetaDTO dto = new CourseProgressWithMetaDTO(95.5, LocalDateTime.now());

        when(service.getCourseProgressWithMeta(userId, courseId)).thenReturn(dto);

        CourseProgressWithMetaDTO result = controller.getCourseProgressWithMetaWithId(userId, courseId);

        assertNotNull(result);
        assertEquals(95.5, result.getCourseCompletionPercentage());
        assertNotNull(result.getFirstCompletedAt());
    }

    @Test
    void testGetLastPosition() {
        Long userId = 1L, courseId = 2L, contentId = 3L;

        when(service.getLastPosition(userId, courseId, contentId)).thenReturn(42);

        Integer result = controller.getLastPosition(userId, courseId, contentId);

        assertEquals(42, result);
    }

    @Test
    void testGetLastPosition_NotFound() {
        Long userId = 1L, courseId = 2L, contentId = 3L;

        when(service.getLastPosition(userId, courseId, contentId)).thenReturn(0);

        Integer result = controller.getLastPosition(userId, courseId, contentId);

        assertEquals(0, result);
    }

    @Test
    void testGetContentProgress() {
        Long userId = 1L, courseId = 2L, contentId = 3L;

        when(service.getContentProgress(userId, courseId, contentId)).thenReturn(0.9);

        Double result = controller.getContentProgress(userId, courseId, contentId);

        assertEquals(0.9, result);
    }

    @Test
    void testGetContentProgress_Empty() {
        Long userId = 1L, courseId = 2L, contentId = 3L;

        when(service.getContentProgress(userId, courseId, contentId)).thenReturn(0.0);

        Double result = controller.getContentProgress(userId, courseId, contentId);

        assertEquals(0.0, result);
    }
}
