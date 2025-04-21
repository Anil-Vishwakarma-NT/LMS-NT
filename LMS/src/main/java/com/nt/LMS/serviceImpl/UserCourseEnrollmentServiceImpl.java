package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.UserCourseEnrollmentOutDTO;
import com.nt.LMS.entities.UserCourseEnrollment;
import com.nt.LMS.service.UserCourseEnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCourseEnrollmentServiceImpl implements UserCourseEnrollmentService {
    @Override
    public List<UserCourseEnrollment> findByCourseId(Long courseId) {
        return List.of();
    }

    @Override
    public List<UserCourseEnrollmentOutDTO> findByUserId() {
        return List.of();
    }
}
