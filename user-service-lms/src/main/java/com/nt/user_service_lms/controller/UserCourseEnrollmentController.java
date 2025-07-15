package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for managing user course enrollment operations.
 * Provides endpoints for retrieving enrollment statistics and related data
 * for users in the Learning Management System.
 *
 * @author Generated
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/service-api/enrollments")
public class UserCourseEnrollmentController {

    /**
     * Service layer component for handling user-related business logic.
     * Provides methods for retrieving user statistics and enrollment data.
     */
    @Autowired
    private UserService userService;

    /**
     * Retrieves enrollment statistics for a specific user.
     * Returns statistical data about the user's course enrollments,
     * such as total enrollments, completed courses, or other relevant metrics.
     *
     * @param userId the unique identifier of the user whose statistics are to be retrieved
     * @return ResponseEntity containing StandardResponseOutDTO with a Map of statistics
     * where keys are statistic names (String) and values are counts (Long)
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<StandardResponseOutDTO<Map<String, Long>>> getUserEnrollments(@PathVariable final Long userId) {
        Map<String, Long> stats = userService.userStatistics(userId);
        StandardResponseOutDTO<Map<String, Long>> standardResponseOutDTO = StandardResponseOutDTO
                .success(stats, "Fetched Users Enrolled");
        return ResponseEntity.ok(standardResponseOutDTO);
    }
}
