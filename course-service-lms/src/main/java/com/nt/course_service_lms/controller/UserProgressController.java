package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing user progress in the Learning Management System.
 * Provides endpoints for updating and retrieving user progress data for courses and content.
 */
@RestController
@RequestMapping("/api/service-api/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    /**
     * Service for handling user progress business logic and operations.
     */
    private final UserProgressService userProgressService;

    /**
     * Updates user progress when interacting with content.
     *
     * @param progressDTO the user progress data transfer object containing progress information
     */
    @PostMapping("/update")
    public void updateProgress(@RequestBody final UserProgressOutDTO progressDTO) {
        userProgressService.updateProgress(progressDTO);
    }

    /**
     * Retrieves course progress with metadata for a specific user and course.
     *
     * @param userId   the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @return CourseProgressWithMetaDTO containing course progress and metadata
     */
    @GetMapping("/meta")
    public CourseProgressWithMetaDTO getCourseProgressWithMetaWithId(
            @RequestParam final Long userId, @RequestParam final Long courseId
    ) {
        return userProgressService.getCourseProgressWithMeta(userId, courseId);
    }

    /**
     * Retrieves the last position of a user in specific content within a course.
     *
     * @param userId    the unique identifier of the user
     * @param courseId  the unique identifier of the course
     * @param contentId the unique identifier of the content
     * @return Integer representing the last position in the content
     */
    @GetMapping("/last-position")
    public Integer getLastPosition(
            @RequestParam final Long userId,
            @RequestParam final Long courseId,
            @RequestParam final Long contentId
    ) {
        return userProgressService.getLastPosition(userId, courseId, contentId);
    }

    /**
     * Retrieves the progress percentage for specific content within a course for a user.
     *
     * @param userId    the unique identifier of the user
     * @param courseId  the unique identifier of the course
     * @param contentId the unique identifier of the content
     * @return Double representing the progress percentage (0.0 to 1.0)
     */
    @GetMapping("/content")
    public Double getContentProgress(
            @RequestParam final Long userId,
            @RequestParam final Long courseId,
            @RequestParam final Long contentId
    ) {
        return userProgressService.getContentProgress(userId, courseId, contentId);
    }
}
