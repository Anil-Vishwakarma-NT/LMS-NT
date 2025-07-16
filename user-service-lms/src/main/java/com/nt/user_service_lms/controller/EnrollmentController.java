package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.user_service_lms.dto.outDTO.EnrollmentDashBoardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.EnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserBundleEnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserEnrollmentsOutDTO;
import com.nt.user_service_lms.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing enrollments in the Learning Management System.
 *
 * <p>This controller provides comprehensive endpoints for user enrollment operations,
 * including enrollment creation, statistics retrieval, and various enrollment data
 * queries. It handles both individual course enrollments and bundle enrollments,
 * supporting the complete enrollment lifecycle in the LMS.</p>
 *
 * <p>All endpoints return standardized responses wrapped in {@link StandardResponseOutDTO}
 * for consistent API response structure across the application.</p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/service-api/enrollment")
public class EnrollmentController {

    /**
     * Service layer dependency for handling enrollment business logic.
     *
     * <p>This service is responsible for all enrollment-related operations including
     * enrollment creation, data retrieval, and statistics computation.</p>
     */
    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * Creates new enrollments for users in the Learning Management System.
     *
     * <p>This endpoint processes enrollment requests and creates enrollments
     * based on the provided enrollment data. It supports both individual course
     * enrollments and bundle enrollments.</p>
     *
     * @param enrollmentRequestInDTO the enrollment request data containing user and course/bundle information
     * @return ResponseEntity containing a standardized response with the list of created enrollments
     * @since 1.0
     */
    @PostMapping("/enroll")
    public ResponseEntity<StandardResponseOutDTO<List<EnrollmentOutDTO>>> enroll(
            @Valid @RequestBody final EnrollmentRequestInDTO enrollmentRequestInDTO) {

        log.info("Processing enrollment request for user: {}", enrollmentRequestInDTO.getUserIds());

        try {
            List<EnrollmentOutDTO> enrollments = enrollmentService.enroll(enrollmentRequestInDTO);

            log.info("Successfully created {} enrollments for user: {}",
                    enrollments.size(), enrollmentRequestInDTO.getUserIds());

            StandardResponseOutDTO<List<EnrollmentOutDTO>> standardResponseOutDTO =
                    StandardResponseOutDTO.success(enrollments, "Enrollment Successful");

            return ResponseEntity.ok(standardResponseOutDTO);

        } catch (Exception e) {
            log.error("Failed to process enrollment request for user: {}",
                    enrollmentRequestInDTO.getUserIds(), e);
            throw e;
        }
    }

    /**
     * Retrieves comprehensive enrollment statistics for the dashboard.
     *
     * <p>This endpoint provides aggregated enrollment data including total enrollments,
     * active enrollments, completion rates, and other key metrics used for
     * administrative dashboard displays.</p>
     *
     * @return ResponseEntity containing standardized response with enrollment statistics
     * @since 1.0
     */
    @GetMapping("/statistics")
    public ResponseEntity<StandardResponseOutDTO<EnrollmentDashBoardStatsOutDTO>> getEnrollmentStatistics() {

        log.info("Fetching enrollment statistics for dashboard");

        try {
            EnrollmentDashBoardStatsOutDTO stats = enrollmentService.getEnrollmentStats();

            log.info("Successfully retrieved enrollment statistics");

            StandardResponseOutDTO<EnrollmentDashBoardStatsOutDTO> standardResponseOutDTO =
                    StandardResponseOutDTO.success(stats, "Fetched Enrollment Statistics");

            return ResponseEntity.ok(standardResponseOutDTO);

        } catch (Exception e) {
            log.error("Failed to fetch enrollment statistics", e);
            throw e;
        }
    }

