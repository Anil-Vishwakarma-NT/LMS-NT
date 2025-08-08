package com.nt.user_service_lms.service;

import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.*;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import com.nt.user_service_lms.dto.outDTO.AdminDashboardStatsOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;

import java.util.List;

/**
 * Service interface for performing admin-related operations in the LMS system.
 * <p>
 * This includes user management, role updates, dashboard stats,
 * bundle management, and more.
 * </p>
 *
 * @author
 */
public interface AdminService {

    /**
     * Registers a new user into the system.
     *
     * @param registerDto the registration details for the user
     * @return a standardized response containing a message DTO
     */
    StandardResponseOutDTO<MessageOutDTO> register(RegisterDto registerDto);

    /**
     * Deletes an employee based on the provided ID.
     *
     * @param id the ID of the employee to delete
     * @return a standardized response containing a message DTO
     */
    StandardResponseOutDTO<MessageOutDTO> employeeDeletion(long id);

    /**
     * Retrieves a list of all currently active users in the system.
     *
     * @return a standardized response containing a list of user DTOs
     */
    StandardResponseOutDTO<List<UserOutDTO>> getAllActiveUsers();

    /**
     * Retrieves a list of all currently inactive users in the system.
     *
     * @return a standardized response containing a list of user DTOs
     */
    StandardResponseOutDTO<List<UserOutDTO>> getAllInactiveUsers();

    /**
     * Changes the role of a user.
     *
     * @param userId      the ID of the user whose role is to be changed
     * @param newRoleName the new role to assign to the user
     * @return a standardized response containing a message DTO
     */
    StandardResponseOutDTO<MessageOutDTO> changeUserRole(long userId, String newRoleName);

    /**
     * Retrieves a list of employees who report to a specific manager.
     *
     * @param userId the ID of the manager
     * @return a standardized response containing a list of user DTOs
     */
    StandardResponseOutDTO<List<UserOutDTO>> getManagerEmployee(long userId);

    /**
     * Retrieves admin dashboard statistics such as user count, course count, etc.
     *
     * @return a standardized response containing admin dashboard statistics
     */
    StandardResponseOutDTO<AdminDashboardStatsOutDTO> getAdminStats();

    /**
     * Updates details of a user based on the provided user ID and data.
     *
     * @param registerDto the new user data
     * @param userId      the ID of the user to update
     * @return a message DTO confirming the update
     */
    MessageOutDTO updateUserDetails(UserInDTO registerDto, long userId);

    /**
     * Deletes a bundle by its ID.
     *
     * @param bundleId the ID of the bundle to delete
     * @return a standardized response containing a message DTO
     */
    StandardResponseOutDTO<MessageOutDTO> deleteBundle(long bundleId);

    /**
     * Removes a specific course from a bundle.
     *
     * @param bundleId the ID of the bundle
     * @param courseId the ID of the course to remove from the bundle
     * @return a standardized response containing a message DTO
     */
    StandardResponseOutDTO<MessageOutDTO> removeCourseFromBundle(Long bundleId, Long courseId);
    StandardResponseOutDTO<BulkUploadResponseOutDTO> bulkUploadUsers(MultipartFile file);
    Resource generateTemplate(String format) throws IOException;

}
