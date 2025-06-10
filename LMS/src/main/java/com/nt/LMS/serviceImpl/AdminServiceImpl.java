package com.nt.LMS.serviceImpl;

import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.StandardResponseOutDTO;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.exception.InvalidRequestException;
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

import static com.nt.LMS.constants.UserConstants.INVALID_REQUEST;
import static com.nt.LMS.constants.UserConstants.USER_UPDATED_SUCCESSFULLY;

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
    public StandardResponseOutDTO<MessageOutDto> register(final RegisterDto registerDto) {
        log.info("Attempting to register user with email: {}", registerDto.getEmail());

        if (userRepository.findByEmailIgnoreCase(registerDto.getEmail()).isPresent()) {
            log.warn("Registration failed - user with email {} already exists", registerDto.getEmail());
            throw new ResourceConflictException(UserConstants.USER_ALREADY_EXISTS);
        }

        if (userRepository.findByUserNameIgnoreCase(registerDto.getUserName()).isPresent()) {
            log.warn("Registration failed - username {} already exists", registerDto.getUserName());
            throw new ResourceConflictException(UserConstants.USERNAME_ALREADY_EXISTS);
        }

        if (!roleRepository.findById(registerDto.getRoleId()).isPresent() || registerDto.getRoleId() == 1) {
            log.warn("Registration failed - role with ID {} does not exist", registerDto.getRoleId());
            throw new ResourceNotFoundException(UserConstants.INVALID_ROLE + " : " + registerDto.getRoleId());
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
        MessageOutDto messageOutDto = new MessageOutDto(UserConstants.USER_REGISTRATION_SUCCESS);
        return StandardResponseOutDTO.success(messageOutDto,"User Registration Successfully");
    }

    /**
     * Deletes an employee or manager(soft delete only).
     *
     * @param id the user ID
     * @return a message response
     */
    @Override
    public StandardResponseOutDTO<MessageOutDto> employeeDeletion(final long id) {
        log.info("Attempting to delete user with ID: {}", id);
        if(id != UserConstants.getAdminId()) {
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
                user.setActive(false);
                userRepository.save(user);
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

                user.setActive(false);
                userRepository.save(user);
                log.info("Manager with ID {} deleted successfully", id);
            }
        else {
            log.error("Invalid role for user with ID {}: {}", id, roleName);
            throw new IllegalStateException(UserConstants.INVALID_USER_ROLE);
        }}
        else{
            throw new InvalidRequestException(INVALID_REQUEST);
        }
        MessageOutDto messageOutDto = new MessageOutDto(UserConstants.USER_DELETION_MESSAGE);
        return StandardResponseOutDTO.success(messageOutDto,null);
    }

    /**
     * Fetches all users.
     *
     * @return a list of UserOutDTO
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getAllActiveUsers() {
        log.info("Fetching all users");
        try {
            List<User> employees = userRepository.findAll();
//            System.out.println(employees);
            if (employees.isEmpty()) {
                log.warn("No employees found");
                return StandardResponseOutDTO.success(Collections.emptyList(),"No user found");
            }

            List<UserOutDTO> userDtos = new ArrayList<>();
            for (User user : employees) {
                if (user.isActive() && (user.getUserId() != UserConstants.getAdminId()) ) {
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

                    UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName,role.getName());
                    userDtos.add(userDto);
                }
            }

                log.info("Successfully fetched {} users", userDtos.size());
                return StandardResponseOutDTO.success(userDtos,"User fetched Successfully");
            } catch(Exception e){
                log.error("Error fetching users", e);
                throw new RuntimeException(UserConstants.DATABASE_ERROR, e);
            }
        }

    /**
     * Get all inactive users.
     *
     * @return list of UserOutDto
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getAllInactiveUsers(){
        log.info("Fetching all  inactive users");
        try {
            List<User> employees = userRepository.findAll();
            if (employees.isEmpty()) {
                log.warn("No employees found");
                return StandardResponseOutDTO.success(Collections.emptyList(),"User does not exist");
            }

            List<UserOutDTO> userDtos = new ArrayList<>();
            for (User user : employees) {
                if (!user.isActive() && (user.getUserId() != UserConstants.getAdminId()) ) {
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

                    UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName,role.getName());
                    userDtos.add(userDto);
                }
            }

            log.info("Successfully fetched {} inactive users", userDtos.size());
            return StandardResponseOutDTO.success(userDtos,"User fetched Successfully");
        } catch(Exception e){
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
    public StandardResponseOutDTO<MessageOutDto> changeUserRole(final long userId, final String newRoleName) {
        log.info("Attempting to change role for user with ID: {} to role: {}", userId, newRoleName);
        try {
            if(userId != UserConstants.getAdminId()){
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
            MessageOutDto messageOutDto =  new MessageOutDto(UserConstants.UPDATED);
            return StandardResponseOutDTO.success(messageOutDto,UserConstants.UPDATED);
            }
            else{
                throw new InvalidRequestException(INVALID_REQUEST);
            }
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
    public StandardResponseOutDTO<List<UserOutDTO>> getManagerEmployee(final long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        try {
            if(userId != UserConstants.getAdminId()) {
                User manager = userRepository.findById(userId).orElseThrow(() -> {
                    log.error("Manager with ID {} not found", userId);
                    throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                });

                String managerName = manager.getFirstName() + manager.getLastName();
                List<User> users = userRepository.findByManagerId(userId);
                if (users.isEmpty()) {
                    log.warn("No employees found");
                    return StandardResponseOutDTO.success(Collections.emptyList(),UserConstants.USER_NOT_FOUND);
                }

                List<UserOutDTO> response = new ArrayList<>();
                for (User user : users) {

                    UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName, "employee");
                    response.add(userDto);
                }

                log.info("Successfully fetched {} employees for manager with ID: {}", response.size(), userId);
                return StandardResponseOutDTO.success(response,"Successfully fetched employee for Manager");
            }
            else {
                throw new InvalidRequestException(INVALID_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error fetching employees for manager with ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }


    public MessageOutDto updateUserDetails(RegisterDto registerDto , long userId ){
        log.info("updating user information");
        try {
            if (userId != UserConstants.getAdminId()) {
                User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                });

                if (!registerDto.getFirstName().isEmpty())
                    user.setFirstName(registerDto.getFirstName());
                if (!registerDto.getLastName().isEmpty())
                    user.setLastName(registerDto.getLastName());
                if (!registerDto.getUserName().isEmpty())
                    user.setUserName(registerDto.getUserName());
                if (!registerDto.getEmail().isEmpty())
                    user.setEmail(registerDto.getEmail());
                if (registerDto.getRoleId() != null)
                    user.setRoleId(registerDto.getRoleId());

                userRepository.save(user);

                return new MessageOutDto(USER_UPDATED_SUCCESSFULLY);
            }
            else{
                throw new InvalidRequestException(INVALID_REQUEST);
            }
        }
        catch(Exception e){
            log.error("Error fetching employees for manager with ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }

}
