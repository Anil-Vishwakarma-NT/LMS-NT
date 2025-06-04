package com.nt.LMS.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserCourseEnrollmentOutDTO {
    private Long ownerId;
    private Long individualEnrollments;
    private String courseName;
    private String ownerName;
    private boolean isActive;
    private List<EnrolledUserDTO> enrolledUserDTOList;
}
