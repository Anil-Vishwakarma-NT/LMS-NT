package com.nt.user_service_lms.service;

import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;

import java.util.List;

/**
 * Service interface for admin-related operations.
 */
public interface AdminService {

    /**
     * Registers a new user.
     *
     * @param registerDto the registration details
     * @return a message response
     */
    StandardResponseOutDTO<MessageOutDTO> register(RegisterDto registerDto);

    /**
     * Deletes an employee by their ID.
     *
     * @param id the employee ID
     * @return a message response
     */
    StandardResponseOutDTO<MessageOutDTO> employeeDeletion(long id);

    /**
     * Gets a list of all users.
     *
     * @return list of user DTOs
     */
    StandardResponseOutDTO<List<UserOutDTO>> getAllActiveUsers();

    /**
     * Gets a list of all inactive users.
     *
     * @return list of user DTOs.
     */
    StandardResponseOutDTO<List<UserOutDTO>> getAllInactiveUsers();

    /**
     * Changes the role of a user.
     *
     * @param userId      the user ID
     * @param newRoleName the new role name
     * @return a message response
     */
    StandardResponseOutDTO<MessageOutDTO> changeUserRole(long userId, String newRoleName);

    /**
     * Gets employees under a specific manager.
     *
     * @param userId the manager's user ID
     * @return list of user DTOs
     */
    StandardResponseOutDTO<List<UserOutDTO>> getManagerEmployee(long userId);


    StandardResponseOutDTO<MessageOutDTO> deleteBundle(long bundleId);


    StandardResponseOutDTO<MessageOutDTO> removeCourseFromBundle(Long bundleId, Long courseId);

   }

