package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.example.course_service_lms.dto.outDTO.UserProgressOutDTO;

public interface UserProgressService {

    void updateProgress(UserProgressOutDTO progressDTO);

    CourseProgressWithMetaDTO getCourseProgressWithMeta(int userId, int courseId);

    Integer getLastPosition(int userId, int courseId, int contentId);

    Double getContentProgress(int userId, int courseId, int contentId);



}