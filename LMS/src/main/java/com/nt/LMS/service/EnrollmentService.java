package com.nt.LMS.service;

import com.nt.LMS.dto.EnrollmentDTO;
import com.nt.LMS.dto.EnrollmentDashBoardStatsDTO;
import com.nt.LMS.dto.UpdateEnrollmentDTO;
import com.nt.LMS.dto.UserEnrollmentsDTO;
import com.nt.LMS.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface EnrollmentService {
    String enrollUser(EnrollmentDTO enrollmentDTO);
    //String updateEnrollment(UpdateEnrollmentDTO updateEnrollmentDTO);
    //String unEnroll();
    long countEnrollments();
    EnrollmentDashBoardStatsDTO getEnrollmentStats();
    List<UserEnrollmentsDTO> getEnrollmentsForUser();
}
