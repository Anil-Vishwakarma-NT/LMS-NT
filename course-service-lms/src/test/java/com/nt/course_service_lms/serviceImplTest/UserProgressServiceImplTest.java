package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.converters.UserProgressConverter;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.entity.UserProgress;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.UserProgressRepository;
import com.nt.course_service_lms.service.serviceImpl.UserProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProgressServiceImplTest {

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private CourseContentRepository courseContentRepository;

    @Mock
    private UserProgressConverter userProgressConverter;

    @InjectMocks
    private UserProgressServiceImpl userProgressService;

    private UserProgressOutDTO progressDTO;
    private UserProgress progressEntity;

    @BeforeEach
    void setUp() {
        progressDTO = UserProgressOutDTO.builder()
                .userId(1L)
                .courseId(101L)
                .contentId(1001L)
                .contentType("video")
                .lastPosition(50)
                .contentCompletionPercentage(80.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        progressEntity = UserProgress.builder()
                .progressId(1L)
                .userId(1L)
                .courseId(101L)
                .contentId(1001L)
                .contentType("video")
                .lastPosition(40)
                .contentCompletionPercentage(60.0)
                .courseCompletionPercentage(60.0)
                .courseCompleted(false)
                .lastUpdated(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void testUpdateProgress_newRecordAndCompletionAbove95() {
        when(userProgressRepository.findProgressByUserIdAndContentId(1L, 1001L))
                .thenReturn(Optional.empty());
        when(userProgressConverter.toEntity(progressDTO)).thenReturn(progressEntity);
        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L))
                .thenReturn(Collections.singletonList(progressEntity));
        when(courseContentRepository.findByCourseId(101L))
                .thenReturn(Arrays.asList(new CourseContent(), new CourseContent())); // 2 contents

        userProgressService.updateProgress(progressDTO);

        verify(userProgressRepository, atLeastOnce()).save(any(UserProgress.class));
    }

    @Test
    void testUpdateProgress_existingRecord_courseCompletionLessThan95() {
        when(userProgressRepository.findProgressByUserIdAndContentId(1L, 1001L))
                .thenReturn(Optional.of(progressEntity));
        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L))
                .thenReturn(Collections.singletonList(progressEntity));
        when(courseContentRepository.findByCourseId(101L))
                .thenReturn(Collections.singletonList(new CourseContent())); // 1 content

        userProgressService.updateProgress(progressDTO);

        verify(userProgressRepository, atLeastOnce()).save(any(UserProgress.class));
        assertFalse(progressEntity.isCourseCompleted() && progressEntity.getFirstCompletedAt() != null);
    }

    @Test
    void testUpdateProgress_completionAbove95_setsFirstCompletedAtOnce() {
        progressDTO.setContentCompletionPercentage(100.0);
        progressEntity.setContentCompletionPercentage(100.0);

        when(userProgressRepository.findProgressByUserIdAndContentId(1L, 1001L))
                .thenReturn(Optional.of(progressEntity));
        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L))
                .thenReturn(Collections.singletonList(progressEntity));
        when(courseContentRepository.findByCourseId(101L))
                .thenReturn(Collections.singletonList(new CourseContent()));

        userProgressService.updateProgress(progressDTO);

        verify(userProgressRepository, atLeastOnce()).save(any(UserProgress.class));
    }

    @Test
    void testGetUserCourseContent_withProgress() {
        CourseContent content = CourseContent.builder()
                .courseContentId(1001L)
                .courseId(101L)
                .title("Intro")
                .description("desc")
                .resourceLink("link")
                .isActive(true)
                .build();

        UserProgress progress = UserProgress.builder()
                .contentId(1001L)
                .contentCompletionPercentage(85.0)
                .build();

        when(courseContentRepository.findByCourseId(101L)).thenReturn(Arrays.asList(content));
        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L)).thenReturn(Arrays.asList(progress));

        List<CourseContentInDTO> result = userProgressService.getUserCourseContent(1L, 101L);

        assertEquals(1, result.size());
        assertEquals("Intro", result.get(0).getTitle());
    }

    @Test
    void testGetUserCourseContent_noProgressFound() {
        CourseContent content = CourseContent.builder()
                .courseContentId(1001L)
                .courseId(101L)
                .title("Intro")
                .description("desc")
                .resourceLink("link")
                .isActive(true)
                .build();

        when(courseContentRepository.findByCourseId(101L)).thenReturn(Arrays.asList(content));
        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L)).thenReturn(Collections.emptyList());

        List<CourseContentInDTO> result = userProgressService.getUserCourseContent(1L, 101L);

        assertEquals(1, result.size());
    }

    @Test
    void testCalculateCourseCompletion_success() {
        UserProgress p1 = UserProgress.builder().contentCompletionPercentage(90).build();
        UserProgress p2 = UserProgress.builder().contentCompletionPercentage(70).build();

        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L)).thenReturn(Arrays.asList(p1, p2));
        when(courseContentRepository.findByCourseId(101L))
                .thenReturn(Arrays.asList(new CourseContent(), new CourseContent()));

        double result = userProgressService.calculateCourseCompletion(1L, 101L);

        assertEquals(80.0, result);
    }

    @Test
    void testCalculateCourseCompletion_zeroContent() {
        when(userProgressRepository.findProgressByUserIdAndCourseId(1L, 101L)).thenReturn(Collections.emptyList());
        when(courseContentRepository.findByCourseId(101L)).thenReturn(Collections.emptyList());

        double result = userProgressService.calculateCourseCompletion(1L, 101L);

        assertEquals(0.0, result);
    }

    @Test
    void testGetCourseProgressWithMeta_found() {
        LocalDateTime completedAt = LocalDateTime.now();
        UserProgress progress = UserProgress.builder()
                .courseCompletionPercentage(100.0)
                .firstCompletedAt(completedAt)
                .build();

        when(userProgressRepository.findSingleCourseProgress(1L, 101L)).thenReturn(progress);

        CourseProgressWithMetaDTO dto = userProgressService.getCourseProgressWithMeta(1L, 101L);

        assertEquals(100.0, dto.getCourseCompletionPercentage());
        assertEquals(completedAt, dto.getFirstCompletedAt());
    }

    @Test
    void testGetCourseProgressWithMeta_notFound() {
        when(userProgressRepository.findSingleCourseProgress(1L, 101L)).thenReturn(null);

        CourseProgressWithMetaDTO dto = userProgressService.getCourseProgressWithMeta(1L, 101L);

        assertEquals(0.0, dto.getCourseCompletionPercentage());
        assertNull(dto.getFirstCompletedAt());
    }

    @Test
    void testGetLastPosition_found() {
        when(userProgressRepository.findLastPosition(1L, 101L, 1001L)).thenReturn(90.0);

        int result = userProgressService.getLastPosition(1L, 101L, 1001L);

        assertEquals(90, result);
    }

    @Test
    void testGetLastPosition_null() {
        when(userProgressRepository.findLastPosition(1L, 101L, 1001L)).thenReturn(null);

        int result = userProgressService.getLastPosition(1L, 101L, 1001L);

        assertEquals(0, result);
    }

    @Test
    void testGetContentProgress_found() {
        UserProgress progress = UserProgress.builder().contentCompletionPercentage(88.0).build();
        when(userProgressRepository.findContentProgress(1L, 101L, 1001L)).thenReturn(progress);

        Double result = userProgressService.getContentProgress(1L, 101L, 1001L);

        assertEquals(88.0, result);
    }

    @Test
    void testGetContentProgress_notFound() {
        when(userProgressRepository.findContentProgress(1L, 101L, 1001L)).thenReturn(null);

        Double result = userProgressService.getContentProgress(1L, 101L, 1001L);

        assertEquals(0.0, result);
    }
}
