package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.Quiz;
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
    void testAllFieldsWithSetters() {
        Quiz quiz = new Quiz();
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 12, 0);
        BigDecimal passingScore = new BigDecimal("85.25");

        quiz.setQuizId(2L);
        quiz.setParentType("bundle");
        quiz.setParentId(202L);
        quiz.setTitle("Advanced Quiz");
        quiz.setDescription("Deep dive");
        quiz.setTimeLimit(45);
        quiz.setAttemptsAllowed(3);
        quiz.setPassingScore(passingScore);
        quiz.setRandomizeQuestions(false);
        quiz.setShowResults(true);
        quiz.setIsActive(true);
        quiz.setCreatedBy(2002);
        quiz.setCreatedAt(createdAt);
        quiz.setUpdatedAt(updatedAt);

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
    void testEqualsAndHashCode_SameValuesWithSetters() {
        LocalDateTime time = LocalDateTime.now();

        Quiz q1 = new Quiz();
        q1.setQuizId(1L);
        q1.setParentType("course");
        q1.setParentId(10L);
        q1.setTitle("Quiz");
        q1.setDescription("Desc");
        q1.setTimeLimit(30);
        q1.setAttemptsAllowed(1);
        q1.setPassingScore(new BigDecimal("50.00"));
        q1.setRandomizeQuestions(true);
        q1.setShowResults(true);
        q1.setIsActive(true);
        q1.setCreatedBy(1);
        q1.setCreatedAt(time);
        q1.setUpdatedAt(time);

        Quiz q2 = new Quiz();
        q2.setQuizId(1L);
        q2.setParentType("course");
        q2.setParentId(10L);
        q2.setTitle("Quiz");
        q2.setDescription("Desc");
        q2.setTimeLimit(30);
        q2.setAttemptsAllowed(1);
        q2.setPassingScore(new BigDecimal("50.00"));
        q2.setRandomizeQuestions(true);
        q2.setShowResults(true);
        q2.setIsActive(true);
        q2.setCreatedBy(1);
        q2.setCreatedAt(time);
        q2.setUpdatedAt(time);

        assertThat(q1).isEqualTo(q2);
        assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
    }

    @Test
    void testEquals_DifferentValuesWithSetters() {
        LocalDateTime time = LocalDateTime.now();

        Quiz q1 = new Quiz();
        q1.setQuizId(1L);
        q1.setParentType("course");
        q1.setParentId(10L);
        q1.setTitle("Quiz");
        q1.setDescription("Desc");
        q1.setTimeLimit(30);
        q1.setAttemptsAllowed(1);
        q1.setPassingScore(new BigDecimal("50.00"));
        q1.setRandomizeQuestions(true);
        q1.setShowResults(true);
        q1.setIsActive(true);
        q1.setCreatedBy(1);
        q1.setCreatedAt(time);
        q1.setUpdatedAt(time);

        Quiz q2 = new Quiz();
        q2.setQuizId(2L);
        q2.setParentType("bundle");
        q2.setParentId(20L);
        q2.setTitle("Different");
        q2.setDescription("Other");
        q2.setTimeLimit(60);
        q2.setAttemptsAllowed(2);
        q2.setPassingScore(new BigDecimal("80.00"));
        q2.setRandomizeQuestions(false);
        q2.setShowResults(false);
        q2.setIsActive(false);
        q2.setCreatedBy(2);
        q2.setCreatedAt(time);
        q2.setUpdatedAt(time);

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
    void testEquals_HashCode_AfterFieldMutationWithSetters() {
        LocalDateTime time = LocalDateTime.now();

        Quiz q1 = new Quiz();
        q1.setQuizId(1L);
        q1.setParentType("course");
        q1.setParentId(10L);
        q1.setTitle("Quiz");
        q1.setDescription("Desc");
        q1.setTimeLimit(30);
        q1.setAttemptsAllowed(1);
        q1.setPassingScore(new BigDecimal("50.00"));
        q1.setRandomizeQuestions(true);
        q1.setShowResults(true);
        q1.setIsActive(true);
        q1.setCreatedBy(1);
        q1.setCreatedAt(time);
        q1.setUpdatedAt(time);

        Quiz q2 = new Quiz();
        q2.setQuizId(1L);
        q2.setParentType("course");
        q2.setParentId(10L);
        q2.setTitle("Quiz");
        q2.setDescription("Desc");
        q2.setTimeLimit(30);
        q2.setAttemptsAllowed(1);
        q2.setPassingScore(new BigDecimal("50.00"));
        q2.setRandomizeQuestions(true);
        q2.setShowResults(true);
        q2.setIsActive(true);
        q2.setCreatedBy(1);
        q2.setCreatedAt(time);
        q2.setUpdatedAt(time);

        assertThat(q1).isEqualTo(q2);

        q2.setTitle("Updated Title");

        assertThat(q1).isNotEqualTo(q2);
        assertThat(q1.hashCode()).isNotEqualTo(q2.hashCode());
    }

    @Test
    void testToStringContainsFieldsWithSetters() {
        LocalDateTime time = LocalDateTime.now();

        Quiz quiz = new Quiz();
        quiz.setQuizId(5L);
        quiz.setParentType("course");
        quiz.setParentId(77L);
        quiz.setTitle("Quiz Title");
        quiz.setDescription("Quiz Desc");
        quiz.setTimeLimit(20);
        quiz.setAttemptsAllowed(2);
        quiz.setPassingScore(new BigDecimal("65.00"));
        quiz.setRandomizeQuestions(false);
        quiz.setShowResults(true);
        quiz.setIsActive(false);
        quiz.setCreatedBy(111);
        quiz.setCreatedAt(time);
        quiz.setUpdatedAt(time);

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

    @Test
    void testSettingAllFields() {
        Quiz quiz = new Quiz();
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        quiz.setQuizId(6L);
        quiz.setParentType("bundle");
        quiz.setParentId(60L);
        quiz.setTitle("Complete Quiz");
        quiz.setDescription("Full details");
        quiz.setTimeLimit(90);
        quiz.setAttemptsAllowed(5);
        quiz.setPassingScore(new BigDecimal("88.50"));
        quiz.setRandomizeQuestions(true);
        quiz.setShowResults(false);
        quiz.setIsActive(false);
        quiz.setCreatedBy(1234);
        quiz.setCreatedAt(now);
        quiz.setUpdatedAt(now);

        assertThat(quiz.getDescription()).isEqualTo("Full details");
        assertThat(quiz.getPassingScore()).isEqualTo(new BigDecimal("88.50"));
        assertThat(quiz.getTimeLimit()).isEqualTo(90);
        assertThat(quiz.getCreatedBy()).isEqualTo(1234);
        assertThat(quiz.getCreatedAt()).isEqualTo(now);
        assertThat(quiz.getUpdatedAt()).isEqualTo(now);
    }

}

