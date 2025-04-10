package com.nt.LMS.controller;

import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.service.AdminService;
import com.nt.LMS.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/manager-employees")
    public ResponseEntity<List<UserOutDTO>> getManagerEmp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("Manager with email {} is requesting the list of their employees", email);

        List<UserOutDTO> employees = adminService.getEmployees(email);

        log.info("Fetched {} employees for manager with email {}", employees.size(), email);

        return ResponseEntity.ok(employees);
    }
}
