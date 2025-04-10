package com.nt.LMS.controller;

import com.nt.LMS.dto.*;
//import com.nt.LMS.exceptions.ManagerNotFoundException;
import com.nt.LMS.service.GroupService;
import com.nt.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nt.LMS.service.AdminService;
import com.nt.LMS.dto.RegisterDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

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
    public ResponseEntity<MessageOutDto> register(@RequestBody RegisterDto registerDto) {
        log.info("Admin registration request received for: {}", registerDto.getEmail());

        MessageOutDto response = adminService.register(registerDto);

        log.info("Admin registered successfully: {}", registerDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/deletion-user")
    public ResponseEntity<String> delEmp(@RequestBody UserInDTO userdto) {
            adminService.empDeletion(userdto.getUserId());
            return ResponseEntity.ok(USER_DELETION_MESSAGE);

    }

    @GetMapping("/get-employees")
    public ResponseEntity<List<UserOutDTO>> getAllEmployees() {
         return ResponseEntity.ok(adminService.getAllUsers());  // HTTP 200 OK
       }



    @GetMapping("/manager-emp")
    public ResponseEntity<List<UserOutDTO>> getManagerEmp(@RequestBody UserInDTO userdto) {
        return ResponseEntity.ok(adminService.getManEmp(userdto.getUserId()));  // HTTP 200 OK
    }


    @PostMapping("/change-role")
    public ResponseEntity<String> changeManager( @RequestBody UserInDTO userdto ){
            boolean deleted = adminService.changeRole(userdto.getUserId(), userdto.getRole());
            return ResponseEntity.ok("User role changed");
    }




}