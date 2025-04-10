package com.nt.LMS.service;


import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.exception.ResourceNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    UserDTOConverter userDTOConverter;




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
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        String role = user.getRole().getName();
        if (role.equalsIgnoreCase("employee")) {
            userRepository.deleteById(user.getUserId());
        } else if (role.equalsIgnoreCase("manager")) {
            userRepository.updateManager(1L, user.getUserId()); // Update manager to a default manager or a specific one
            userRepository.deleteById(user.getUserId());
        } else {
            throw new IllegalStateException(INVALID_USER_ROLE);
        }
    }



    public List<UserOutDTO> getAllUsers() {
        try {
            List<User> employees = userRepository.getEmployees();
            if (employees.isEmpty()) {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            List<UserOutDTO> res = new ArrayList<>();
            for(User user : employees){
                Optional<User> userOpt = userRepository.findById(user.getManagerId());
                User manager = userOpt.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
                String managername = manager.getFirstName() + manager.getLastName();
                UserOutDTO userdto = userDTOConverter.userToOutDto(user , managername);
                res.add(userdto);
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(DATABASE_ERROR, e);
        }
    }


    public boolean changeRole(long userId, String role) {
        try {
            Role r = roleRepository.findByRoleName(role);
            if (r == null) {
                throw new IllegalArgumentException(INVALID_USER_ROLE);
            }
            Optional<User> userOpt = userRepository.findById(userId);
            User user = userOpt.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
            int rowsAffected = userRepository.updateRole(r.getRoleId(), userId);
            return rowsAffected > 0;
        }catch (Exception e) { throw new RuntimeException(ERROR  + e); }
    }


    public List<UserOutDTO> getManEmp(long userId) {
        try {Optional<User> managerOpt = userRepository.findById(userId);
            if (managerOpt.isEmpty()) {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
            List<User> users = userRepository.getEmployeesUnderManager(userId);
            List<UserOutDTO> response = new ArrayList<>();
            for(User user : users){
                String managername = "";
                UserOutDTO userdto = userDTOConverter.userToOutDto(user , managername);
                response.add(userdto);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(ERROR, e);
        }
    }



    public List<UserOutDTO> getEmps(String username) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(username);
            User user = userOpt.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
            List<User> users = userRepository.getEmployeesUnderManager(user.getUserId());
            List<UserOutDTO> response = new ArrayList<>();
            for(User u : users){
                String managername = "";
                UserOutDTO userdto = userDTOConverter.userToOutDto(u , managername);
                response.add(userdto);
            }
            return response;
        }catch (Exception e){
            throw  new RuntimeException(ERROR + e);
        }
    }
}





