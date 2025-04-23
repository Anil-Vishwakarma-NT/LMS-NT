package com.nt.LMS.service;

import com.nt.LMS.dto.UserOutDTO;

import java.util.List;

public interface ManagerService {

    /**
     * Gets employees by username.
     *
     * @param username the username to filter by
     * @return list of user DTOs
     */
    List<UserOutDTO> getEmployees(String username);
}
