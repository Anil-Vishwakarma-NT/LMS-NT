package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.dto.UserProgressDTO;
import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.entity.UserProgress;
import com.example.course_service_lms.entity.CourseContent;
import com.example.course_service_lms.repository.UserProgressRepository;
import com.example.course_service_lms.repository.CourseContentRepository;
import com.example.course_service_lms.service.UserProgressService;
import com.example.course_service_lms.converters.UserProgressConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Enable logging
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Logging annotation
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final CourseContentRepository courseContentRepository;
    private final UserProgressConverter userProgressConverter;

    @Override
    public void updateProgress(UserProgressDTO progressDTO) {
        log.info("Received Progress Update: {}", progressDTO);

        Optional<UserProgress> progressOpt = userProgressRepository.findProgressByUserIdAndContentId(
                progressDTO.getUserId(), progressDTO.getContentId());

        UserProgress progress = progressOpt.orElseGet(() -> {
            log.info("No existing progress found for UserId: {}, ContentId: {}. Creating new entry.",
                    progressDTO.getUserId(), progressDTO.getContentId());
            return userProgressConverter.toEntity(progressDTO);
        });

        if (progressOpt.isPresent()) {
            log.info("Updating existing progress for UserId: {}, ContentId: {}",
                    progressDTO.getUserId(), progressDTO.getContentId());
            progress.setLastPosition(progressDTO.getLastPosition());
            progress.setContentCompletionPercentage(progressDTO.getContentCompletionPercentage());
            progress.setLastUpdated(LocalDateTime.now());
        }

        // **Step 1: Save Progress First**
        userProgressRepository.save(progress);
        log.info("Progress saved successfully for User {} in Course {}",
                progressDTO.getUserId(), progressDTO.getCourseId());

        // **Step 2: Compute Course Completion AFTER Saving**
        double courseCompletionPercentage = calculateCourseCompletion(progressDTO.getUserId(), progressDTO.getCourseId());
        log.info("Calculated Course Completion Percentage for Course {}: {}",
                progressDTO.getCourseId(), courseCompletionPercentage);

        // **Step 3: Update All Previous Progress Records**
        List<UserProgress> allProgressRecords = userProgressRepository.findProgressByUserIdAndCourseId(
                progressDTO.getUserId(), progressDTO.getCourseId());

        log.info("Updating all previous progress records with course completion {}", courseCompletionPercentage);

        allProgressRecords.forEach(record -> {
            record.setCourseCompletionPercentage(courseCompletionPercentage);
            record.setCourseCompleted(courseCompletionPercentage >= 100);
            userProgressRepository.save(record); // ✅ Ensure all records are updated
        });

        log.info("Updated Course Completion Status for all records.");
    }

    public List<CourseContentDTO> getUserCourseContent(int userId, long courseId) {
        log.info("Fetching Course Content for CourseId: {}", courseId);
        List<CourseContent> courseContents = courseContentRepository.findByCourseId(courseId);
        List<UserProgress> userProgressList = userProgressRepository.findProgressByUserIdAndCourseId(userId, courseId);

        log.info("Total Course Content items retrieved: {}", courseContents.size());
        log.info("Total User Progress records retrieved: {}", userProgressList.size());

        return courseContents.stream().map(content -> {
            Optional<UserProgress> progressOpt = userProgressList.stream()
                    .filter(p -> p.getContentId() == content.getCourseContentId())
                    .findFirst();

            log.info("Mapped Progress for CourseContentId {}: {}", content.getCourseContentId(),
                    progressOpt.map(UserProgress::getContentCompletionPercentage).orElse(0.0));

            return new CourseContentDTO(
                    content.getCourseId(),
                    content.getTitle(),
                    content.getDescription(),
                    content.getResourceLink(),
                    content.isActive()
            );
        }).toList();
    }

    private double calculateCourseCompletion(int userId, long courseId) {
        log.info("Calculating Course Completion for UserId: {}, CourseId: {}", userId, courseId);
        List<UserProgress> progressList = userProgressRepository.findProgressByUserIdAndCourseId(userId, courseId);
        int totalContents = courseContentRepository.findByCourseId(courseId).size();

        log.info("Total Course Contents: {}", totalContents);
        log.info("User Progress Records Retrieved: {}", progressList.size());

        double sumCompletion = progressList.stream().mapToDouble(UserProgress::getContentCompletionPercentage).sum();
        log.info("Total Completion Percentage Sum: {}", sumCompletion);

        double completionPercentage = totalContents > 0 ? (sumCompletion / totalContents) : 0;
        log.info("Final Computed Completion Percentage: {}", completionPercentage);

        return completionPercentage;
    }

    public Double getCourseProgress(int userId, int courseId) {
        UserProgress progressRecord = userProgressRepository.findSingleCourseProgress(userId, courseId);

        return (progressRecord != null) ? progressRecord.getCourseCompletionPercentage() : 0.0;
    }

    @Override
    public Integer getLastPosition(int userId, int courseId, int contentId) {
        log.info("Fetching last position for UserId: {}, CourseId: {}, ContentId: {}", userId, courseId, contentId);

        Double lastPosition = userProgressRepository.findLastPosition(userId, courseId, contentId);

        return (lastPosition != null) ? lastPosition.intValue() : 0;
    }

    public Double getContentProgress(int userId, int courseId, int contentId) {
        UserProgress progressRecord = userProgressRepository.findContentProgress(userId, courseId, contentId);

        return (progressRecord != null) ? progressRecord.getContentCompletionPercentage() : 0.0;
    }


}

