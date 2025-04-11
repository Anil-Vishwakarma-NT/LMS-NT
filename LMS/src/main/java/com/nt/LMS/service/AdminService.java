package com.nt.LMS.service;

import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.exception.ResourceNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static com.nt.LMS.constants.UserConstants.*;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDTOConverter userDTOConverter;

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
                    return new RuntimeException("Role does not exist");  });
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


    public void employeeDeletion(long id) {
        log.info("Attempting to delete user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {log.error("User with ID {} not found", id);
                    return new ResourceNotFoundException(USER_NOT_FOUND);   });
        String role = user.getRole().getName();
        if (role.equalsIgnoreCase("employee")) {
            userRepository.deleteById(user.getUserId());
            log.info("Employee with ID {} deleted successfully", id);}
        else if (role.equalsIgnoreCase("manager")) {
            log.info("Changing manager for the deleted manager with ID: {}", id);
            List<User> subordinates = userRepository.findByManagerId(user.getUserId());
            for (User u : subordinates) {
                u.setManagerId(1L);      }
            userRepository.saveAll(subordinates);
            userRepository.deleteById(user.getUserId());
            log.info("Manager with ID {} deleted successfully", id);
        } else {
            log.error("Invalid role for user with ID {}: {}", id, role);
            throw new IllegalStateException(INVALID_USER_ROLE);
        }
    }


    public List<UserOutDTO> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<User> employees = userRepository.findAll();
            if (employees.isEmpty()) {
                log.warn("No employees found");
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            List<UserOutDTO> userDtos = new ArrayList<>();
            for (User user : employees) {
                Optional<User> userOpt = userRepository.findById(user.getManagerId());
                User manager = userOpt.orElseThrow(() -> {
                    log.error("Manager with ID {} not found", user.getManagerId());
                    return new ResourceNotFoundException(USER_NOT_FOUND);
                });
                String managerName = manager.getFirstName() + " " + manager.getLastName();
                UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName);
                userDtos.add(userDto);
            }
            log.info("Successfully fetched {} users", userDtos.size());
            return userDtos;
        } catch (Exception e) {
            log.error("Error fetching users", e);
            throw new RuntimeException(DATABASE_ERROR, e);
        }
    }


    public boolean changeUserRole(long userId, String newRoleName) {
        log.info("Attempting to change role for user with ID: {} to role: {}", userId, newRoleName);
        try {
            Role role = roleRepository.findByName(newRoleName);
            if (role == null) {
                log.error("Invalid role provided: {}", newRoleName);
                throw new IllegalArgumentException(INVALID_USER_ROLE);
            }
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.error("User with ID {} not found", userId);
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User with ID {} not found", userId);
                        return new ResourceNotFoundException(USER_NOT_FOUND);
                    });
            user.setRole(role);
            user.setUpdatedAt(new Date());
            userRepository.save(user);
            log.info("Successfully changed role for user with ID: {} to {}", userId, newRoleName);
            return true;
        } catch (Exception e) {
            log.error("Error changing role for user with ID: {}", userId, e);
            throw new RuntimeException(ERROR, e);
        }
    }


    public List<UserOutDTO> getManagerEmployee(long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        try {
            if (userRepository.findById(userId).isEmpty()) {
                log.error("Manager with ID {} not found", userId);
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            List<User> users = userRepository.findByManagerId(userId);
            List<UserOutDTO> response = new ArrayList<>();
            for (User user : users) {
                String managerName = ""; // Empty manager name as it's the same for all users under this manager
                UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName);
                response.add(userDto);
            }
            log.info("Successfully fetched {} employees for manager with ID: {}", response.size(), userId);
            return response;
        } catch (Exception e) {
            log.error("Error fetching employees for manager with ID: {}", userId, e);
            throw new RuntimeException(ERROR, e);
        }
    }


    public List<UserOutDTO> getEmployees(String username) {
        log.info("Fetching employees for manager with email: {}", username);
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.error("Manager with email {} not found", username);
                        return new ResourceNotFoundException(USER_NOT_FOUND);
                    });
            List<User> users = userRepository.findByManagerId(user.getUserId());
            List<UserOutDTO> response = new ArrayList<>();
            for (User u : users) {
                String managerName = ""; // Empty manager name as it's the same for all users under this manager
                UserOutDTO userDto = userDTOConverter.userToOutDto(u, managerName);
                response.add(userDto);
            }
            log.info("Successfully fetched {} employees for manager with email: {}", response.size(), username);
            return response;
        } catch (Exception e) {
            log.error("Error fetching employees for manager with email: {}", username, e);
            throw new RuntimeException(ERROR + e);
        }
    }
}
