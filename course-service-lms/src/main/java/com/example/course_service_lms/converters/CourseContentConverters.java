package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.entity.CourseContent;

public class CourseContentConverters {
    public static CourseContent courseContentDtoToCourseContent(CourseContentDTO courseContentDTO) {
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(courseContentDTO.getCourseId());
        courseContent.setTitle(courseContentDTO.getTitle());
        courseContent.setDescription(courseContentDTO.getDescription());
        courseContent.setVideoLink(courseContentDTO.getVideoLink());
        courseContent.setResourceLink(courseContentDTO.getResourceLink());
        return courseContent;
    }
}
