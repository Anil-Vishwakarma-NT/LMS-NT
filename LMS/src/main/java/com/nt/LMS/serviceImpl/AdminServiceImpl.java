package com.nt.LMS.serviceImpl;

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
import com.nt.LMS.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Implementation of AdminService containing admin operations.
 */
@Slf4j
@Service
public final class AdminServiceImpl implements AdminService {

    /**
     * Repository for user operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for role operations.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Encoder for password encryption.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Converter for user DTOs.
     */
    @Autowired
    private UserDTOConverter userDTOConverter;

    /**
     * Registers a new user.
     *
     * @param registerDto the registration data
     * @return a message response
     */
    @Override
    public MessageOutDto register(final RegisterDto registerDto) {
        log.info("Attempting to register user with email: {}", registerDto.getEmail());

        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            log.warn("Registration failed - user with email {} already exists", registerDto.getEmail());
            throw new ResourceConflictException(UserConstants.USER_ALREADY_EXISTS);
        }

        if (userRepository.findByUserNameIgnoreCase(registerDto.getUserName()).isPresent()) {
            log.warn("Registration failed - username {} already exists", registerDto.getUserName());
            throw new ResourceConflictException(UserConstants.USERNAME_ALREADY_EXISTS);
        }

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUserName(registerDto.getUserName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoleId(registerDto.getRoleId());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        log.info("User registered successfully with email: {}", registerDto.getEmail());
        return new MessageOutDto(UserConstants.USER_REGISTRATION_SUCCESS);
    }

    /**
     * Deletes an employee or manager.
     *
     * @param id the user ID
     * @return a message response
     */
    @Override
    public MessageOutDto employeeDeletion(final long id) {
        log.info("Attempting to delete user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    return new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                });

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> {
                    log.error("Role with ID {} not found for user ID {}", user.getRoleId(), id);
                    return new IllegalStateException(UserConstants.INVALID_USER_ROLE);
                });

        String roleName = role.getName();

        if ("employee".equalsIgnoreCase(roleName)) {
            userRepository.deleteById(user.getUserId());
            log.info("Employee with ID {} deleted successfully", id);
        } else if ("manager".equalsIgnoreCase(roleName)) {
            log.info("Changing manager for the deleted manager with ID: {}", id);

            List<User> subordinates = userRepository.findByManagerId(user.getUserId());
            if (!subordinates.isEmpty()) {
                for (User u : subordinates) {
                    u.setManagerId(UserConstants.getAdminId());
                }
                userRepository.saveAll(subordinates);
            }

            userRepository.deleteById(user.getUserId());
            log.info("Manager with ID {} deleted successfully", id);
        } else {
            log.error("Invalid role for user with ID {}: {}", id, roleName);
            throw new IllegalStateException(UserConstants.INVALID_USER_ROLE);
        }
        return new MessageOutDto(UserConstants.USER_DELETION_MESSAGE);
    }

    /**
     * Fetches all users.
     *
     * @return a list of UserOutDTO
     */
    @Override
    public List<UserOutDTO> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<User> employees = userRepository.findAll();
            if (employees.isEmpty()) {
                log.warn("No employees found");
                return Collections.emptyList();
            }

            List<UserOutDTO> userDtos = new ArrayList<>();
            for (User user : employees) {
                User manager = userRepository.findById(user.getManagerId())
                        .orElseThrow(() -> {
                            log.error("Manager with ID {} not found", user.getManagerId());
                            throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                        });
                String managerName = manager.getFirstName() + " " + manager.getLastName();
                Role role = roleRepository.findById(user.getRoleId()).orElseThrow(
                        () -> {
                            log.error("Role with ID {} not found", user.getManagerId());
                            throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                        }
                );

                UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName);
                userDtos.add(userDto);
            }

            log.info("Successfully fetched {} users", userDtos.size());
            return userDtos;
        } catch (Exception e) {
            log.error("Error fetching users", e);
            throw new RuntimeException(UserConstants.DATABASE_ERROR, e);
        }
    }

    /**
     * Changes a user's role.
     *
     * @param userId      the user ID
     * @param newRoleName the new role name
     * @return a message response
     */
    @Override
    public MessageOutDto changeUserRole(final long userId, final String newRoleName) {
        log.info("Attempting to change role for user with ID: {} to role: {}", userId, newRoleName);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User with ID {} not found", userId);
                        throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                    });

            Role role = roleRepository.findByName(newRoleName)
                    .orElseThrow(() -> {
                        log.error("Invalid role provided: {}", newRoleName);
                        throw new IllegalArgumentException(UserConstants.INVALID_USER_ROLE);
                    });

            user.setRoleId(role.getRoleId());
            user.setUpdatedAt(new Date());
            userRepository.save(user);
            log.info("Successfully changed role for user with ID: {} to {}", userId, newRoleName);
            return new MessageOutDto(UserConstants.UPDATED);
        } catch (Exception e) {
            log.error("Error changing role for user with ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }

    /**
     * Fetches employees under a manager.
     *
     * @param userId the manager's user ID
     * @return a list of UserOutDTO
     */
    @Override
    public List<UserOutDTO> getManagerEmployee(final long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        try {
            User manager = userRepository.findById(userId).orElseThrow(() -> {
                log.error("Manager with ID {} not found", userId);
                throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
            });

            String managerName = manager.getFirstName() + manager.getLastName();
            List<User> users = userRepository.findByManagerId(userId);
            if (users.isEmpty()) {
                log.warn("No employees found");
                return Collections.emptyList();
            }

            List<UserOutDTO> response = new ArrayList<>();
            for (User user : users) {
                UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName);
                response.add(userDto);
            }

            log.info("Successfully fetched {} employees for manager with ID: {}", response.size(), userId);
            return response;
        } catch (Exception e) {
            log.error("Error fetching employees for manager with ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }

    /**
     * Fetches employees under a manager by username.
     *
     * @param username the manager's email/username
     * @return a list of UserOutDTO
     */
    @Override
    public List<UserOutDTO> getEmployees(final String username) {
        log.info("Fetching employees for manager with email: {}", username);
        try {
            User manager = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.error("Manager with email {} not found", username);
                        throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                    });

            String managerName = manager.getFirstName() + manager.getLastName();
            List<User> users = userRepository.findByManagerId(manager.getUserId());
            if (users.isEmpty()) {
                log.warn("No employees found");
                return Collections.emptyList();
            }

            List<UserOutDTO> response = new ArrayList<>();
            for (User u : users) {
                UserOutDTO userDto = userDTOConverter.userToOutDto(u, managerName);
                response.add(userDto);
            }

            log.info("Successfully fetched {} employees for manager with email: {}", response.size(), username);
            return response;
        } catch (Exception e) {
            log.error("Error fetching employees for manager with email: {}", username, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }
}
