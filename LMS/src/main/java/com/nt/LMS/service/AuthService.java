package com.nt.LMS.service;

import com.nt.LMS.config.JwtUtil;
import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private  JwtUtil jwtUtil;

    @Autowired
    private  UserService userDetailsService;

    @Autowired
    private  RoleRepository roleRepository;


    public String register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
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
        return "User registered successfully";
    }

//    public String login(LoginDto loginDto) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
//        );
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//        return jwtUtil.generateToken(userDetails);
//    }
public String login(LoginDto loginDto) {
    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return jwtUtil.generateToken(userDetails);
    } catch (Exception e) {
        e.printStackTrace();
        return "Authentication failed: " + e.getMessage();
    }
}

}
