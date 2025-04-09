package com.nt.LMS.controller;

import com.nt.LMS.dto.*;
import com.nt.LMS.exception.UserNotFoundException;
import com.nt.LMS.exceptions.ManagerNotFoundException;
import com.nt.LMS.entities.User;
import com.nt.LMS.service.GroupService;
import com.nt.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.nt.LMS.service.AdminService;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private GroupService groupService;


    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<MessageOutDto> register(@RequestBody RegisterDto registerDto) {
        log.info("Admin registration request received for: {}", registerDto.getEmail());

        MessageOutDto response = adminService.register(registerDto);

        log.info("Admin registered successfully: {}", registerDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    @DeleteMapping("/deletion-user")
    public ResponseEntity<String> delEmp(@RequestBody UserDTO admindto) {
        try {
            adminService.empDeletion(admindto.getUserId());
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/get-employees")
    public ResponseEntity<List<UserOutDTO>> getAllEmployees(){
        try {List<User> employees = adminService.getAllUsers();
            if (employees.isEmpty()) {
                return ResponseEntity.noContent().build(); // HTTP 204 No Content
            }
            List<UserOutDTO> response = employees.stream()
                    .map(user -> new UserOutDTO(user.getUserId(), user.getUserName())) // Create UserOutDTO from User
                    .collect(Collectors.toList()); // Collect the results into a List
            return ResponseEntity.ok(response);  // HTTP 200 OK
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @GetMapping("/manageremp")
    public ResponseEntity<List<UserOutDTO>> getManagerEmp(@RequestBody UserDTO groupdto) {
        try {
            List<User> employees = adminService.getManEmp(groupdto.getUserId());
                if (employees.isEmpty()) {
                   return ResponseEntity.noContent().build();  // HTTP 204 No Content
            }
            List<UserOutDTO> response = employees.stream()
                    .map(user -> new UserOutDTO(user.getUserId(), user.getUserName())) // Create UserOutDTO from User
                    .collect(Collectors.toList()); // Collect the results into a List
            return ResponseEntity.ok(response);  // HTTP 200 OK
        } catch (Exception e) {
            // Log the exception here for better debugging (optional)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);  // HTTP 500 Internal Server Error
        }
    }


    @PostMapping("/change-manager")
    public ResponseEntity<String> changeManager( @RequestBody UserDTO userdto ){
        try{
            boolean deleted = adminService.changeRole(userdto.getUserId(), userdto.getChangeRole());
            return ResponseEntity.ok("User role changed");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }




}