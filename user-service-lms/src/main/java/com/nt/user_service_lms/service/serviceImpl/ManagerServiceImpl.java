package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.constants.UserConstants;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserOutDTO;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service implementation that exposes manager‑specific operations, primarily the ability
 * to retrieve the employees reporting to a given manager.
 * <p>
 * This class is annotated with {@link Service} so that Spring can detect and manage it as a
 * singleton bean. It relies on {@link UserRepository} for data access and
 * {@link UserDTOConverter} for transforming {@link User} domain objects into their
 * corresponding DTO representations.
 * </p>
 */
@Slf4j
@Service
public class ManagerServiceImpl implements ManagerService {

    /**
     * Repository providing CRUD operations for {@link User} entities.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Converter used to transform {@link User} entities into {@link UserOutDTO} instances.
     */
    @Autowired
    private UserDTOConverter userDTOConverter;

    /**
     * Retrieves every employee that reports to the manager identified by the given username
     * (treated here as an email address).
     *
     * @param username the manager's email address
     * @return a {@link StandardResponseOutDTO} containing a list of {@link UserOutDTO}
     *         objects that represent the employees
     * @throws ResourceNotFoundException if no manager is found for the supplied username
     * @throws RuntimeException          if an unexpected error occurs while processing the request
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getEmployees(final String username) {
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
                return StandardResponseOutDTO.success(Collections.emptyList(), UserConstants.USER_NOT_FOUND);
            }

            List<UserOutDTO> response = new ArrayList<>();
            for (User u : users) {
                UserOutDTO userDto = userDTOConverter.userToOutDto(u, managerName, "employee");
                response.add(userDto);
            }

            log.info("Successfully fetched {} employees for manager with email: {}", response.size(), username);
            return StandardResponseOutDTO.success(response, "Successfully fetched employees for manager");
        } catch (Exception e) {
            log.error("Error fetching employees for manager with email: {}", username, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }
}
