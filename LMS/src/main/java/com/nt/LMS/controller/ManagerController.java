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

    @GetMapping("/manager-employees")
    public ResponseEntity<List<UserOutDTO>> getManagerEmp(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println(email + ".................................................");
        return ResponseEntity.ok(adminService.getEmps(email));
    }








}
