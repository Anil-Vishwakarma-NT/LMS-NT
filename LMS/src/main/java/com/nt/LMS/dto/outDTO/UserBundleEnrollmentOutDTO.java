package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.util.List;

@Data
public class UserBundleEnrollmentOutDTO {

    private String bundleName;

    private Long totalCourses;

    private Long individualEnrollments;

    private Float averageCompletion;

    private boolean isActive;

    private List<EnrolledUserOutDTO> enrolledUserOutDTOList;
}
