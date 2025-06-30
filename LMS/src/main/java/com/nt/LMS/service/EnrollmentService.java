package com.nt.LMS.service;

import com.nt.LMS.dto.inDTO.EnrollmentRequestInDTO;
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
    List<EnrollmentOutDTO> enroll(EnrollmentRequestInDTO requestDTO);
    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();
    UserEnrollmentsOutDTO getUserEnrollmentsByUserID(Long userId);
    List<UserEnrollmentsOutDTO> getAllUsersEnrollments();
    /**
     * Get individual course enrollments details grouped by course
     * @return List of UserCourseEnrollmentOutDTO containing course details and enrolled users
     */
    List<UserCourseEnrollmentOutDTO> getIndividualCourseEnrollments();
    List<UserBundleEnrollmentOutDTO> getIndividualBundleEnrollments();

    List<UserCourseEnrollDetails> getUserEnrolledCourses(Long userId);
//
//    long countEnrollments();
//
//    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();
//
//    List<UserEnrollmentsOutDTO> getEnrollmentsForUser();
}