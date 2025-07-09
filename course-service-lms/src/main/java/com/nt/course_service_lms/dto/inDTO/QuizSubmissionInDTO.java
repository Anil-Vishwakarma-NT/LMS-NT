package com.nt.course_service_lms.dto.inDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;

/**
 * DTO for quiz submission input containing user responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionInDTO {

    @Valid
    private List<UserResponseInDTO> userResponses;

    // Optional metadata
    private String notes;
    private Long timeSpent; // total time spent on quiz in seconds
}