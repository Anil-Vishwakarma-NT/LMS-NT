package com.nt.user_service_lms.controllerTest;


import com.nt.user_service_lms.config.ServicePrincipal;
import com.nt.user_service_lms.dtoTest.outDTO.CourseDeadlinesDTO;
import com.nt.user_service_lms.dtoTest.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dtoTest.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dtoTest.outDTO.UserOutDTO;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.UnauthorizedAccessException;
import com.nt.user_service_lms.repository.UserRepository;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Repository for performing database operations on User entities.
     * Provides methods for finding users by various criteria such as email and ID.
     */
    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<UserOutDTO> getUserIdByEmail(@RequestParam final String email) {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if (user.isPresent()) {
            UserOutDTO userOutDTO = new UserOutDTO();
            userOutDTO.setUserId(user.get().getUserId());
            userOutDTO.setFirstName(user.get().getFirstName());
            userOutDTO.setLastName(user.get().getLastName());

            return ResponseEntity.ok(userOutDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves user details for the currently authenticated user.
     * Uses Spring Security context to determine the authenticated user's email.
     *
     * @return ResponseEntity containing UserOutDTO with authenticated user's details if found,
     * or NOT_FOUND status with null body if user doesn't exist
     */
    @GetMapping("/getUserDetails")
    public ResponseEntity<UserOutDTO> getUserIdByAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByEmailIgnoreCase(username);

        if (user.isPresent()) {
            UserOutDTO userOutDTO = new UserOutDTO();
            userOutDTO.setUserId(user.get().getUserId());
            userOutDTO.setFirstName(user.get().getFirstName());
            userOutDTO.setLastName(user.get().getLastName());

            return ResponseEntity.ok(userOutDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(StandardResponseOutDTO.error("User not found"));
        }

        User user = userOpt.get();
        UserOutDTO userOutDTO = new UserOutDTO();
        userOutDTO.setUserId(user.getUserId());
        userOutDTO.setFirstName(user.getFirstName());
        userOutDTO.setLastName(user.getLastName());

        return ResponseEntity.ok(
                StandardResponseOutDTO.success(userOutDTO, "Fetched user info")
        );
    }

    /**
     * Retrieves course deadlines for the currently authenticated user.
     * Uses custom ServicePrincipal authentication to identify the user.
     *
     * @return ResponseEntity containing StandardResponseOutDTO with list of CourseDeadlinesDTO
     * @throws UnauthorizedAccessException if authentication fails or principal is not ServicePrincipal
     */
    @GetMapping("/getDeadlines")
    public ResponseEntity<StandardResponseOutDTO<List<CourseDeadlinesDTO>>> getUserDeadlines() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
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
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollDetails>>> getEnrolledCoursesByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof ServicePrincipal principal)) {
            throw new UnauthorizedAccessException("Authentication failed");
        }
        String userId = principal.getUserId();
        List<UserCourseEnrollDetails> enrolledCourses = userService.getUserEnrolledCourses(Long.parseLong(userId));
        return ResponseEntity.ok(StandardResponseOutDTO.success(enrolledCourses, "Fetched enrolled courses successfully"));
    }

    @GetMapping("/{userId}/statistics")
    public ResponseEntity<StandardResponseOutDTO<Map<String, Long>>> getUserEnrollments(@PathVariable Long userId) {
        Map<String, Long> stats = userService.userStatistics(userId);
        StandardResponseOutDTO<Map<String, Long>> standardResponseOutDTO = StandardResponseOutDTO.success(stats, "Fetched Users Enrolled");
        return ResponseEntity.ok(standardResponseOutDTO);
    }


}