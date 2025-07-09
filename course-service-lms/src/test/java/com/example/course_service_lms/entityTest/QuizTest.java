package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.Quiz;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class QuizTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Quiz quiz = new Quiz();
        LocalDateTime now = LocalDateTime.now();

        quiz.setQuizId(1L);
        quiz.setParentType("course");
        quiz.setParentId(101L);
        quiz.setTitle("Java Basics");
        quiz.setDescription("Intro to Java");
        quiz.setTimeLimit(60);
        quiz.setAttemptsAllowed(2);
        quiz.setPassingScore(new BigDecimal("75.50"));
        quiz.setRandomizeQuestions(true);
        quiz.setShowResults(false);
        quiz.setIsActive(false);
        quiz.setCreatedBy(1001);
        quiz.setCreatedAt(now);
        quiz.setUpdatedAt(now);

        assertThat(quiz.getQuizId()).isEqualTo(1L);
        assertThat(quiz.getParentType()).isEqualTo("course");
        assertThat(quiz.getParentId()).isEqualTo(101L);
        assertThat(quiz.getTitle()).isEqualTo("Java Basics");
        assertThat(quiz.getDescription()).isEqualTo("Intro to Java");
        assertThat(quiz.getTimeLimit()).isEqualTo(60);
        assertThat(quiz.getAttemptsAllowed()).isEqualTo(2);
        assertThat(quiz.getPassingScore()).isEqualTo(new BigDecimal("75.50"));
        assertThat(quiz.getRandomizeQuestions()).isTrue();
        assertThat(quiz.getShowResults()).isFalse();
        assertThat(quiz.getIsActive()).isFalse();
        assertThat(quiz.getCreatedBy()).isEqualTo(1001);
        assertThat(quiz.getCreatedAt()).isEqualTo(now);
        assertThat(quiz.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 12, 0);
        BigDecimal passingScore = new BigDecimal("85.25");

        Quiz quiz = new Quiz(
                2L,
                "bundle",
                202L,
                "Advanced Quiz",
                "Deep dive",
                45,
                3,
                passingScore,
                false,
                true,
                true,
                2002,
                createdAt,
                updatedAt
        );

        assertThat(quiz.getQuizId()).isEqualTo(2L);
        assertThat(quiz.getParentType()).isEqualTo("bundle");
        assertThat(quiz.getParentId()).isEqualTo(202L);
        assertThat(quiz.getTitle()).isEqualTo("Advanced Quiz");
        assertThat(quiz.getDescription()).isEqualTo("Deep dive");
        assertThat(quiz.getTimeLimit()).isEqualTo(45);
        assertThat(quiz.getAttemptsAllowed()).isEqualTo(3);
        assertThat(quiz.getPassingScore()).isEqualTo(passingScore);
        assertThat(quiz.getRandomizeQuestions()).isFalse();
        assertThat(quiz.getShowResults()).isTrue();
        assertThat(quiz.getIsActive()).isTrue();
        assertThat(quiz.getCreatedBy()).isEqualTo(2002);
        assertThat(quiz.getCreatedAt()).isEqualTo(createdAt);
        assertThat(quiz.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        LocalDateTime time = LocalDateTime.now();
        Quiz q1 = new Quiz(1L, "course", 10L, "Quiz", "Desc", 30, 1,
                new BigDecimal("50.00"), true, true, true, 1, time, time);
        Quiz q2 = new Quiz(1L, "course", 10L, "Quiz", "Desc", 30, 1,
                new BigDecimal("50.00"), true, true, true, 1, time, time);

        assertThat(q1).isEqualTo(q2);
        assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
    }

    @Test
    void testEquals_DifferentValues() {
        LocalDateTime time = LocalDateTime.now();
        Quiz q1 = new Quiz(1L, "course", 10L, "Quiz", "Desc", 30, 1,
                new BigDecimal("50.00"), true, true, true, 1, time, time);
        Quiz q2 = new Quiz(2L, "bundle", 20L, "Different", "Other", 60, 2,
                new BigDecimal("80.00"), false, false, false, 2, time, time);

        assertThat(q1).isNotEqualTo(q2);
        assertThat(q1.hashCode()).isNotEqualTo(q2.hashCode());
    }

    @Test
    void testEquals_NullAndDifferentClass() {
        Quiz quiz = new Quiz();
        assertThat(quiz).isNotEqualTo(null);
        assertThat(quiz).isNotEqualTo("not a quiz");
    }

    @Test
    void testEquals_HashCode_AfterFieldMutation() {
        LocalDateTime time = LocalDateTime.now();
        Quiz q1 = new Quiz(1L, "course", 10L, "Quiz", "Desc", 30, 1,
                new BigDecimal("50.00"), true, true, true, 1, time, time);
        Quiz q2 = new Quiz(1L, "course", 10L, "Quiz", "Desc", 30, 1,
                new BigDecimal("50.00"), true, true, true, 1, time, time);

        assertThat(q1).isEqualTo(q2);

        q2.setTitle("Updated Title");

        assertThat(q1).isNotEqualTo(q2);
        assertThat(q1.hashCode()).isNotEqualTo(q2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime time = LocalDateTime.now();
        Quiz quiz = new Quiz(5L, "course", 77L, "Quiz Title", "Quiz Desc", 20, 2,
                new BigDecimal("65.00"), false, true, false, 111, time, time);

        String str = quiz.toString();

        assertThat(str).contains("quizId=5");
        assertThat(str).contains("parentType=course");
        assertThat(str).contains("parentId=77");
        assertThat(str).contains("title=Quiz Title");
        assertThat(str).contains("description=Quiz Desc");
        assertThat(str).contains("timeLimit=20");
        assertThat(str).contains("attemptsAllowed=2");
        assertThat(str).contains("passingScore=65.00");
        assertThat(str).contains("randomizeQuestions=false");
        assertThat(str).contains("showResults=true");
        assertThat(str).contains("isActive=false");
        assertThat(str).contains("createdBy=111");
        assertThat(str).contains("createdAt=");
        assertThat(str).contains("updatedAt=");
    }
}

