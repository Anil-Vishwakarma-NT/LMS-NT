package com.nt.LMS.service;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.entities.Enrollment;

public interface EnrollmentService {
    public String enroll(EnrollmentDTO enrollmentDTO);
    long countEnrollments();
}
