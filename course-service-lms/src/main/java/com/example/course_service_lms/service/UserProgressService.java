package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.outDTO.UserProgressOutDTO;

public interface UserProgressService {

    void updateProgress(UserProgressOutDTO progressDTO);

    Double getCourseProgress(int userId, int courseId);

    Integer getLastPosition(int userId, int courseId, int contentId);

    Double getContentProgress(int userId, int courseId, int contentId);

}