package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.UserResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResponseTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal points = new BigDecimal("4.75");

        UserResponse response = new UserResponse();
        response.setResponseId(1L);
        response.setUserId(100L);
        response.setQuizId(200L);
        response.setQuestionId(300L);
        response.setAttempt(1L);
        response.setUserAnswer("B");
        response.setIsCorrect(true);
        response.setPointsEarned(points);
        response.setAnsweredAt(now);

        assertThat(response.getResponseId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getQuizId()).isEqualTo(200L);
        assertThat(response.getQuestionId()).isEqualTo(300L);
        assertThat(response.getAttempt()).isEqualTo(1L);
        assertThat(response.getUserAnswer()).isEqualTo("B");
        assertThat(response.getIsCorrect()).isTrue();
        assertThat(response.getPointsEarned()).isEqualByComparingTo(points);
        assertThat(response.getAnsweredAt()).isEqualTo(now);
    }

    @Test
    void testAllFieldsWithSetters() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal points = new BigDecimal("2.50");

        UserResponse response = new UserResponse();
        response.setResponseId(2L);
        response.setUserId(101L);
        response.setQuizId(201L);
        response.setQuestionId(301L);
        response.setAttempt(2L);
        response.setUserAnswer("D");
        response.setIsCorrect(false);
        response.setPointsEarned(points);
        response.setAnsweredAt(now);

        assertThat(response.getResponseId()).isEqualTo(2L);
        assertThat(response.getUserId()).isEqualTo(101L);
        assertThat(response.getQuizId()).isEqualTo(201L);
        assertThat(response.getQuestionId()).isEqualTo(301L);
        assertThat(response.getAttempt()).isEqualTo(2L);
        assertThat(response.getUserAnswer()).isEqualTo("D");
        assertThat(response.getIsCorrect()).isFalse();
        assertThat(response.getPointsEarned()).isEqualByComparingTo(points);
        assertThat(response.getAnsweredAt()).isEqualTo(now);
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal points = new BigDecimal("3.00");

        UserResponse r1 = new UserResponse();
        r1.setResponseId(5L);
        r1.setUserId(111L);
        r1.setQuizId(211L);
        r1.setQuestionId(311L);
        r1.setAttempt(1L);
        r1.setUserAnswer("C");
        r1.setIsCorrect(true);
        r1.setPointsEarned(points);
        r1.setAnsweredAt(now);

        UserResponse r2 = new UserResponse();
        r2.setResponseId(5L);
        r2.setUserId(111L);
        r2.setQuizId(211L);
        r2.setQuestionId(311L);
        r2.setAttempt(1L);
        r2.setUserAnswer("C");
        r2.setIsCorrect(true);
        r2.setPointsEarned(points);
        r2.setAnsweredAt(now);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime now = LocalDateTime.now();

        UserResponse r1 = new UserResponse();
        r1.setResponseId(1L);
        r1.setUserId(1L);
        r1.setQuizId(1L);
        r1.setQuestionId(1L);
        r1.setAttempt(1L);
        r1.setUserAnswer("A");
        r1.setIsCorrect(true);
        r1.setPointsEarned(new BigDecimal("1.00"));
        r1.setAnsweredAt(now);

        UserResponse r2 = new UserResponse();
        r2.setResponseId(2L);
        r2.setUserId(2L);
        r2.setQuizId(2L);
        r2.setQuestionId(2L);
        r2.setAttempt(2L);
        r2.setUserAnswer("B");
        r2.setIsCorrect(false);
        r2.setPointsEarned(new BigDecimal("2.00"));
        r2.setAnsweredAt(now);

        assertThat(r1).isNotEqualTo(r2);
        assertThat(r1.hashCode()).isNotEqualTo(r2.hashCode());
    }

    @Test
    void testEquals_NullAndOtherType() {
        UserResponse response = new UserResponse();
        assertThat(response).isNotEqualTo(null);
        assertThat(response).isNotEqualTo("some string");
    }

    @Test
    void testEquals_SameReference() {
        UserResponse response = new UserResponse();
        assertThat(response).isEqualTo(response);
    }

    @Test
    void testEqualsAfterMutation() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal points = new BigDecimal("5.00");

        UserResponse r1 = new UserResponse();
        r1.setResponseId(10L);
        r1.setUserId(20L);
        r1.setQuizId(30L);
        r1.setQuestionId(40L);
        r1.setAttempt(1L);
        r1.setUserAnswer("A");
        r1.setIsCorrect(true);
        r1.setPointsEarned(points);
        r1.setAnsweredAt(now);

        UserResponse r2 = new UserResponse();
        r2.setResponseId(10L);
        r2.setUserId(20L);
        r2.setQuizId(30L);
        r2.setQuestionId(40L);
        r2.setAttempt(1L);
        r2.setUserAnswer("A");
        r2.setIsCorrect(true);
        r2.setPointsEarned(points);
        r2.setAnsweredAt(now);

        assertThat(r1).isEqualTo(r2);

        r2.setUserAnswer("Changed");
        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal points = new BigDecimal("10.00");

        UserResponse response = UserResponse.builder()
                .responseId(99L)
                .userId(500L)
                .quizId(600L)
                .questionId(700L)
                .attempt(1L)
                .userAnswer("A")
                .isCorrect(true)
                .pointsEarned(points)
                .answeredAt(now)
                .build();

        assertThat(response.getResponseId()).isEqualTo(99L);
        assertThat(response.getUserId()).isEqualTo(500L);
        assertThat(response.getQuizId()).isEqualTo(600L);
        assertThat(response.getQuestionId()).isEqualTo(700L);
        assertThat(response.getAttempt()).isEqualTo(1L);
        assertThat(response.getUserAnswer()).isEqualTo("A");
        assertThat(response.getIsCorrect()).isTrue();
        assertThat(response.getPointsEarned()).isEqualByComparingTo(points);
        assertThat(response.getAnsweredAt()).isEqualTo(now);
    }

}

