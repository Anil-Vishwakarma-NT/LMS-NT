package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserBundleEnrollmentOutDTO {

    private Long individualEnrollments;

    private Long bundleName;

    private Long isActive;

    private List<EnrolledUserDTO> enrolledUserDTOList;
}
