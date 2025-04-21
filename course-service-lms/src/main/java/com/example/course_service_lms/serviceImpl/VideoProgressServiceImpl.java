package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.converters.VideoProgressConverter;
import com.example.course_service_lms.dto.VideoProgressDTO;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.entity.VideoProgress;
import com.example.course_service_lms.repository.CourseContentRepository;
import com.example.course_service_lms.repository.VideoProgressRepository;
import com.example.course_service_lms.service.VideoProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoProgressServiceImpl implements VideoProgressService {

    private final VideoProgressRepository videoProgressRepository;
    private final CourseContentRepository courseContentRepository;

    public VideoProgressServiceImpl(VideoProgressRepository videoProgressRepository,
                                    CourseContentRepository courseContentRepository) {
        this.videoProgressRepository = videoProgressRepository;
        this.courseContentRepository = courseContentRepository;
    }

    @Override
    public VideoProgressDTO getProgress(Long courseId, Long courseContentId) {
        // Fetch progress using scalar fields
        VideoProgress progress = videoProgressRepository.findByCourseIdAndCourseContentId(courseId, courseContentId);

        if (progress == null) {
            return new VideoProgressDTO(courseId, courseContentId, 0f, 0f, 0f); // Return default values
        }

        return VideoProgressConverter.toDTO(progress); // Convert entity to DTO
    }

    @Override
    public VideoProgressDTO updateProgress(VideoProgressDTO progressDTO) {
        // Fetch progress using scalar fields
        VideoProgress progress = videoProgressRepository.findByCourseIdAndCourseContentId(
                progressDTO.getCourseId(),
                progressDTO.getCourseContentId()
        );

        if (progress == null) {
            // Create new progress if not found
            progress = VideoProgressConverter.toEntity(progressDTO);
        } else {
            // Update existing progress
            progress.setLastWatchedTime(progressDTO.getLastWatchedTime());
            progress.setPercentageCompleted(progressDTO.getPercentageCompleted());
        }

        VideoProgress savedProgress = videoProgressRepository.save(progress);
        System.out.println("Updated video progress for courseContentId: " + progressDTO.getCourseContentId());

        // Trigger course progress update dynamically
        updateCourseProgress(progressDTO.getCourseId());

        return VideoProgressConverter.toDTO(savedProgress); // Return updated progress as DTO
    }

    // Helper Method to Recalculate Course Progress
    private void updateCourseProgress(Long courseId) {
        // Fetch all course contents for the given courseId
        List<CourseContent> totalContents = courseContentRepository.findByCourseId(courseId);

        if (totalContents.isEmpty()) {
            System.out.println("No videos found for courseId: " + courseId);
            return;
        }

        // Fetch played video progress for the course
        List<VideoProgress> playedProgress = videoProgressRepository.findAllByCourseId(courseId);

        // Map played videos to a quick lookup (e.g., contentId -> percentageCompleted)
        Map<Long, Float> playedProgressMap = playedProgress.stream()
                .collect(Collectors.toMap(VideoProgress::getCourseContentId, VideoProgress::getPercentageCompleted));

        // Calculate total progress, assuming 0% for unplayed videos
        float totalPercentage = 0f;
        for (CourseContent content : totalContents) {
            totalPercentage += playedProgressMap.getOrDefault(content.getCourseContentId(), 0f);
        }

        // Calculate average percentage
        float averagePercentage = totalPercentage / totalContents.size();

        // Update the progress for all rows related to the course
        playedProgress.forEach(progress -> {
            progress.setCourseTotalProgress(averagePercentage);
            videoProgressRepository.save(progress); // Save updated progress
        });

        System.out.println("Updated course progress to: " + averagePercentage + "% for courseId: " + courseId);
    }
}