package com.nt.course_service_lms.dto.outDTO;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDetailsByUserIDOutDTO {
    private CourseOutDTO courseOutDTO;
    private List<UserQuizAttemptDetailsOutDTO> userQuizAttemptDetailsOutDTOS;
}
