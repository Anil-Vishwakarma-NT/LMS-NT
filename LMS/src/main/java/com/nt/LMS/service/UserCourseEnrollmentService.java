package com.nt.LMS.service;

import com.nt.LMS.dto.UserCourseEnrollmentOutDTO;
import com.nt.LMS.dto.UserEnrollDetails;
import com.nt.LMS.entities.UserCourseEnrollment;

import java.util.List;

public interface UserCourseEnrollmentService {
    List<UserCourseEnrollmentOutDTO> getUserEnrollmentsByCourse();
    List<UserEnrollDetails> getUserEnrolledCourses(Long userId);
}
