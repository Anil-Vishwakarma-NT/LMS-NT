package com.nt.LMS.dto.outDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCourseOutDTO {

    String courseName;
    long courseId;
    double progress;
    long enrols;

}
