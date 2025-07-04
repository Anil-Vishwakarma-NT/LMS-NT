package com.nt.LMS.service;

import com.nt.LMS.dto.UsersDetailsViewDTO;
import com.nt.LMS.dto.outDTO.UserCourseEnrollDetails;
import com.nt.LMS.entities.User;
import com.nt.LMS.dto.outDTO.CourseDeadlinesDTO;
import com.nt.LMS.dto.outDTO.CourseInfoOutDTO;
import com.nt.LMS.dto.outDTO.StandardResponseOutDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
    public long countActiveUsers();
    List<UsersDetailsViewDTO> getRecentUserDetails();

    public Map<String , Long> userStatistics(long userId);

    StandardResponseOutDTO<List<CourseDeadlinesDTO>> deadlineCourses(String email);

    public List<UserCourseEnrollDetails> getUserEnrolledCourses(Long userId);
}




