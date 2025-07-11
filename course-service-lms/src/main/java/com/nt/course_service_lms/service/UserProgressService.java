package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;

public interface UserProgressService {

    void updateProgress(UserProgressOutDTO progressDTO);

    CourseProgressWithMetaDTO getCourseProgressWithMeta(Long userId, Long courseId);

    Integer getLastPosition(Long userId, Long courseId, Long contentId);

    Double getContentProgress(Long userId, Long courseId, Long contentId);

}