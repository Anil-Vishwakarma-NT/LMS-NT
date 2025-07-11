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
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress progress = new UserProgress(
                2L, 202L, 302L, 402L, "video",
                45.0, 90.0, 100.0,
                true, now, now
        );

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

        UserProgress p1 = new UserProgress(1L, 10L, 20L, 30L, "pdf",
                5.0, 10.0, 15.0, true, now, now);

        UserProgress p2 = new UserProgress(1L, 10L, 20L, 30L, "pdf",
                5.0, 10.0, 15.0, true, now, now);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime now = LocalDateTime.now();

        UserProgress p1 = new UserProgress(1L, 10L, 20L, 30L, "pdf",
                5.0, 10.0, 15.0, true, now, now);

        UserProgress p2 = new UserProgress(2L, 11L, 21L, 31L, "video",
                6.0, 11.0, 16.0, false, now, null);

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

        UserProgress p1 = new UserProgress(1L, 10L, 20L, 30L, "pdf", 5.0, 10.0, 15.0, false, now, null);
        UserProgress p2 = new UserProgress(1L, 10L, 20L, 30L, "pdf", 5.0, 10.0, 15.0, false, now, null);

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

