package com.nt.LMS.service;

import com.nt.LMS.inDTO.EnrollmentInDTO;
import com.nt.LMS.outDTO.EnrollmentDashBoardStatsOutDTO;
import com.nt.LMS.outDTO.UserEnrollmentsOutDTO;

import java.util.List;

public interface EnrollmentService {
    String enrollUser(EnrollmentInDTO enrollmentInDTO);
    //String updateEnrollment(UpdateEnrollmentDTO updateEnrollmentDTO);
    //String unEnroll();
    long countEnrollments();
    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();
    List<UserEnrollmentsOutDTO> getEnrollmentsForUser();
}
