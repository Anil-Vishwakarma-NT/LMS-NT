package com.nt.LMS.serviceImpl;

import com.nt.LMS.constants.UserConstants;
import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserDTOConverter userDTOConverter;
    /**
     * Fetches employees under a manager by username.
     *
     * @param username the manager's email/username
     * @return a list of UserOutDTO
     */
    @Override
    public List<UserOutDTO> getEmployees(final String username) {
        log.info("Fetching employees for manager with email: {}", username);
        try {
            User manager = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> {
                        log.error("Manager with email {} not found", username);
                        throw new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
                    });

            String managerName = manager.getFirstName() + manager.getLastName();
            List<User> users = userRepository.findByManagerId(manager.getUserId());
            if (users.isEmpty()) {
                log.warn("No employees found");
                return Collections.emptyList();
            }

            List<UserOutDTO> response = new ArrayList<>();
            for (User u : users) {
                UserOutDTO userDto = userDTOConverter.userToOutDto(u, managerName);
                response.add(userDto);
            }

            log.info("Successfully fetched {} employees for manager with email: {}", response.size(), username);
            return response;
        } catch (Exception e) {
            log.error("Error fetching employees for manager with email: {}", username, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }
}
