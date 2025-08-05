package com.nt.user_service_lms.controller;

import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.AdminDashboardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.dto.outDTO.UsersDetailsViewDTO;
import com.nt.user_service_lms.service.AdminService;
import com.nt.user_service_lms.service.GroupService;
import com.nt.user_service_lms.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for administrative operations in the LMS user service.
 * Provides endpoints for user management, role assignment, and employee operations.
 * All endpoints require admin role authorization.
 */
@Slf4j
@RestController
@RequestMapping("/api/service-api/admin")
public class AdminController {

    /**
     * Service for user-related operations.
     */
    @Autowired
    private UserService userService;

    /**
     * Service for admin-specific operations.
     */
    @Autowired
    private AdminService adminService;

    /**
     * Service for group-related operations.
     */
    @Autowired
    private GroupService groupService;

    /**
     * Registers a new user in the system.
     * Creates a new user account with the provided registration details.
     *
     * @param registerDto the user registration information including email, password, and personal details
     * @return ResponseEntity containing the success response with HTTP 201 status
     */
    @PostMapping("/register")
    public ResponseEntity<StandardResponseOutDTO> register(@Valid @RequestBody final RegisterDto registerDto) {
        log.info("Admin registration request received for: {}", registerDto.getEmail());

        StandardResponseOutDTO response = adminService.register(registerDto);
        log.info("Admin registered successfully: {}", registerDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletes a user by their unique identifier.
     * Removes the user from the system permanently.
     *
     * @param userId the unique identifier of the user to delete
     * @return ResponseEntity containing the success message with HTTP 200 status
     */
    @DeleteMapping("/remove-user/{userId}")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> deleteEmployee(@PathVariable final long userId) {
        log.info("Received request to delete user with ID: {}", userId);

        StandardResponseOutDTO response = adminService.employeeDeletion(userId);
        log.info("User with ID: {} deleted successfully", userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all active employees in the system.
     * Returns a list of all users who are currently active.
     *
     * @return ResponseEntity containing the list of active employees with HTTP 200 status,
     * or HTTP 204 if no active employees exist
     */
    @GetMapping("/active-employees")
    public ResponseEntity<StandardResponseOutDTO<List<UserOutDTO>>> getAllEmployees() {
        log.info("Fetching all employees");
        StandardResponseOutDTO<List<UserOutDTO>> response = adminService.getAllActiveUsers();
        log.info("Fetched {} employees", response.getData().size());
        if (response.getData().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all inactive employees in the system.
     * Returns a list of all users who are currently inactive or disabled.
     *
     * @return ResponseEntity containing the list of inactive employees with HTTP 200 status,
     * or HTTP 204 if no inactive employees exist
     */
    @GetMapping("/inactive-employees")
    public ResponseEntity<StandardResponseOutDTO<List<UserOutDTO>>> getAllInactiveEmployees() {
        log.info("Fetching all inactive employees");
        StandardResponseOutDTO<List<UserOutDTO>> response = adminService.getAllInactiveUsers();
        log.info("Fetched {} inactive employees", response.getData().size());
        if (response.getData().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all employees under a specific manager.
     * Returns a list of employees who report to the specified manager.
     *
     * @param userId the unique identifier of the manager
     * @return ResponseEntity containing the list of employees under the manager with HTTP 200 status,
     * or HTTP 204 if no employees are found under the manager
     */
    @GetMapping("/manager-employee/{userId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserOutDTO>>> getManagerEmployee(@PathVariable final long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        StandardResponseOutDTO<List<UserOutDTO>> response = adminService.getManagerEmployee(userId);
        if (response.getData().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        log.info("Fetched {} employees for manager with ID: {}", response.getData().size(), userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Changes a user's role in the system.
     * Updates the role assignment for the specified user.
     *
     * @param userDto contains the user ID and new role information
     * @return ResponseEntity containing the success message with HTTP 200 status
     */
    @PostMapping("/change-role")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> changeRole(@RequestBody @Valid final UserInDTO userDto) {
        log.info("Received request to change role for user with ID: {}", userDto.getUserId());
        StandardResponseOutDTO standardResponseOutDTO = adminService.changeUserRole(userDto.getUserId(), userDto.getRole());
        return new ResponseEntity<>(standardResponseOutDTO, HttpStatus.OK);
    }

    /**
     * Updates user details for a specific user.
     * Modifies the user's information based on the provided data.
     *
     * @param userId    the unique identifier of the user to update
     * @param userInDTO contains the updated user information
     * @return ResponseEntity containing the success message with HTTP 200 status
     */
    @PatchMapping("/update-user/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<MessageOutDTO> updateUser(@PathVariable final long userId, @RequestBody final UserInDTO userInDTO) {
        log.info("Received request to update user details");
        return new ResponseEntity<>(
                adminService.updateUserDetails(userInDTO, userId),
                HttpStatus.OK
        );
    }

    /**
     * Retrieves the total count of active users in the system.
     * Returns the number of users who are currently active.
     *
     * @return ResponseEntity containing the total user count with HTTP 200 status
     */
    @GetMapping("/count")
    public ResponseEntity<StandardResponseOutDTO<Long>> getTotalUserCount() {
        log.info("Fetching total user count");
        long count = userService.countActiveUsers();
        log.info("Total user count retrieved: {}", count);
        StandardResponseOutDTO<Long> standardResponseOutDTO = StandardResponseOutDTO
                .success(count, "Fetched User Count");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Retrieves recently registered users in the system.
     * Returns a list of users who have recently joined the system.
     *
     * @return ResponseEntity containing the list of recent users with HTTP 200 status
     */
    @GetMapping("/users/recent")
    public ResponseEntity<StandardResponseOutDTO<List<UsersDetailsViewDTO>>> getRecentUsers() {
        List<UsersDetailsViewDTO> usersDetailsViewDTOS = userService.getRecentUserDetails();
        StandardResponseOutDTO<List<UsersDetailsViewDTO>> standardResponseOutDTO = StandardResponseOutDTO
                .success(usersDetailsViewDTOS, "Fetched Recent Users");
        return ResponseEntity.ok(standardResponseOutDTO);
    }

    /**
     * Deletes registered users in the system.
     *
     * @param bundleId
     * @return ResponseEntity containing the message with HTTP 200 status
     */

    @DeleteMapping("/bundle")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> deleteBundle(@RequestParam final Long bundleId) {
        StandardResponseOutDTO<MessageOutDTO> response = adminService.deleteBundle(bundleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes registered courses from the bundle in the system.
     *
     * @param bundleId
     * @param courseId
     * @return ResponseEntity containing the message with HTTP 200 status
     */
    @DeleteMapping("/bundle/removecourse")
    public ResponseEntity<StandardResponseOutDTO<MessageOutDTO>> removeCourseFromBundle(@RequestParam final Long bundleId,
                                                                                        @RequestParam final Long courseId) {
        StandardResponseOutDTO<MessageOutDTO> message = adminService.removeCourseFromBundle(bundleId, courseId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    /**
     * Retrieves all courses enrolled by the currently authenticated user.
     * Uses custom ServicePrincipal authentication to identify the user.
     *
     * @param userId the Id of user
     * @return ResponseEntity containing StandardResponseOutDTO with list of UserCourseEnrollDetails
     */
    @GetMapping("/userCourses/{userId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserCourseEnrollDetails>>> getEnrolledCoursesByUserId(
            @RequestParam final long userId
    ) {
        List<UserCourseEnrollDetails> enrolledCourses = userService.getUserEnrolledCourses(userId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(enrolledCourses, "Fetched enrolled courses successfully"));
    }

    @GetMapping("/admin-dashboard-stats")
    public ResponseEntity<StandardResponseOutDTO<AdminDashboardStatsOutDTO>> getAdminDashboardStats() {
        StandardResponseOutDTO<AdminDashboardStatsOutDTO> standardResponseOutDTO = adminService.getAdminStats();
        return ResponseEntity.ok(standardResponseOutDTO);
    }

}
