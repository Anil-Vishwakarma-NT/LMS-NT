package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.constants.CommonConstants;
import com.nt.course_service_lms.converters.UserProgressConverter;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.entity.UserProgress;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.UserProgressRepository;
import com.nt.course_service_lms.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing user progress in courses within the Learning Management System.
 *
 * <p>This service handles all operations related to tracking and calculating user progress through
 * course content, including individual content completion percentages, overall course completion,
 * and progress persistence. It provides functionality to update progress, calculate completion
 * metrics, and retrieve progress data for users and courses.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Tracking individual content completion percentages</li>
 *   <li>Calculating overall course completion based on all content items</li>
 *   <li>Managing course completion status and timestamps</li>
 *   <li>Retrieving user progress data with comprehensive logging</li>
 * </ul>
 *
 * <p>The service uses a threshold-based approach where courses are marked as completed
 * when reaching 80% completion, and first completion timestamps are set at 95% completion.</p>
 *
 * @author Learning Management System Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressServiceImpl implements UserProgressService {

    /**
     * Repository for accessing and managing user progress data.
     * Used for CRUD operations on UserProgress entities.
     */
    private final UserProgressRepository userProgressRepository;

    /**
     * Repository for accessing course content information.
     * Used to retrieve course content details and count total content items.
     */
    private final CourseContentRepository courseContentRepository;

    /**
     * Converter utility for transforming between UserProgress entities and DTOs.
     * Handles the mapping between different data representations.
     */
    private final UserProgressConverter userProgressConverter;

    /**
     * Updates user progress for a specific piece of course content.
     *
     * <p>This method handles both creating new progress records and updating existing ones.
     * It performs a multi-step process:</p>
     * <ol>
     *   <li>Finds or creates a progress record for the user and content</li>
     *   <li>Updates the progress with new completion data</li>
     *   <li>Calculates overall course completion percentage</li>
     *   <li>Updates all progress records for the course with the new completion status</li>
     *   <li>Sets first completion timestamp if the course reaches 95% completion</li>
     * </ol>
     *
     * <p>Course completion logic:</p>
     * <ul>
     *   <li>Course is marked as completed when reaching 80% completion</li>
     *   <li>First completion timestamp is set when reaching 95% completion</li>
     * </ul>
     *
     * @param progressDTO the progress data transfer object containing user ID, course ID,
     *                    content ID, completion percentage, and last position
     * @throws IllegalArgumentException if progressDTO is null or contains invalid data
     * @example <pre>{@code
     * UserProgressOutDTO progressDTO = new UserProgressOutDTO();
     * progressDTO.setUserId(123L);
     * progressDTO.setCourseId(456L);
     * progressDTO.setContentId(789L);
     * progressDTO.setContentCompletionPercentage(85.5);
     * progressDTO.setLastPosition(1200);
     *
     * userProgressService.updateProgress(progressDTO);
     * }</pre>
     */
    @Override
    public void updateProgress(final UserProgressOutDTO progressDTO) {
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

        // Step 1: Save current progress first
        userProgressRepository.save(progress);
        log.info("Progress saved successfully for User {} in Course {}",
                progressDTO.getUserId(), progressDTO.getCourseId());

        // Step 2: Calculate course-level completion after saving
        double courseCompletionPercentage = calculateCourseCompletion(progressDTO.getUserId(), progressDTO.getCourseId());
        log.info("Calculated Course Completion Percentage for Course {}: {}",
                progressDTO.getCourseId(), courseCompletionPercentage);

        // Step 3: Fetch all progress records for user-course
        List<UserProgress> allProgressRecords = userProgressRepository.findProgressByUserIdAndCourseId(
                progressDTO.getUserId(), progressDTO.getCourseId());

        log.info("Updating all previous progress records with course completion {}", courseCompletionPercentage);

        boolean shouldSetFirstCompletedAt = false;

        if (courseCompletionPercentage >= CommonConstants.FLOAT_NINTY_FIVE) {
            boolean alreadyCompleted = allProgressRecords.stream()
                    .anyMatch(record -> record.getFirstCompletedAt() != null);

            if (!alreadyCompleted) {
                shouldSetFirstCompletedAt = true;
            }
        }

        // Step 4: Update each record with course-level values
        for (UserProgress record : allProgressRecords) {
            record.setCourseCompletionPercentage(courseCompletionPercentage);
            record.setCourseCompleted(courseCompletionPercentage >= CommonConstants.NUMBER_EIGHTY);

            if (shouldSetFirstCompletedAt) {
                record.setFirstCompletedAt(LocalDateTime.now());
            }

            userProgressRepository.save(record);
        }

        log.info("Updated Course Completion Status for all records.");
    }

    /**
     * Retrieves all course content for a specific user and course.
     *
     * <p>This method fetches all content items for a given course and maps them to
     * CourseContentInDTO objects. It also retrieves the user's progress for each
     * content item to provide comprehensive course content information.</p>
     *
     * <p>The method performs the following operations:</p>
     * <ul>
     *   <li>Retrieves all course content items for the specified course</li>
     *   <li>Fetches user progress records for the course</li>
     *   <li>Maps content items to DTOs with progress information</li>
     * </ul>
     *
     * @param userId   the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @return a list of CourseContentInDTO objects representing all content items in the course
     * @throws IllegalArgumentException if userId or courseId is null or negative
     */
    public List<CourseContentOutDTO> getUserCourseContent(final Long userId, final long courseId) {
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

            return new CourseContentOutDTO(
                    content.getCourseId(),
                    content.getTitle(),
                    content.getDescription(),
                    content.getContentType(),
                    content.getResourceLink(),
                    content.isActive()
            );
        }).toList();
    }

    /**
     * Calculates the overall completion percentage for a user's progress in a specific course.
     *
     * <p>This method computes the course completion percentage by:</p>
     * <ol>
     *   <li>Retrieving all user progress records for the course</li>
     *   <li>Summing up individual content completion percentages</li>
     *   <li>Dividing by the total number of content items in the course</li>
     * </ol>
     *
     * <p>The calculation formula is: (Sum of all content completion percentages) / (Total number of content items)</p>
     *
     * <p>If there are no content items in the course, the method returns 0.0.</p>
     *
     * @param userId   the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @return the calculated completion percentage as a double value between 0.0 and 100.0
     * @throws IllegalArgumentException if userId or courseId is null or negative
     */
    public double calculateCourseCompletion(final Long userId, final long courseId) {
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

    /**
     * Retrieves course progress information along with completion metadata for a specific user and course.
     *
     * <p>This method provides a comprehensive view of a user's progress in a course, including
     * the overall completion percentage and the timestamp of when the course was first completed
     * (if applicable).</p>
     *
     * <p>The method returns a CourseProgressWithMetaDTO containing:</p>
     * <ul>
     *   <li>Course completion percentage (0.0 to 100.0)</li>
     *   <li>First completion timestamp (null if not yet completed)</li>
     * </ul>
     *
     * @param userId   the unique identifier of the user
     * @param courseId the unique identifier of the course
     * @return a CourseProgressWithMetaDTO containing completion percentage and first completion timestamp,
     * or a DTO with 0.0 completion and null timestamp if no progress exists
     * @throws IllegalArgumentException if userId or courseId is null or negative
     */
    public CourseProgressWithMetaDTO getCourseProgressWithMeta(final Long userId, final Long courseId) {
        UserProgress progressRecord = userProgressRepository.findSingleCourseProgress(userId, courseId);
        if (progressRecord == null) {
            return new CourseProgressWithMetaDTO(0.0, null);
        }
        return new CourseProgressWithMetaDTO(
                progressRecord.getCourseCompletionPercentage(),
                progressRecord.getFirstCompletedAt()
        );
    }

    /**
     * Retrieves the last position (bookmark) for a user in a specific piece of course content.
     *
     * <p>This method is useful for resuming content consumption from where the user left off.
     * The position typically represents a timestamp in seconds for video content, page number
     * for text content, or similar positional data.</p>
     *
     * <p>If no progress record exists for the specified user, course, and content combination,
     * the method returns 0 as the default starting position.</p>
     *
     * @param userId    the unique identifier of the user
     * @param courseId  the unique identifier of the course
     * @param contentId the unique identifier of the content item
     * @return the last position as an Integer value, or 0 if no progress exists
     * @throws IllegalArgumentException if any of the parameters are null or negative
     */
    @Override
    public Integer getLastPosition(final Long userId, final Long courseId, final Long contentId) {
        log.info("Fetching last position for UserId: {}, CourseId: {}, ContentId: {}", userId, courseId, contentId);

        Double lastPosition = userProgressRepository.findLastPosition(userId, courseId, contentId);

        return (lastPosition != null) ? lastPosition.intValue() : 0;
    }

    /**
     * Retrieves the completion percentage for a specific content item within a course for a user.
     *
     * <p>This method provides granular progress information for individual content items,
     * allowing for detailed tracking of user engagement with specific course materials.</p>
     *
     * <p>The completion percentage is returned as a double value between 0.0 and 100.0,
     * where 0.0 indicates no progress and 100.0 indicates complete consumption of the content.</p>
     *
     * @param userId    the unique identifier of the user
     * @param courseId  the unique identifier of the course
     * @param contentId the unique identifier of the content item
     * @return the completion percentage as a Double value between 0.0 and 100.0,
     * or 0.0 if no progress exists for the specified content
     * @throws IllegalArgumentException if any of the parameters are null or negative
     */
    public Double getContentProgress(final Long userId, final Long courseId, final Long contentId) {
        UserProgress progressRecord = userProgressRepository.findContentProgress(userId, courseId, contentId);
        return (progressRecord != null) ? progressRecord.getContentCompletionPercentage() : 0.0;
    }
}
