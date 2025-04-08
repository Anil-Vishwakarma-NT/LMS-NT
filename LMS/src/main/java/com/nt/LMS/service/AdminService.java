package com.nt.LMS.service;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceConflictException;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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

        Role role = roleRepository.findById(registerDto.getRoleId()).orElseThrow(()-> new RuntimeException("Role does not exist"));

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

}
