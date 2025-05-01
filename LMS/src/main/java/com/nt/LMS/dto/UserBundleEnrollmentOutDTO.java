package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserBundleEnrollmentOutDTO {

    private String bundleName;

    private Long totalCourses;

    private Long individualEnrollments;

    private Float averageCompletion;

    private boolean isActive;

    private List<EnrolledUserDTO> enrolledUserDTOList;
}
