package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.entities.Enrollment;
import com.nt.LMS.feignClient.CourseClient;
import com.nt.LMS.repository.EnrollmentRepository;
import com.nt.LMS.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;

public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseClient courseClient;

    @Override
    public Enrollment enroll(EnrollmentDTO enrollmentDTO) {

        return null;
    }
}
