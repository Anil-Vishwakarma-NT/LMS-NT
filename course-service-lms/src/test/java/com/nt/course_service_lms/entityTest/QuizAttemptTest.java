package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.QuizAttempt;
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
    void testAllFieldsViaSetters() {
        QuizAttempt attempt = new QuizAttempt();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusMinutes(5);

        attempt.setQuizAttemptId(1L);
        attempt.setAttempt(1L);
        attempt.setQuizId(100L);
        attempt.setUserId(200L);
        attempt.setStartedAt(now);
        attempt.setFinishedAt(later);
        attempt.setScoreDetails("{\"score\":85}");
        attempt.setStatus("IN_PROGRESS");
        attempt.setCreatedAt(now);
        attempt.setUpdatedAt(later);

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
    void testBuilderSetsAllFields() {
        LocalDateTime now = LocalDateTime.now();

        QuizAttempt attempt = QuizAttempt.builder()
                .quizAttemptId(99L)
                .attempt(2L)
                .quizId(300L)
                .userId(400L)
                .startedAt(now)
                .finishedAt(now.plusMinutes(30))
                .scoreDetails("{\"score\":100}")
                .status("COMPLETED")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(attempt.getQuizAttemptId()).isEqualTo(99L);
        assertThat(attempt.getAttempt()).isEqualTo(2L);
        assertThat(attempt.getQuizId()).isEqualTo(300L);
        assertThat(attempt.getUserId()).isEqualTo(400L);
        assertThat(attempt.getStartedAt()).isEqualTo(now);
        assertThat(attempt.getFinishedAt()).isEqualTo(now.plusMinutes(30));
        assertThat(attempt.getScoreDetails()).isEqualTo("{\"score\":100}");
        assertThat(attempt.getStatus()).isEqualTo("COMPLETED");
        assertThat(attempt.getCreatedAt()).isEqualTo(now);
        assertThat(attempt.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testStartedAtHasDefaultValue() {
        QuizAttempt attempt = new QuizAttempt();
        assertThat(attempt.getStartedAt()).isNotNull();
    }


    @Test
    void testEqualsAndHashCode_SameValues() {
        LocalDateTime time = LocalDateTime.now();

        QuizAttempt a1 = new QuizAttempt();
        a1.setQuizAttemptId(1L);
        a1.setAttempt(1L);
        a1.setQuizId(100L);
        a1.setUserId(200L);
        a1.setStartedAt(time);
        a1.setFinishedAt(time.plusMinutes(1));
        a1.setScoreDetails("details");
        a1.setStatus("COMPLETED");
        a1.setCreatedAt(time);
        a1.setUpdatedAt(time);

        QuizAttempt a2 = new QuizAttempt();
        a2.setQuizAttemptId(1L);
        a2.setAttempt(1L);
        a2.setQuizId(100L);
        a2.setUserId(200L);
        a2.setStartedAt(time);
        a2.setFinishedAt(time.plusMinutes(1));
        a2.setScoreDetails("details");
        a2.setStatus("COMPLETED");
        a2.setCreatedAt(time);
        a2.setUpdatedAt(time);

        assertThat(a1).isEqualTo(a2);
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime time = LocalDateTime.now();

        QuizAttempt a1 = new QuizAttempt();
        a1.setQuizAttemptId(1L);
        a1.setAttempt(1L);
        a1.setQuizId(100L);
        a1.setUserId(200L);
        a1.setStartedAt(time);
        a1.setFinishedAt(null);
        a1.setScoreDetails("details");
        a1.setStatus("COMPLETED");
        a1.setCreatedAt(time);
        a1.setUpdatedAt(time);

        QuizAttempt a2 = new QuizAttempt();
        a2.setQuizAttemptId(2L);
        a2.setAttempt(2L);
        a2.setQuizId(101L);
        a2.setUserId(201L);
        a2.setStartedAt(time);
        a2.setFinishedAt(null);
        a2.setScoreDetails("diff");
        a2.setStatus("IN_PROGRESS");
        a2.setCreatedAt(time);
        a2.setUpdatedAt(time);

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

        QuizAttempt a1 = new QuizAttempt();
        a1.setQuizAttemptId(1L);
        a1.setAttempt(1L);
        a1.setQuizId(100L);
        a1.setUserId(200L);
        a1.setStartedAt(now);
        a1.setScoreDetails("details");
        a1.setStatus("COMPLETED");
        a1.setCreatedAt(now);
        a1.setUpdatedAt(now);

        QuizAttempt a2 = new QuizAttempt();
        a2.setQuizAttemptId(1L);
        a2.setAttempt(1L);
        a2.setQuizId(100L);
        a2.setUserId(200L);
        a2.setStartedAt(now);
        a2.setScoreDetails("details");
        a2.setStatus("COMPLETED");
        a2.setCreatedAt(now);
        a2.setUpdatedAt(now);

        assertThat(a1).isEqualTo(a2);

        a2.setStatus("IN_PROGRESS");

        assertThat(a1).isNotEqualTo(a2);
        assertThat(a1.hashCode()).isNotEqualTo(a2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();
        QuizAttempt attempt = new QuizAttempt();

        attempt.setQuizAttemptId(5L);
        attempt.setAttempt(3L);
        attempt.setQuizId(100L);
        attempt.setUserId(300L);
        attempt.setStartedAt(now);
        attempt.setFinishedAt(now.plusMinutes(10));
        attempt.setScoreDetails("details");
        attempt.setStatus("COMPLETED");
        attempt.setCreatedAt(now);
        attempt.setUpdatedAt(now);

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

