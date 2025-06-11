package com.nt.LMS.service;

import com.nt.LMS.dto.inDTO.EnrollmentInDTO;
import com.nt.LMS.dto.outDTO.*;

import java.util.List;

public interface EnrollmentService {

    EnrollmentOutDTO enrollUser(EnrollmentInDTO enrollmentInDTO);

    long countEnrollments();

    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();

    List<UserEnrollmentsOutDTO> getEnrollmentsForUser();
}