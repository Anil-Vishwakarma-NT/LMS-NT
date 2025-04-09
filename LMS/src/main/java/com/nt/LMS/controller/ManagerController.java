package com.nt.LMS.controller;


import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.service.AdminService;
import com.nt.LMS.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/manager")
public class ManagerController {


    @Autowired
    private AdminService adminService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/manageremp")
    public ResponseEntity<List<UserOutDTO>> getManagerEmp(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<User> employees = adminService.getEmps(username);
        List<UserOutDTO> response = employees.stream()
                .map(user -> new UserOutDTO(user.getUserId(), user.getUserName())) // Create UserOutDTO from User
                .collect(Collectors.toList()); // Collect the results into a List
        return ResponseEntity.ok(response);
    }








}
