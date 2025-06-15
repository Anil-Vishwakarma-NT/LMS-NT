package com.nt.LMS.service;

import com.nt.LMS.dto.inDTO.EnrollmentInDTO;
import com.nt.LMS.dto.inDTO.EnrollmentRequestDTO;
import com.nt.LMS.dto.outDTO.*;
import com.nt.LMS.entities.Enrollment;

import java.util.List;

public interface EnrollmentService {

    /**
     * Handles all types of enrollment scenarios:
     * - User to Course enrollment
     * - User to Bundle enrollment
     * - Group to Course enrollment
     * - Group to Bundle enrollment
     *
     * Also handles edge cases like:
     * - Existing enrollments with different sources
     * - Bundle expansion to individual courses
     * - Group member individual enrollments
     * - Priority-based enrollment source management
     *
     * @param requestDTO The enrollment request containing all necessary information
     * @return List of created/updated enrollments
     * @throws com.nt.LMS.exception.ResourceNotFoundException if referenced entities don't exist
     * @throws com.nt.LMS.exception.ResourceAlreadyExistsException if enrollment already exists and force is not enabled
     * @throws com.nt.LMS.exception.ResourceNotValidException if request is invalid or enrollment fails
     */
    List<Enrollment> enroll(EnrollmentRequestDTO requestDTO);
//
//    long countEnrollments();
//
//    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();
//
//    List<UserEnrollmentsOutDTO> getEnrollmentsForUser();
}