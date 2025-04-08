package com.nt.LMS.controller;

import com.nt.LMS.dto.Admindto;
import com.nt.LMS.exceptions.ManagerNotFoundException;
import com.nt.LMS.entities.User;
import com.nt.LMS.service.GroupService;
import com.nt.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.service.AdminService;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

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
        MessageOutDto response = adminService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }
    @DeleteMapping("/deletion-user")
    public ResponseEntity<String> delEmp(@RequestBody Admindto admindto) {
        String role = admindto.getRole();
        try {
            if (role.equals("manager")) {
                adminService.deleteManager(admindto.getUserId());
                return ResponseEntity.ok("Deletion successful");
            } else if (role.equals("employee")) {
                adminService.deleteEmployee(admindto.getUserId());
                return ResponseEntity.ok("Deletion successful");
            } else {
                return ResponseEntity.badRequest().body("Invalid role provided.");
            }
        } catch (ManagerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }




    @GetMapping("/employees")
    public ResponseEntity<List<Admindto>> getAllEmployees(){
        try {List<User> employees = adminService.getAllUsers();
            if (employees.isEmpty()) {
                return ResponseEntity.noContent().build(); // HTTP 204 No Content
            }
            List<Admindto> adminDtos = employees.stream()
                    .map(Admindto::new)  // Using the constructor to convert User to Admindto
                    .collect(Collectors.toList());
            return ResponseEntity.ok(adminDtos);  // HTTP 200 OK
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @GetMapping("/manageremp")
    public ResponseEntity<List<Admindto>> getManagerEmp(@RequestParam("userId") long userId) {
        try {List<User> employees = adminService.getManagerEmp(userId);
                if (employees.isEmpty()) {
                   return ResponseEntity.noContent().build();  // HTTP 204 No Content
            }
                List<Admindto> admindtos = employees.stream()
                    .map(Admindto::new)  // Convert User to Admindto using constructor
                    .collect(Collectors.toList());
                return ResponseEntity.ok(admindtos);  // HTTP 200 OK
        } catch (Exception e) {
            // Log the exception here for better debugging (optional)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);  // HTTP 500 Internal Server Error
        }
    }





}