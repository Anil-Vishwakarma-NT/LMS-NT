package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.util.List;

@Data
public class UserCourseEnrollmentOutDTO {
    private Long courseId;
    private Long ownerId;
    private Long individualEnrollments;
    private String courseName;
    private String ownerName;
    private boolean isActive;
    private List<EnrolledUserOutDTO> enrolledUserOutDTOList;
}