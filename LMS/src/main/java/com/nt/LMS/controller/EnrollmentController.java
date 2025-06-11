package com.nt.LMS.controller;

import com.nt.LMS.dto.inDTO.EnrollmentInDTO;
import com.nt.LMS.dto.outDTO.*;
import com.nt.LMS.service.EnrollmentService;
import com.nt.LMS.service.UserBundleEnrollmentService;
import com.nt.LMS.service.UserCourseEnrollmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing enrollments in the Learning Management System.
 * Provides endpoints for user enrollment operations, statistics, and enrollment data retrieval.
 */
@Slf4j
@RestController
@RequestMapping("/api/enrollment")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserCourseEnrollmentService userCourseEnrollmentService;
    private final UserBundleEnrollmentService userBundleEnrollmentService;

    /**
     * Constructor-based dependency injection for better testability.
     *
     * @param enrollmentService service for general enrollment operations
     * @param userCourseEnrollmentService service for user-course enrollment operations
     * @param userBundleEnrollmentService service for user-bundle enrollment operations
     */
    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService,
                                UserCourseEnrollmentService userCourseEnrollmentService,
                                UserBundleEnrollmentService userBundleEnrollmentService) {
        this.enrollmentService = enrollmentService;
        this.userCourseEnrollmentService = userCourseEnrollmentService;
        this.userBundleEnrollmentService = userBundleEnrollmentService;
    }

    /**
     * Enrolls a user in a course or bundle.
     *
     * @param enrollmentInDTO DTO containing enrollment details
     * @return ResponseEntity with enrollment confirmation message
     */
    @PostMapping("/enroll")
    public ResponseEntity<StandardResponseOutDTO<String>> enroll(
            @Valid @RequestBody EnrollmentInDTO enrollmentInDTO) {

        log.info("Received enrollment request for user ID: {}", enrollmentInDTO.getUserId());

        String response = enrollmentService.enrollUser(enrollmentInDTO);
        StandardResponseOutDTO<String> standardResponse = StandardResponseOutDTO
                .success(response, "User enrolled successfully");

        log.info("User enrollment completed successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(standardResponse);
    }

    /**
     * Retrieves the total count of enrollments in the system.
     *
     * @return ResponseEntity containing the total enrollment count
     */
    @GetMapping("/count")
    public ResponseEntity<StandardResponseOutDTO<Long>> getTotalEnrollmentCount() {
        log.info("Fetching total enrollments count");

        long count = enrollmentService.countEnrollments();
        StandardResponseOutDTO<Long> response = StandardResponseOutDTO
                .success(count, "Total enrollment count retrieved successfully");

        log.info("Total enrollments count retrieved: {}", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves comprehensive enrollment statistics for dashboard.
     *
     * @return ResponseEntity containing enrollment dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<StandardResponseOutDTO<EnrollmentDashBoardStatsOutDTO>> getEnrollmentStats() {
        log.info("Fetching enrollment statistics for dashboard");

        EnrollmentDashBoardStatsOutDTO stats = enrollmentService.getEnrollmentStats();
        StandardResponseOutDTO<EnrollmentDashBoardStatsOutDTO> response = StandardResponseOutDTO
                .success(stats, "Enrollment statistics retrieved successfully");

        log.info("Enrollment statistics retrieved successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all user enrollments filtered by course.
     *
     * @return ResponseEntity containing list of user-course enrollments
     */
    @GetMapping("/user-course")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>>> getUsersEnrolledFilterByCourse() {
        log.info("Fetching user enrollments filtered by course");

        List<UserCourseEnrollmentOutDTO> userCourseEnrollments = userCourseEnrollmentService.getUserEnrollmentsByCourse();
        StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>> response = StandardResponseOutDTO
                .success(userCourseEnrollments, "User course enrollments retrieved successfully");

        log.info("Retrieved {} user course enrollments", userCourseEnrollments.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all user enrollments filtered by bundle.
     *
     * @return ResponseEntity containing list of user-bundle enrollments
     */
    @GetMapping("/user-bundle")
    public ResponseEntity<StandardResponseOutDTO<List<UserBundleEnrollmentOutDTO>>> getUsersEnrolledFilterByBundle() {
        log.info("Fetching user enrollments filtered by bundle");

        List<UserBundleEnrollmentOutDTO> userBundleEnrollments = userBundleEnrollmentService.getUserEnrollmentsByBundle();
        StandardResponseOutDTO<List<UserBundleEnrollmentOutDTO>> response = StandardResponseOutDTO
                .success(userBundleEnrollments, "User bundle enrollments retrieved successfully");

        log.info("Retrieved {} user bundle enrollments", userBundleEnrollments.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all enrollments filtered by user.
     *
     * @return ResponseEntity containing list of user enrollments
     */
    @GetMapping("/user-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserEnrollmentsOutDTO>>> getUserEnrollmentsFilterByUser() {
        log.info("Fetching enrollments filtered by user");

        List<UserEnrollmentsOutDTO> userEnrollments = enrollmentService.getEnrollmentsForUser();
        StandardResponseOutDTO<List<UserEnrollmentsOutDTO>> response = StandardResponseOutDTO
                .success(userEnrollments, "User enrollments retrieved successfully");

        log.info("Retrieved {} user enrollments", userEnrollments.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all courses enrolled by a specific user.
     *
     * @param userId ID of the user whose enrolled courses are to be retrieved
     * @return ResponseEntity containing list of user's enrolled course details
     */
    @GetMapping("/user/{userId}/enrolled-courses")
    public ResponseEntity<StandardResponseOutDTO<List<UserEnrollDetailsOutDTO>>> getUserEnrolledCourses(
            @PathVariable Long userId) {

        log.info("Fetching enrolled courses for user ID: {}", userId);

        List<UserEnrollDetailsOutDTO> enrollments = userCourseEnrollmentService.getUserEnrolledCourses(userId);
        StandardResponseOutDTO<List<UserEnrollDetailsOutDTO>> response = StandardResponseOutDTO
                .success(enrollments, "User enrolled courses retrieved successfully");

        log.info("Retrieved {} enrolled courses for user ID: {}", enrollments.size(), userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the enrollment service.
     *
     * @return ResponseEntity indicating service health
     */
    @GetMapping("/health")
    public ResponseEntity<StandardResponseOutDTO<String>> healthCheck() {
        log.debug("Health check requested for enrollment service");

        StandardResponseOutDTO<String> response = StandardResponseOutDTO
                .success("UP", "Enrollment Service is running");

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves enrollment count for a specific user.
     *
     * @param userId ID of the user
     * @return ResponseEntity containing the user's enrollment count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<StandardResponseOutDTO<Integer>> getUserEnrollmentCount(
            @PathVariable Long userId) {

        log.info("Fetching enrollment count for user ID: {}", userId);

        List<UserEnrollDetailsOutDTO> enrollments = userCourseEnrollmentService.getUserEnrolledCourses(userId);
        int count = enrollments.size();

        StandardResponseOutDTO<Integer> response = StandardResponseOutDTO
                .success(count, "User enrollment count retrieved successfully");

        log.info("User ID {} has {} enrollments", userId, count);
        return ResponseEntity.ok(response);
    }
}