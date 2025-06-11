package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.dto.UsersDetailsViewDTO;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.entities.Role;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.EnrollmentRepository;
import com.nt.LMS.repository.RoleRepository;
import com.nt.LMS.repository.UserGroupRepository;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.nt.LMS.entities.UserGroup;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface that handles user-related operations.
 * This class provides the method to load user details by username (email).
 *
 * <p> It interacts with the user repository to fetch user data and role details. </p>
 */
@Slf4j
@Service
public final class UserServiceImpl implements UserService {  // Made the class final since it's not intended for extension

    /**
     * To access user table.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * To access role table.
     */
    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private EnrollmentRepository enrollmentRepository;


    @Autowired
    private UserGroupRepository userGroupRepository;
    /**
     * Loads the user details based on the provided email.
     * It retrieves the user and their associated role, and then constructs a UserDetails object
     * for Spring Security authentication.
     *
     * @param email The email of the user to be loaded.
     * @return The UserDetails object containing user information and their authorities.
     * @throws UsernameNotFoundException If the user is not found with the given email.
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {  // Marked email as final
        // Fetch the user by email
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(UserConstants.USER_NOT_FOUND + " : " + email));

        // Fetch the role of the user
        Optional<Role> role = roleRepository.findById(user.getRoleId());

        // Return the user details for Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(role.get().getName())
                .build();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.isActive() && user.getUserId() != 1)
                .count();
    }

    @Override
    public List<UsersDetailsViewDTO> getRecentUserDetails() {
        List<Object[]> results = userRepository.fetchRecentUserDetails();

        return results.stream().map(obj -> {
            UsersDetailsViewDTO dto = new UsersDetailsViewDTO();
            dto.setFullName((String) obj[0]);
            dto.setEmail((String) obj[1]);
            dto.setRole((String) obj[2]);
            dto.setManagerName((String) obj[3]);
            dto.setCreatedAt((Timestamp) obj[4]);
            return dto;
        }).collect(Collectors.toList());
    }

//    //helper methods

//    public List<Enrollment> getAllEnrolledCourses(long userId){
//        List<Enrollment> enrols = enrollmentRepository. getAllUserEnrols(userId).orElseThrow(() -> {
//            log.error("User with ID {} not found", userId);
//            throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
//        });
//
//        return enrols;
//    }


    @Override
    public Map<String , Long> userStatistics(long userId){
        Map<String , Long> stats = new HashMap<>();

       long enrols = enrollmentRepository.getAllUserEnrols(userId);
       stats.put("enrollments" , enrols);
       long userGroup = userGroupRepository.getAllUserGroups(userId);
       stats.put("groups",userGroup);

       return stats;
    }


}



