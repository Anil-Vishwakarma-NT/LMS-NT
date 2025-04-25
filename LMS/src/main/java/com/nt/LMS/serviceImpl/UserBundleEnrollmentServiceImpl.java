package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.UserBundleEnrollmentOutDTO;
import com.nt.LMS.service.UserBundleEnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBundleEnrollmentServiceImpl implements UserBundleEnrollmentService {
    @Override
    public List<UserBundleEnrollmentOutDTO> getUserEnrollmentsByBundle() {
        return List.of();
    }
}
