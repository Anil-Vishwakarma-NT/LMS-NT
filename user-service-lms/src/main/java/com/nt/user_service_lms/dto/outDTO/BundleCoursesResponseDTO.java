package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.util.List;

@Data
public class BundleCoursesResponseDTO {
    private Long bundleId;
    private String bundleName;
    private List<CourseMetaDTO> courses;
}

