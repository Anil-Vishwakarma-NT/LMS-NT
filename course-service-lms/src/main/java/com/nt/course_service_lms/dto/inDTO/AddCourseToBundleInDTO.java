package com.nt.course_service_lms.dto.inDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseToBundleInDTO {

    Long bundleId;

    List<Long> courses;

}
