package com.nt.user_service_lms.service;

import com.nt.user_service_lms.dto.inDTO.EnrollmentRequestInDTO;
import com.nt.user_service_lms.dto.outDTO.EnrollmentDashBoardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.EnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserBundleEnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollmentOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserEnrollmentsOutDTO;

import java.util.List;

/**
 * Service interface for managing user and group enrollments in courses and bundles.
 * Provides methods for enrolling users, retrieving enrollment statistics, and fetching enrollment details.
 */
public interface EnrollmentService {

    /**
     * Handles all types of enrollment scenarios including user and group enrollments to courses and bundles.
     * Also manages edge cases such as existing enrollments, bundle expansion, and priority-based source management.
     *
     * @param requestDTO the enrollment request containing all necessary information
     * @return list of created or updated enrollments
     * @throws com.nt.user_service_lms.exception.ResourceNotFoundException if referenced entities do not exist
     * @throws com.nt.user_service_lms.exception.ResourceAlreadyExistsException if
     * enrollment already exists and force is not enabled
     * @throws com.nt.user_service_lms.exception.ResourceNotValidException if the request is invalid or enrollment fails
     */
    List<EnrollmentOutDTO> enroll(EnrollmentRequestInDTO requestDTO);

    /**
     * Retrieves enrollment dashboard statistics.
     *
     * @return enrollment dashboard statistics data transfer object
     */
    EnrollmentDashBoardStatsOutDTO getEnrollmentStats();

    /**
     * Retrieves all enrollments for a specific user by user ID.
     *
     * @param userId the ID of the user
     * @return user enrollments data transfer object
     */
    UserEnrollmentsOutDTO getUserEnrollmentsByUserID(Long userId);

    /**
     * Retrieves enrollments for all users.
     *
     * @return list of user enrollments data transfer objects
     */
    List<UserEnrollmentsOutDTO> getAllUsersEnrollments();

    /**
     * Retrieves individual course enrollment details grouped by course.
     *
     * @return list of user course enrollment data transfer objects containing course details and enrolled users
     */
    List<UserCourseEnrollmentOutDTO> getIndividualCourseEnrollments();

    /**
     * Retrieves individual bundle enrollment details.
     *
     * @return list of user bundle enrollment data transfer objects
     */
    List<UserBundleEnrollmentOutDTO> getIndividualBundleEnrollments();

    /**
     * Retrieves the list of courses a user is enrolled in.
     *
     * @param userId the ID of the user
     * @return list of user course enrollment details
     */
    List<UserCourseEnrollDetails> getUserEnrolledCourses(Long userId);
}