    /**
     * Retrieves all enrollments for a specific user by their user ID.
     *
     * <p>This endpoint returns comprehensive enrollment information for a single user,
     * including both course and bundle enrollments, along with their current status
     * and progress information.</p>
     *
     * @param userId the unique identifier of the user whose enrollments are to be retrieved
     * @return ResponseEntity containing standardized response with user's enrollment data
     * @since 1.0
     */
    @GetMapping("/user-enrollments/{id}")
    public ResponseEntity<StandardResponseOutDTO<UserEnrollmentsOutDTO>> getUserEnrollmentsByUserId(
            @PathVariable("id") final Long userId) {

        log.info("Fetching enrollments for user ID: {}", userId);

        try {
            UserEnrollmentsOutDTO userEnrollmentsOutDTO = enrollmentService.getUserEnrollmentsByUserID(userId);

            log.info("Successfully retrieved enrollments for user ID: {}", userId);

            StandardResponseOutDTO<UserEnrollmentsOutDTO> standardResponseOutDTO =
                    StandardResponseOutDTO.success(userEnrollmentsOutDTO, "User Enrollment Fetched");

            return ResponseEntity.ok(standardResponseOutDTO);

        } catch (Exception e) {
            log.error("Failed to fetch enrollments for user ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Retrieves enrollment information for all users in the system.
     *
     * <p>This endpoint returns a comprehensive list of all user enrollments,
     * typically used for administrative purposes and reporting. The response
     * includes enrollment data for all users with their respective course and
     * bundle enrollments.</p>
     *
     * @return ResponseEntity containing standardized response with all users' enrollment data
     * @since 1.0
     */
    @GetMapping("/user-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserEnrollmentsOutDTO>>> getUserEnrollments() {

        log.info("Fetching enrollments for all users");

        try {
            List<UserEnrollmentsOutDTO> userEnrollmentsOutDTOs = enrollmentService.getAllUsersEnrollments();

            log.info("Successfully retrieved enrollments for {} users", userEnrollmentsOutDTOs.size());

            StandardResponseOutDTO<List<UserEnrollmentsOutDTO>> standardResponseOutDTO =
                    StandardResponseOutDTO.success(userEnrollmentsOutDTOs, "User Enrollment Fetched");

            return ResponseEntity.ok(standardResponseOutDTO);

        } catch (Exception e) {
            log.error("Failed to fetch enrollments for all users", e);
            throw e;
        }
    }

    /**
     * Retrieves individual course enrollment details for all users.
     *
     * <p>This endpoint specifically returns course-only enrollments, excluding
     * bundle enrollments. It provides detailed information about each user's
     * individual course enrollments including progress, completion status,
     * and enrollment dates.</p>
     *
     * @return ResponseEntity containing standardized response with individual course enrollment data
     * @since 1.0
     */
    @GetMapping("/user-course-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>>> getUserCourseEnrollments() {

        log.info("Fetching individual course enrollments for all users");

        try {
            List<UserCourseEnrollmentOutDTO> userCourseEnrollmentOutDTOS =
                    enrollmentService.getIndividualCourseEnrollments();

            log.info("Successfully retrieved {} individual course enrollments",
                    userCourseEnrollmentOutDTOS.size());

            StandardResponseOutDTO<List<UserCourseEnrollmentOutDTO>> standardResponseOutDTO =
                    StandardResponseOutDTO.success(userCourseEnrollmentOutDTOS, "Fetched Course Enrollments for User");

            return ResponseEntity.ok(standardResponseOutDTO);

        } catch (Exception e) {
            log.error("Failed to fetch individual course enrollments", e);
            throw e;
        }
    }

    /**
     * Retrieves individual bundle enrollment details for all users.
     *
     * <p>This endpoint specifically returns bundle-only enrollments, excluding
     * individual course enrollments. It provides detailed information about
     * each user's bundle enrollments including the courses within bundles,
     * progress tracking, and completion status.</p>
     *
     * @return ResponseEntity containing standardized response with individual bundle enrollment data
     * @since 1.0
     */
    @GetMapping("/user-bundle-enrollments")
    public ResponseEntity<StandardResponseOutDTO<List<UserBundleEnrollmentOutDTO>>> getUserBundleEnrollments() {

        log.info("Fetching individual bundle enrollments for all users");

        try {
            List<UserBundleEnrollmentOutDTO> userBundleEnrollmentOutDTOS =
                    enrollmentService.getIndividualBundleEnrollments();

            log.info("Successfully retrieved {} individual bundle enrollments",
                    userBundleEnrollmentOutDTOS.size());

            StandardResponseOutDTO<List<UserBundleEnrollmentOutDTO>> standardResponseOutDTO =
                    StandardResponseOutDTO.success(userBundleEnrollmentOutDTOS, "Fetched Bundle Enrollments for User");

            return ResponseEntity.ok(standardResponseOutDTO);

        } catch (Exception e) {
            log.error("Failed to fetch individual bundle enrollments", e);
            throw e;
        }
    }

    /**
     * Retrieves detailed enrollment information for all courses enrolled by a specific user.
     *
     * <p>This endpoint provides comprehensive course enrollment details for a specific user,
     * including course information, enrollment dates, progress tracking, completion status,
     * and any additional metadata associated with the user's course enrollments.</p>
     *
     * @param userId the unique identifier of the user whose enrolled courses are to be retrieved
     * @return ResponseEntity containing standardized response with detailed course enrollment information
     * @since 1.0
     */
    @GetMapping("/userCourses/{userId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollDetails>>> getEnrolledCoursesByUserId(
            @PathVariable final Long userId) {

        log.info("Fetching enrolled courses for user ID: {}", userId);

        try {
            List<UserCourseEnrollDetails> enrolledCourses = enrollmentService.getUserEnrolledCourses(userId);

            log.info("Successfully retrieved {} enrolled courses for user ID: {}",
                    enrolledCourses.size(), userId);

            return ResponseEntity.ok(StandardResponseOutDTO.success(enrolledCourses,
                    "Fetched enrolled courses successfully"));

        } catch (Exception e) {
            log.error("Failed to fetch enrolled courses for user ID: {}", userId, e);
            throw e;
        }
    }
}