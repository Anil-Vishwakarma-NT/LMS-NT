package com.nt.LMS.service;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.dto.EnrollmentDashBoardStatsDTO;
import com.nt.LMS.dto.UpdateEnrollmentDTO;
import com.nt.LMS.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface EnrollmentService {
    String enrollUser(EnrollmentDTO enrollmentDTO);
    //String updateEnrollment(UpdateEnrollmentDTO updateEnrollmentDTO);
    //String unEnroll();
    long countEnrollments();
    EnrollmentDashBoardStatsDTO getEnrollmentStats();

}
