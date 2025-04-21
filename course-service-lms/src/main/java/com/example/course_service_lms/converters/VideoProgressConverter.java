package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.VideoProgressDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.entity.VideoProgress;

public class VideoProgressConverter {

    // Map DTO to Entity
    public static VideoProgress toEntity(VideoProgressDTO progressDTO) {
        VideoProgress progress = new VideoProgress();
        progress.setCourseId(progressDTO.getCourseId()); // Directly set courseId
        progress.setCourseContentId(progressDTO.getCourseContentId()); // Directly set courseContentId
        progress.setLastWatchedTime(progressDTO.getLastWatchedTime());
        progress.setPercentageCompleted(progressDTO.getPercentageCompleted());
        progress.setCourseTotalProgress(progressDTO.getCourseTotalProgress());
        return progress;
    }

    // Map Entity to DTO
    public static VideoProgressDTO toDTO(VideoProgress progress) {
        return new VideoProgressDTO(
                progress.getCourseId(), // Get courseId directly
                progress.getCourseContentId(), // Get courseContentId directly
                progress.getLastWatchedTime(),
                progress.getPercentageCompleted(),
                progress.getCourseTotalProgress()
        );
    }
}