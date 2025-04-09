package com.nt.LMS.service;


import com.nt.LMS.exception.UserNotFoundException;
import com.nt.LMS.exceptions.ManagerNotFoundException;
import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public MessageOutDto register(RegisterDto registerDto) {
        Optional<User> ExistingUser = userRepository.findByEmail(registerDto.getEmail());
        if (ExistingUser.isPresent()) {
            throw new ResourceConflictException(UserConstants.USER_ALREADY_EXISTS);
        }

        Role role = roleRepository.findById(registerDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role does not exist"));

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
                throw new ManagerNotFoundException("Manager with ID " + userId + " not found.");
            }
            List<User> users = userRepository.getEmployeesUnderManager(userId);
            if (users.isEmpty()) {
                // You can either return an empty list or throw an exception if you want
                // to signal no employees found.
                throw new ManagerNotFoundException("Manager with ID " + userId + " has no employees.");
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
            throw new ManagerNotFoundException("Manager with ID " + user.getUserId() + " has no employees.");
        }
        return users;

    }

}





