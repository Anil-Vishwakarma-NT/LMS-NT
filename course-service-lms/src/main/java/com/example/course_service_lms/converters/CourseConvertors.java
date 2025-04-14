package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseLevel;
import com.example.course_service_lms.utils.StringUtils;

public class CourseConvertors {
    public static Course courseDtoToCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setTitle(StringUtils.toProperCase(courseDTO.getTitle()));
        course.setOwnerId(courseDTO.getOwnerId());
        course.setDescription(StringUtils.toProperCase(courseDTO.getDescription()));
        course.setLevel(CourseLevel.valueOf(courseDTO.getCourseLevel().toUpperCase()));
        course.setImage(courseDTO.getImage());
        return course;
    }
}
