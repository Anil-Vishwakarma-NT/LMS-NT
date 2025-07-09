package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.QuizAttempt;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class QuizAttemptTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        QuizAttempt attempt = new QuizAttempt();
        LocalDateTime now = LocalDateTime.now();

        attempt.setQuizAttemptId(10L);
        attempt.setAttempt(2L);
        attempt.setQuizId(101L);
        attempt.setUserId(202L);
        attempt.setStartedAt(now);
        attempt.setFinishedAt(now.plusMinutes(10));
        attempt.setScoreDetails("{\"score\":90}");
        attempt.setStatus("COMPLETED");
        attempt.setCreatedAt(now);
        attempt.setUpdatedAt(now);

        assertThat(attempt.getQuizAttemptId()).isEqualTo(10L);
        assertThat(attempt.getAttempt()).isEqualTo(2L);
        assertThat(attempt.getQuizId()).isEqualTo(101L);
        assertThat(attempt.getUserId()).isEqualTo(202L);
        assertThat(attempt.getStartedAt()).isEqualTo(now);
        assertThat(attempt.getFinishedAt()).isEqualTo(now.plusMinutes(10));
        assertThat(attempt.getScoreDetails()).isEqualTo("{\"score\":90}");
        assertThat(attempt.getStatus()).isEqualTo("COMPLETED");
        assertThat(attempt.getCreatedAt()).isEqualTo(now);
        assertThat(attempt.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusMinutes(5);

        QuizAttempt attempt = new QuizAttempt(
                1L,
                1L,
                100L,
                200L,
                now,
                later,
                "{\"score\":85}",
                "IN_PROGRESS",
                now,
                later
        );

        assertThat(attempt.getQuizAttemptId()).isEqualTo(1L);
        assertThat(attempt.getAttempt()).isEqualTo(1L);
        assertThat(attempt.getQuizId()).isEqualTo(100L);
        assertThat(attempt.getUserId()).isEqualTo(200L);
        assertThat(attempt.getStartedAt()).isEqualTo(now);
        assertThat(attempt.getFinishedAt()).isEqualTo(later);
        assertThat(attempt.getScoreDetails()).isEqualTo("{\"score\":85}");
        assertThat(attempt.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(attempt.getCreatedAt()).isEqualTo(now);
        assertThat(attempt.getUpdatedAt()).isEqualTo(later);
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        LocalDateTime time = LocalDateTime.now();

        QuizAttempt a1 = new QuizAttempt(
                1L, 1L, 100L, 200L, time, time.plusMinutes(1),
                "details", "COMPLETED", time, time
        );
        QuizAttempt a2 = new QuizAttempt(
                1L, 1L, 100L, 200L, time, time.plusMinutes(1),
                "details", "COMPLETED", time, time
        );

        assertThat(a1).isEqualTo(a2);
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime time = LocalDateTime.now();

        QuizAttempt a1 = new QuizAttempt(1L, 1L, 100L, 200L, time, null, "details", "COMPLETED", time, time);
        QuizAttempt a2 = new QuizAttempt(2L, 2L, 101L, 201L, time, null, "diff", "IN_PROGRESS", time, time);

        assertThat(a1).isNotEqualTo(a2);
        assertThat(a1.hashCode()).isNotEqualTo(a2.hashCode());
    }

    @Test
    void testEquals_SameReference() {
        QuizAttempt attempt = new QuizAttempt();
        assertThat(attempt).isEqualTo(attempt);
    }

    @Test
    void testEquals_NullAndDifferentType() {
        QuizAttempt attempt = new QuizAttempt();
        assertThat(attempt).isNotEqualTo(null);
        assertThat(attempt).isNotEqualTo("Not a QuizAttempt");
    }

    @Test
    void testEqualsAndHashCode_AfterFieldMutation() {
        LocalDateTime now = LocalDateTime.now();

        QuizAttempt a1 = new QuizAttempt(1L, 1L, 100L, 200L, now, null, "details", "COMPLETED", now, now);
        QuizAttempt a2 = new QuizAttempt(1L, 1L, 100L, 200L, now, null, "details", "COMPLETED", now, now);

        assertThat(a1).isEqualTo(a2);

        a2.setStatus("IN_PROGRESS");

        assertThat(a1).isNotEqualTo(a2);
        assertThat(a1.hashCode()).isNotEqualTo(a2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();
        QuizAttempt attempt = new QuizAttempt(5L, 3L, 100L, 300L, now, now.plusMinutes(10),
                "details", "COMPLETED", now, now);

        String str = attempt.toString();

        assertThat(str).contains("quizAttemptId=5");
        assertThat(str).contains("attempt=3");
        assertThat(str).contains("quizId=100");
        assertThat(str).contains("userId=300");
        assertThat(str).contains("scoreDetails=details");
        assertThat(str).contains("status=COMPLETED");
    }

    @Test
    void testNullableFieldsAllowNulls() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setFinishedAt(null);
        attempt.setScoreDetails(null);

        assertThat(attempt.getFinishedAt()).isNull();
        assertThat(attempt.getScoreDetails()).isNull();
    }
}

