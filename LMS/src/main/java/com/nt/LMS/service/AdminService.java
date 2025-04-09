package com.nt.LMS.service;


import com.nt.LMS.exception.UserNotFoundException;
//import com.nt.LMS.exceptions.ManagerNotFoundException;
import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public MessageOutDto register(RegisterDto registerDto) {
        log.info("Attempting to register user with email: {}", registerDto.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(registerDto.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Registration failed - user with email {} already exists", registerDto.getEmail());
            throw new ResourceConflictException(UserConstants.USER_ALREADY_EXISTS);
        }

        Role role = roleRepository.findById(registerDto.getRoleId())
                .orElseThrow(() -> {
                    log.error("Role with ID {} not found", registerDto.getRoleId());
                    return new RuntimeException("Role does not exist");
                });

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUserName(registerDto.getUserName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(role);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        log.info("User registered successfully with email: {}", registerDto.getEmail());
        return new MessageOutDto(UserConstants.USER_REGISTRATION_SUCCESS);
    }


    public void empDeletion(long id) {
        Optional<User> userOpt = userRepository.findById(id);

        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        String role = user.getRole().getName();
        if (role.equalsIgnoreCase("employee")) {
            userRepository.deleteById(user.getUserId());
        } else if (role.equalsIgnoreCase("manager")) {
            userRepository.updateManager(1L, user.getUserId()); // Update manager to a default manager or a specific one
            userRepository.deleteById(user.getUserId());
        } else {
            throw new IllegalStateException("Unexpected role: " + role);
        }
    }


    public List<User> getAllUsers() {
        try {
            List<User> employees = userRepository.getEmployees();
            if (employees.isEmpty()) {
                throw new UserNotFoundException("No users found in the database");
            }
            return employees;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching users from the database", e);
        }
    }
//
//    public User getUserDetail(long userId){
//        Optional<User> user = userRepository.findById(userId);
//
//    }

    public boolean changeRole(long userId, String role) {
        // Check if the user exists
        Role r = roleRepository.findByRoleName(role);
        Optional<User> userOpt = userRepository.findById(userId);

        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User with ID " + " not found"));

        int rowsAffected = userRepository.updateRole(r.getRoleId(), userId);
        return rowsAffected > 0;  // Return true if at least one row was updated
    }


    public List<User> getManEmp(long userId) {
        try {
            Optional<User> managerOpt = userRepository.findById(userId);
            if (managerOpt.isEmpty()) {
                // If manager doesn't exist, throw a custom exception
                throw new RuntimeException("Manager with ID " + userId + " not found.");
            }
            List<User> users = userRepository.getEmployeesUnderManager(userId);
            if (users.isEmpty()) {
                // You can either return an empty list or throw an exception if you want
                // to signal no employees found.
                throw new RuntimeException("Manager with ID " + userId + " has no employees.");
            }
            return users;
        } catch (Exception e) {
            // Handle unexpected exceptions (e.g., database errors)
            throw new RuntimeException("An error occurred while fetching employees for manager ID " + userId, e);
        }
    }

    public List<User> getEmps(String username) {
        Optional<User> userOpt = userRepository.findByEmail(username);

        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User with ID " + " not found"));
        List<User> users = userRepository.getEmployeesUnderManager(user.getUserId());
        if (users.isEmpty()) {
            // You can either return an empty list or throw an exception if you want
            // to signal no employees found.
            throw new RuntimeException("Manager with ID " + user.getUserId() + " has no employees.");
        }
        return users;

    }


}





