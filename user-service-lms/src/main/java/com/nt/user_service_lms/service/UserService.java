package com.nt.user_service_lms.service;

import com.nt.user_service_lms.dto.UsersDetailsViewDTO;
import com.nt.user_service_lms.dto.outDTO.CourseDeadlinesDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Service interface for user-related operations in the LMS.
 */
public interface UserService extends UserDetailsService {

    /**
     * Loads the user details by email.
     *
     * @param email the email of the user
     * @return the user details
     * @throws UsernameNotFoundException if the user is not found
     */
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Counts the number of active users.
     *
     * @return the count of active users
     */
    long countActiveUsers();

    /**
     * Retrieves the most recent user details.
     *
     * @return a list of recent user details
     */
    List<UsersDetailsViewDTO> getRecentUserDetails();

    /**
     * Provides user statistics for a given user ID.
     *
     * @param userId the user ID
     * @return a map containing user statistics
     */
    Map<String, Long> userStatistics(long userId);

    /**
     * Retrieves the course deadlines for a user by email.
     *
     * @param email the email of the user
     * @return a standard response containing a list of course deadlines
     */
    StandardResponseOutDTO<List<CourseDeadlinesDTO>> deadlineCourses(String email);

    /**
     * Gets the list of courses a user is enrolled in.
     *
     * @param userId the user ID
     * @return a list of user course enrollment details
     */
    List<UserCourseEnrollDetails> getUserEnrolledCourses(Long userId);
}



