package com.nt.LMS.service;

import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.RegisterDto;
import com.nt.LMS.dto.UserOutDTO;

import java.util.List;

public interface AdminService {
    public MessageOutDto register(RegisterDto registerDto);

    public MessageOutDto employeeDeletion(long id);

    public List<UserOutDTO> getAllUsers();

    public MessageOutDto changeUserRole(long userId, String newRoleName);

    public List<UserOutDTO> getManagerEmployee(long userId);

    public List<UserOutDTO> getEmployees(String username);
}