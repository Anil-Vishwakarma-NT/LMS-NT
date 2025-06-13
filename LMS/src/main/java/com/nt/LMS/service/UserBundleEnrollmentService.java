package com.nt.LMS.service;

import com.nt.LMS.dto.outDTO.UserBundleEnrollmentOutDTO;

import java.util.List;

public interface UserBundleEnrollmentService {
    List<UserBundleEnrollmentOutDTO> getUserEnrollmentsByBundle();
}
