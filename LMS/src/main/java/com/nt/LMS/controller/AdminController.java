package com.nt.LMS.controller;

import com.nt.LMS.dto.*;
import com.nt.LMS.serviceImpl.GroupServiceImpl;
import com.nt.LMS.serviceImpl.UserServiceImpl;
import com.nt.LMS.serviceImpl.AdminServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Admin functionalities such as registering users,
 * deleting users, listing employees, assigning roles, etc.
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public final class AdminController {

    /**
     * Service for user operations.
     */
    @Autowired
    private UserServiceImpl userService;

    /**
     * Service for admin operations.
     */
    @Autowired
    private AdminServiceImpl adminService;

    /**
     * Service for group operations.
     */
    @Autowired
    private GroupServiceImpl groupService;

    /**
     * Registers new user.
     *
     * @param registerDto the user registration information
     * @return success message
     */
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<MessageOutDto> register(@Valid @RequestBody final RegisterDto registerDto) {
        log.info("Admin registration request received for: {}", registerDto.getEmail());
        MessageOutDto response = adminService.register(registerDto);
        log.info("Admin registered successfully: {}", registerDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the ID of the user to delete
     * @return success message
     */
    @DeleteMapping("/remove-user/{userId}")
    public ResponseEntity<MessageOutDto> deleteEmployee(@PathVariable final long userId) {
        log.info("Received request to delete user with ID: {}", userId);
        MessageOutDto msg = adminService.employeeDeletion(userId);
        log.info("User with ID: {} deleted successfully", userId);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    /**
     * Gets all employees.
     *
     * @return list of all employees
     */
    @GetMapping("/active-employees")
    public ResponseEntity<List<UserOutDTO>> getAllEmployees() {
        log.info("Fetching all employees");
        List<UserOutDTO> employees = adminService.getAllActiveUsers();
        log.info("Fetched {} employees", employees.size());
        if (employees.isEmpty()) {
            return new ResponseEntity<>(employees, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Gets all inactive employees.
     *
     * @return list of all employees
     */
    @GetMapping("/inactive-employees")
    public ResponseEntity<List<UserOutDTO>> getAllInactiveEmployees() {
        log.info("Fetching all inactive employees");
        List<UserOutDTO> employees = adminService.getAllInactiveUsers();
        log.info("Fetched {} inactive employees", employees.size());
        if (employees.isEmpty()) {
            return new ResponseEntity<>(employees, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Gets employees under a manager.
     *
     * @param userId manager's user ID
     * @return list of employees
     */
    @GetMapping("/manager-employee/{userId}")
    public ResponseEntity<List<UserOutDTO>> getManagerEmployee(@PathVariable final long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        List<UserOutDTO> employees = adminService.getManagerEmployee(userId);
        if (employees.isEmpty()) {
            return new ResponseEntity<>(employees, HttpStatus.NO_CONTENT);
        }
        log.info("Fetched {} employees for manager with ID: {}", employees.size(), userId);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Changes a user's role.
     *
     * @param userDto user ID and new role
     * @return success message
     */
    @PostMapping("/change-role")
    public ResponseEntity<MessageOutDto> changeRole(@RequestBody @Valid final UserInDTO userDto) {
        log.info("Received request to change role for user with ID: {}", userDto.getUserId());
        return new ResponseEntity<>(
                adminService.changeUserRole(userDto.getUserId(), userDto.getRole()),
                HttpStatus.OK
        );
    }

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<MessageOutDto> updateUser(@PathVariable final long userId , @RequestBody final RegisterDto registerDto){

        log.info("Received request to update user details");
        return new ResponseEntity<>(
                adminService.updateUserDetails(registerDto,userId),
                HttpStatus.OK
        );
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/count")
    public ResponseEntity<MessageOutDto> getTotalUserCount() {
        log.info("Fetching total user count");
        long count = userService.CountUsers();
        log.info("Total user count retrieved: {}", count);
        MessageOutDto message =new MessageOutDto();
        message.setMessage(""+count);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/users/recent")
    public ResponseEntity<List<UsersDetailsViewDTO>> getRecentUsers() {
        return ResponseEntity.ok(userService.getRecentUserDetails());
    }

}
