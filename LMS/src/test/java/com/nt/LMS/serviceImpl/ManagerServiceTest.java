package com.nt.LMS.serviceImpl;

import com.nt.LMS.converter.UserDTOConverter;
import com.nt.LMS.dto.outDTO.UserOutDTO;
import com.nt.LMS.entities.User;
import com.nt.LMS.exception.ResourceNotFoundException;
import com.nt.LMS.repository.UserRepository;
import com.nt.LMS.service.serviceImpl.ManagerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.nt.LMS.constants.UserConstants.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ManagerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDTOConverter userDTOConverter;


    @Mock
    private ManagerServiceImpl managerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getEmployees_ShouldReturnEmployeeList_WhenManagerAndEmployeesExist() {
        // Arrange
        String managerEmail = "manager@example.com";
        User manager = new User(1L, "Alice", "Alice", "Smith", managerEmail, "alice123", 0L, 2L, null, null,true);

        User employee = new User(2L, "Bob", "Bob", "Jones", "bob@example.com", "bobjones", 1L, 3L, null, null,true);
        UserOutDTO userOutDTO = new UserOutDTO(); // You can populate as needed

        when(userRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(manager.getUserId())).thenReturn(List.of(employee));
        when(userDTOConverter.userToOutDto(employee, "AliceSmith")).thenReturn(userOutDTO);

        // Act
        List<UserOutDTO> result = managerService.getEmployees(managerEmail);

        // Assert
        assertEquals(1, result.size());
        assertEquals(userOutDTO, result.get(0));
        verify(userRepository).findByEmail(managerEmail);
        verify(userRepository).findByManagerId(manager.getUserId());
        verify(userDTOConverter).userToOutDto(employee, "AliceSmith");
    }

    @Test
    void getEmployees_ShouldReturnEmptyList_WhenNoEmployeesFound() {
        // Arrange
        String managerEmail = "manager@example.com";
        User manager = new User(1L, "Alice", "Alice", "Smith", managerEmail, "alice123", 0L, 2L, null, null,true);

        when(userRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(manager.getUserId())).thenReturn(Collections.emptyList());

        // Act
        List<UserOutDTO> result = managerService.getEmployees(managerEmail);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(managerEmail);
        verify(userRepository).findByManagerId(manager.getUserId());
        verify(userDTOConverter, never()).userToOutDto(any(), any());
    }

    @Test
    void getEmployees_ShouldThrowException_WhenManagerNotFound() {
        // Arrange
        String email = "invalid@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> {
            managerService.getEmployees(email);
        });

        // Print the actual exception for debugging
        ex.printStackTrace();

        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        assertEquals(USER_NOT_FOUND, ex.getCause().getMessage());

        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).findByManagerId(anyLong());
        verify(userDTOConverter, never()).userToOutDto(any(), any());
    }


}
