package com.nt.LMS.controller;

import com.nt.LMS.dto.*;
import com.nt.LMS.service.GroupService;
import com.nt.LMS.service.UserService;
import com.nt.LMS.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nt.LMS.constants.UserConstants.UPDATED;
import static com.nt.LMS.constants.UserConstants.USER_DELETION_MESSAGE;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private GroupService groupService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<MessageOutDto> register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("Admin registration request received for: {}", registerDto.getEmail());
        MessageOutDto response = adminService.register(registerDto);
        log.info("Admin registered successfully: {}", registerDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable long userId) {
        log.info("Received request to delete user with ID: {}", userId);
        adminService.employeeDeletion(userId);
        log.info("User with ID: {} deleted successfully", userId);
        return ResponseEntity.ok(USER_DELETION_MESSAGE);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserOutDTO>> getAllEmployees() {
        log.info("Fetching all employees");
        List<UserOutDTO> employees = adminService.getAllUsers();
        log.info("Fetched {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/manager-employee")
    public ResponseEntity<List<UserOutDTO>> getManagerEmployee(@RequestBody UserInDTO userdto) {
        log.info("Fetching employees for manager with ID: {}", userdto.getUserId());
        List<UserOutDTO> employees = adminService.getManagerEmployee(userdto.getUserId());
        log.info("Fetched {} employees for manager with ID: {}", employees.size(), userdto.getUserId());
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/change-role")
    public ResponseEntity<String> changeRole(@RequestBody UserInDTO userdto) {
        log.info("Received request to change role for user with ID: {}", userdto.getUserId());
        boolean roleChanged = adminService.changeUserRole(userdto.getUserId(), userdto.getRole());
        if (roleChanged) {
            log.info("Role changed successfully for user with ID: {}", userdto.getUserId());
            return ResponseEntity.ok(UPDATED);
        } else {
            log.warn("Failed to change role for user with ID: {}", userdto.getUserId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change role");
        }
    }
}
