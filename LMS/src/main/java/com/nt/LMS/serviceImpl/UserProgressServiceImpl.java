package com.nt.LMS.serviceImpl;

import com.nt.LMS.dto.UserProgressDTO;
import com.nt.LMS.entities.UserProgress;
import com.nt.LMS.repository.UserProgressRepository;
import com.nt.LMS.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressRepository userProgressRepository;

    @Override
    public void updateProgress(UserProgressDTO progressDTO) {
        // Fetch existing progress or create a new entry if none exists
        UserProgress progress = userProgressRepository.findProgressByUserIdAndContentId(progressDTO.getUserId(), progressDTO.getContentId());

        if (progress == null) {
            // First interaction—create new progress entry
            progress = UserProgress.builder()
                    .userId(progressDTO.getUserId())
                    .contentId(progressDTO.getContentId())
                    .courseId(progressDTO.getCourseId()) // Added courseId
                    .contentType(progressDTO.getContentType())
                    .lastPosition(progressDTO.getLastPosition())
                    .contentCompletionPercentage(progressDTO.getContentCompletionPercentage())
                    .courseCompletionPercentage(progressDTO.getCourseCompletionPercentage()) // Ensure accurate progress update
                    .courseCompleted(progressDTO.isCourseCompleted())
                    .lastUpdated(LocalDateTime.now()) // Set current timestamp
                    .build();
        } else {
            // Update existing progress
            progress.setLastPosition(progressDTO.getLastPosition());
            progress.setContentCompletionPercentage(progressDTO.getContentCompletionPercentage());
            progress.setCourseCompletionPercentage(progressDTO.getCourseCompletionPercentage());
            progress.setCourseCompleted(progressDTO.isCourseCompleted());
            progress.setLastUpdated(LocalDateTime.now()); // Ensure timestamp consistency
        }

        userProgressRepository.save(progress);
    }
}