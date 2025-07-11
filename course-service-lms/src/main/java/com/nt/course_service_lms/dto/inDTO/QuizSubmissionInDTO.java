package com.nt.course_service_lms.dto.inDTO;

import lombok.Data;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

/**
 * DTO for quiz submission input containing user responses
 */
@Data
public class QuizSubmissionInDTO {

    @Valid
    private List<UserResponseInDTO> userResponses;

    // Optional metadata
    private String notes;
    private Long timeSpent; // total time spent on quiz in seconds

    public QuizSubmissionInDTO() {
    }

    public QuizSubmissionInDTO(List<UserResponseInDTO> userResponses, String notes, Long timeSpent) {
        this.userResponses = userResponses;
        this.notes = notes;
        this.timeSpent = timeSpent;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizSubmissionInDTO that = (QuizSubmissionInDTO) o;
        return Objects.equals(userResponses, that.userResponses) && Objects.equals(notes, that.notes) && Objects.equals(timeSpent, that.timeSpent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userResponses, notes, timeSpent);
    }
}