package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.UserProgress;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserProgressTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress progress = new UserProgress();
        progress.setProgressId(1L);
        progress.setUserId(101L);
        progress.setContentId(201L);
        progress.setCourseId(301L);
        progress.setContentType("pdf");
        progress.setLastPosition(12.5);
        progress.setContentCompletionPercentage(85.5);
        progress.setCourseCompletionPercentage(60.0);
        progress.setCourseCompleted(true);
        progress.setLastUpdated(now);
        progress.setFirstCompletedAt(now);

        assertThat(progress.getProgressId()).isEqualTo(1L);
        assertThat(progress.getUserId()).isEqualTo(101L);
        assertThat(progress.getContentId()).isEqualTo(201L);
        assertThat(progress.getCourseId()).isEqualTo(301L);
        assertThat(progress.getContentType()).isEqualTo("pdf");
        assertThat(progress.getLastPosition()).isEqualTo(12.5);
        assertThat(progress.getContentCompletionPercentage()).isEqualTo(85.5);
        assertThat(progress.getCourseCompletionPercentage()).isEqualTo(60.0);
        assertThat(progress.isCourseCompleted()).isTrue();
        assertThat(progress.getLastUpdated()).isEqualTo(now);
        assertThat(progress.getFirstCompletedAt()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructorWithSetters() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress progress = new UserProgress();
        progress.setProgressId(2L);
        progress.setUserId(202L);
        progress.setContentId(302L);
        progress.setCourseId(402L);
        progress.setContentType("video");
        progress.setLastPosition(45.0);
        progress.setContentCompletionPercentage(90.0);
        progress.setCourseCompletionPercentage(100.0);
        progress.setCourseCompleted(true);
        progress.setLastUpdated(now);
        progress.setFirstCompletedAt(now);

        assertThat(progress.getProgressId()).isEqualTo(2L);
        assertThat(progress.getUserId()).isEqualTo(202L);
        assertThat(progress.getContentId()).isEqualTo(302L);
        assertThat(progress.getCourseId()).isEqualTo(402L);
        assertThat(progress.getContentType()).isEqualTo("video");
        assertThat(progress.getLastPosition()).isEqualTo(45.0);
        assertThat(progress.getContentCompletionPercentage()).isEqualTo(90.0);
        assertThat(progress.getCourseCompletionPercentage()).isEqualTo(100.0);
        assertThat(progress.isCourseCompleted()).isTrue();
        assertThat(progress.getLastUpdated()).isEqualTo(now);
        assertThat(progress.getFirstCompletedAt()).isEqualTo(now);
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress p1 = new UserProgress();
        p1.setProgressId(1L);
        p1.setUserId(10L);
        p1.setContentId(20L);
        p1.setCourseId(30L);
        p1.setContentType("pdf");
        p1.setLastPosition(5.0);
        p1.setContentCompletionPercentage(10.0);
        p1.setCourseCompletionPercentage(15.0);
        p1.setCourseCompleted(true);
        p1.setLastUpdated(now);
        p1.setFirstCompletedAt(now);

        UserProgress p2 = new UserProgress();
        p2.setProgressId(1L);
        p2.setUserId(10L);
        p2.setContentId(20L);
        p2.setCourseId(30L);
        p2.setContentType("pdf");
        p2.setLastPosition(5.0);
        p2.setContentCompletionPercentage(10.0);
        p2.setCourseCompletionPercentage(15.0);
        p2.setCourseCompleted(true);
        p2.setLastUpdated(now);
        p2.setFirstCompletedAt(now);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress p1 = new UserProgress();
        p1.setProgressId(1L);
        p1.setUserId(10L);
        p1.setContentId(20L);
        p1.setCourseId(30L);
        p1.setContentType("pdf");
        p1.setLastPosition(5.0);
        p1.setContentCompletionPercentage(10.0);
        p1.setCourseCompletionPercentage(15.0);
        p1.setCourseCompleted(true);
        p1.setLastUpdated(now);
        p1.setFirstCompletedAt(now);

        UserProgress p2 = new UserProgress();
        p2.setProgressId(2L);
        p2.setUserId(11L);
        p2.setContentId(21L);
        p2.setCourseId(31L);
        p2.setContentType("video");
        p2.setLastPosition(6.0);
        p2.setContentCompletionPercentage(11.0);
        p2.setCourseCompletionPercentage(16.0);
        p2.setCourseCompleted(false);
        p2.setLastUpdated(now);
        p2.setFirstCompletedAt(null);

        assertThat(p1).isNotEqualTo(p2);
        assertThat(p1.hashCode()).isNotEqualTo(p2.hashCode());
    }

    @Test
    void testEquals_SameReference() {
        UserProgress progress = new UserProgress();
        assertThat(progress).isEqualTo(progress);
    }

    @Test
    void testEquals_NullAndDifferentType() {
        UserProgress progress = new UserProgress();
        assertThat(progress).isNotEqualTo(null);
        assertThat(progress).isNotEqualTo("not a UserProgress");
    }

    @Test
    void testEqualsAfterMutation() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress p1 = new UserProgress();
        p1.setProgressId(1L);
        p1.setUserId(10L);
        p1.setContentId(20L);
        p1.setCourseId(30L);
        p1.setContentType("pdf");
        p1.setLastPosition(5.0);
        p1.setContentCompletionPercentage(10.0);
        p1.setCourseCompletionPercentage(15.0);
        p1.setCourseCompleted(false);
        p1.setLastUpdated(now);
        p1.setFirstCompletedAt(null);

        UserProgress p2 = new UserProgress();
        p2.setProgressId(1L);
        p2.setUserId(10L);
        p2.setContentId(20L);
        p2.setCourseId(30L);
        p2.setContentType("pdf");
        p2.setLastPosition(5.0);
        p2.setContentCompletionPercentage(10.0);
        p2.setCourseCompletionPercentage(15.0);
        p2.setCourseCompleted(false);
        p2.setLastUpdated(now);
        p2.setFirstCompletedAt(null);

        assertThat(p1).isEqualTo(p2);

        p2.setCourseCompleted(true);

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress progress = UserProgress.builder()
                .progressId(100L)
                .userId(200L)
                .contentId(300L)
                .courseId(400L)
                .contentType("video")
                .lastPosition(123.4)
                .contentCompletionPercentage(70.0)
                .courseCompletionPercentage(55.5)
                .courseCompleted(true)
                .lastUpdated(now)
                .firstCompletedAt(now)
                .build();

        assertThat(progress.getProgressId()).isEqualTo(100L);
        assertThat(progress.getUserId()).isEqualTo(200L);
        assertThat(progress.getContentId()).isEqualTo(300L);
        assertThat(progress.getCourseId()).isEqualTo(400L);
        assertThat(progress.getContentType()).isEqualTo("video");
        assertThat(progress.getLastPosition()).isEqualTo(123.4);
        assertThat(progress.getContentCompletionPercentage()).isEqualTo(70.0);
        assertThat(progress.getCourseCompletionPercentage()).isEqualTo(55.5);
        assertThat(progress.isCourseCompleted()).isTrue();
        assertThat(progress.getLastUpdated()).isEqualTo(now);
        assertThat(progress.getFirstCompletedAt()).isEqualTo(now);
    }

    @Test
    void testDefaultValues() {
        UserProgress progress = new UserProgress();

        assertThat(progress.getLastPosition()).isEqualTo(0.0);
        assertThat(progress.getContentCompletionPercentage()).isEqualTo(0.0);
        assertThat(progress.getCourseCompletionPercentage()).isEqualTo(0.0);
        assertThat(progress.isCourseCompleted()).isFalse();
        assertThat(progress.getLastUpdated()).isNotNull();
    }
}

