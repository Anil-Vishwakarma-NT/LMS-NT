package com.nt.user_service_lms.controller;


import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dto.outDTO.CourseDeadlinesDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for user retrieval, authentication-based user details,
 * and user-specific data such as deadlines and enrolled courses.
 *
 * @author Generated
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/service-api/users")
public class UserController {

    /**
     * Service layer component for handling complex user-related business logic.
     * Encapsulates operations such as retrieving user deadlines and enrolled courses.
     */
    @Autowired
    private UserService userService;

    /**
     * Retrieves user information by email address.
     *
     * @param email the email address of the user to retrieve (case-insensitive)
     * @return ResponseEntity containing UserOutDTO with user details if found,
     * or NOT_FOUND status with null body if user doesn't exist
     */
    @GetMapping("/getUserId")
    public ResponseEntity<StandardResponseOutDTO<UserOutDTO>> getUserIdByEmail(@RequestParam final String email) {
        StandardResponseOutDTO<UserOutDTO> userdto = userService.getUserDetailsByEmail(email);
        return new ResponseEntity<>(userdto, HttpStatus.OK);
    }

    /**
     * Retrieves user details for the currently authenticated user.
     * Uses Spring Security context to determine the authenticated user's email.
     *
     * @return ResponseEntity containing UserOutDTO with authenticated user's details if found,
     * or NOT_FOUND status with null body if user doesn't exist
     */
    @GetMapping("/getUserDetails")
    public ResponseEntity<StandardResponseOutDTO<UserOutDTO>> getUserIdByAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(StandardResponseOutDTO.error("No authentication found"));
        }

        String username = authentication.getName();
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(StandardResponseOutDTO.error("No username found in authentication"));
        }

        StandardResponseOutDTO<UserOutDTO> userdto = userService.getUserDetailsByEmail(username);
        return new ResponseEntity<>(userdto, HttpStatus.OK);
    }

    /**
     * Retrieves user information by user ID.
     *
     * @param userId the unique identifier of the user to retrieve
     * @return ResponseEntity containing StandardResponseOutDTO with UserOutDTO if user exists,
     * or NOT_FOUND status with error message if user doesn't exist
     */
    @GetMapping("/{userId}")
    public ResponseEntity<StandardResponseOutDTO<UserOutDTO>> getUserNameById(@PathVariable final long userId) {
        StandardResponseOutDTO<UserOutDTO> userdto = userService.getUserDetailsByUserId(userId);
        if (userdto.getData() == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(StandardResponseOutDTO.error("User not found"));
        }
        return new ResponseEntity<>(userdto, HttpStatus.OK);
    }

    /**
     * Retrieves course deadlines for the currently authenticated user.
     * Uses custom ServicePrincipal authentication to identify the user.
     *
     * @return ResponseEntity containing StandardResponseOutDTO with list of CourseDeadlinesDTO
     * @throws UnauthorizedAccessException if authentication fails or principal is not ServicePrincipal
     */
    @GetMapping("/getDeadlines")
    public ResponseEntity<StandardResponseOutDTO<List<CourseDeadlinesDTO>>> getUserDeadlines() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Add null check first
        if (authentication == null || !(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new AccessDeniedException("Authentication failed");
        }

        String username = principal.getUserEmail();
        StandardResponseOutDTO<List<CourseDeadlinesDTO>> deadlines = userService.deadlineCourses(username);
        return new ResponseEntity<>(deadlines, HttpStatus.OK);
    }

    /**
     * Retrieves all courses enrolled by the currently authenticated user.
     * Uses custom ServicePrincipal authentication to identify the user.
     *
     * @return ResponseEntity containing StandardResponseOutDTO with list of UserCourseEnrollDetails
     * @throws UnauthorizedAccessException if authentication fails or principal is not ServicePrincipal
     */
    @GetMapping("/userCourses")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollDetails>>> getEnrolledCoursesByUserId() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(StandardResponseOutDTO.error("Authentication failed"));

        }
        String userId = principal.getUserId();
        List<UserCourseEnrollDetails> enrolledCourses = userService.getUserEnrolledCourses(Long.parseLong(userId));
        return ResponseEntity.ok(StandardResponseOutDTO.success(enrolledCourses, "Fetched enrolled courses successfully"));
    }

    /**
     * Retrieves user statistics like total enrollments and groups.
     *
     * @param userId
     * @return Map of String and long.
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<StandardResponseOutDTO<Map<String, Long>>> getUserEnrollments(@PathVariable final Long userId) {
        Map<String, Long> stats = userService.userStatistics(userId);
        StandardResponseOutDTO<Map<String, Long>> standardResponseOutDTO = StandardResponseOutDTO.success(stats,
                "Fetched Users Enrolled");
        return ResponseEntity.ok(standardResponseOutDTO);
    }


}
