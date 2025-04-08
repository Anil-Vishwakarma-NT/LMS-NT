package com.nt.LMS.controller;


import com.nt.LMS.dto.Admindto;
import com.nt.LMS.dto.GroupDTO;
import com.nt.LMS.dto.ManagerDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.exceptions.GroupNotFoundException;
import com.nt.LMS.exceptions.InvalidGroupException;
import com.nt.LMS.exceptions.UserNotFoundException;
import com.nt.LMS.service.AdminService;
import com.nt.LMS.service.GroupService;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/manager")
public class ManagerController {


    @Autowired
    private AdminService adminService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/manageremp")
    public ResponseEntity<List<Admindto>> getManagerEmp(@RequestBody Admindto admindto){
        List<User> employees = adminService.getManagerEmp(admindto.getUserId());
        List<Admindto> admindtos = employees.stream()
                .map(Admindto::new)  // Using the constructor to convert User to Admindto
                .collect(Collectors.toList());
        return ResponseEntity.ok(admindtos);
    }








}
