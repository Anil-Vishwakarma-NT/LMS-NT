package com.nt.user_service_lms.service;

import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;

import java.util.List;

public interface ManagerService {

    /**
     * Gets employees by username.
     *
     * @param username the username to filter by
     * @return list of user DTOs
     */
    StandardResponseOutDTO<List<UserOutDTO>> getEmployees(String username);
}
