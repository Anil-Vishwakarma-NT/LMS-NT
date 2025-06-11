package com.nt.LMS.controller;

import com.nt.LMS.dto.StandardResponseOutDTO;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.service.serviceImpl.GroupServiceImpl;
import com.nt.LMS.service.serviceImpl.ManagerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing employee-related operations for managers.
 * Provides an endpoint for managers to view their list of employees.
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    /**
     * Service class to handle admin related operations.
     */
    @Autowired
    private ManagerServiceImpl managerService;

    /**
     * Service class to handle group related operations.
     */
    @Autowired
    private GroupServiceImpl groupService;

    /**
     * Endpoint to fetch the list of employees under the manager's supervision.
     *
     * @return ResponseEntity containing the list of employees if found, or an HTTP status
     * of NO_CONTENT if no employees are found.
     */
    @GetMapping("/manager-employees")
    public ResponseEntity<StandardResponseOutDTO<List<UserOutDTO>>> getManagerEmp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("Manager with email {} is requesting the list of their employees", email);
        StandardResponseOutDTO<List<UserOutDTO>> employees = managerService.getEmployees(email);

        if (employees.getData().isEmpty()) {
            return new ResponseEntity<>(employees, HttpStatus.NO_CONTENT);
        }

        log.info("Fetched {} employees for manager with email {}", employees.getData().size(), email);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
