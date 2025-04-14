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

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private GroupServiceImpl groupService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<MessageOutDto> register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("Admin registration request received for: {}", registerDto.getEmail());
        MessageOutDto response = adminService.register(registerDto);
        log.info("Admin registered successfully: {}", registerDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<MessageOutDto> deleteEmployee(@PathVariable long userId) {
        log.info("Received request to delete user with ID: {}", userId);
       MessageOutDto msg= adminService.employeeDeletion(userId);
        log.info("User with ID: {} deleted successfully", userId);
        return new ResponseEntity<>(msg,HttpStatus.OK);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserOutDTO>> getAllEmployees() {
        log.info("Fetching all employees");
        List<UserOutDTO> employees = adminService.getAllUsers();
        log.info("Fetched {} employees", employees.size());
        if(employees.isEmpty()){
            return new ResponseEntity<>(employees, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employees , HttpStatus.OK);
    }

    @GetMapping("/manager-employee/{userId}")
    public ResponseEntity<List<UserOutDTO>> getManagerEmployee(@PathVariable long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        List<UserOutDTO> employees = adminService.getManagerEmployee(userId);
        if(employees.isEmpty()){
            return new ResponseEntity<>(employees, HttpStatus.NO_CONTENT);
        }
        log.info("Fetched {} employees for manager with ID: {}", employees.size(), userId);
        return new ResponseEntity<>(employees , HttpStatus.OK);
    }

    @PostMapping("/change-role")
    public ResponseEntity<MessageOutDto> changeRole(@RequestBody @Valid UserInDTO userdto) {
        log.info("Received request to change role for user with ID: {}", userdto.getUserId());
        return new ResponseEntity<>(adminService.changeUserRole(userdto.getUserId(), userdto.getRole()),HttpStatus.OK);
    }
}
