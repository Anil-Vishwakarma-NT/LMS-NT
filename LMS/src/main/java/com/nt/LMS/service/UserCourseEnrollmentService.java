package com.nt.LMS.service;

import com.nt.LMS.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.LMS.dto.outDTO.UserEnrollDetailsOutDTO;

import java.util.List;

public interface UserCourseEnrollmentService {
    List<UserCourseEnrollmentOutDTO> getUserEnrollmentsByCourse();
    List<UserEnrollDetailsOutDTO> getUserEnrolledCourses(Long userId);
}
