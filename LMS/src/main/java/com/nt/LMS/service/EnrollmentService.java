package com.nt.LMS.service;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface EnrollmentService {
    String enrollUser(EnrollmentDTO enrollmentDTO);
}
