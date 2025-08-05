package com.nt.course_service_lms.dto.inDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseToBundleInDTO {
    /**
     * Bundle Id for bundle
     */
    private Long bundleId;

    /**
     * list of courses to be added.
     */
    private List<Long> courses;

}
