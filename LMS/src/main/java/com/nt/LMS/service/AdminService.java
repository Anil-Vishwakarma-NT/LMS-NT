package com.nt.LMS.service;

import com.nt.LMS.exceptions.ManagerNotFoundException;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public void deleteEmployee(Long id) {
        userRepository.findById(id).ifPresentOrElse(user -> {
            userRepository.deleteById(id);
        }, () -> {
            throw new IllegalArgumentException("User not found for deletion");
        });
    }

    public void deleteManager(Long id) {
        Optional<User> manager = userRepository.findById(id);
        if (manager.isEmpty()) {
            throw new ManagerNotFoundException("Manager not found with ID: " + id);
        }

        // Assuming `updateManager` is a method that disassociates manager from any users or other records
        userRepository.updateManager(1L, manager.get().getUserId());

        // Deleting the manager
        userRepository.deleteById(manager.get().getUserId());
    }

    public void changeRole(User user , Role role){
        long count = userRepository.updateRole(role.getRole_id() , user.getUserId());
    }


    public List<User> getAllUsers(){
      List<User> employees = userRepository.getAllEmployees();
      return employees;
    }

    public List<User> getManagerEmp(long id){
        List<User> employees = userRepository.getEmployeesUnderManager(id);
        return employees;
    }
}
