package com.nt.LMS.service;

import com.nt.LMS.dto.UserBundleEnrollmentOutDTO;
import com.nt.LMS.dto.UserCourseEnrollmentOutDTO;

import java.util.List;

public interface UserBundleEnrollmentService {
    List<UserBundleEnrollmentOutDTO> getUserEnrollmentsByBundle();
}
