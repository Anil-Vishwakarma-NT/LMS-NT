package com.nt.LMS.service;

import com.nt.LMS.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.LMS.dto.UserEnrollDetails;

import java.util.List;

public interface UserCourseEnrollmentService {
    List<UserCourseEnrollmentOutDTO> getUserEnrollmentsByCourse();
    List<UserEnrollDetails> getUserEnrolledCourses(Long userId);
}
