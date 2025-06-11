package com.nt.LMS.service;

import com.nt.LMS.dto.inDTO.EnrollmentInDTO;
import com.nt.LMS.dto.outDTO.EnrollmentDashBoardStatsOutDTO;
import com.nt.LMS.dto.outDTO.UserEnrollmentsOutDTO;

import java.util.List;

public interface EnrollmentService {
    String enrollUser(EnrollmentInDTO enrollmentInDTO);
    //String updateEnrollment(UpdateEnrollmentDTO updateEnrollmentDTO);
    //String unEnroll();
    long countEnrollments();
    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();
    List<UserEnrollmentsOutDTO> getEnrollmentsForUser();
}
