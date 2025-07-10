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
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal points = new BigDecimal("2.50");

        UserResponse response = new UserResponse(
                2L, 101L, 201L, 301L, 2L,
                "D", false, points, now
        );

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

        UserResponse r1 = new UserResponse(
                5L, 111L, 211L, 311L, 1L, "C", true, points, now
        );
        UserResponse r2 = new UserResponse(
                5L, 111L, 211L, 311L, 1L, "C", true, points, now
        );

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        UserResponse r1 = new UserResponse(
                1L, 1L, 1L, 1L, 1L, "A", true,
                new BigDecimal("1.00"), LocalDateTime.now()
        );
        UserResponse r2 = new UserResponse(
                2L, 2L, 2L, 2L, 2L, "B", false,
                new BigDecimal("2.00"), LocalDateTime.now()
        );

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

        UserResponse r1 = new UserResponse(10L, 20L, 30L, 40L, 1L, "A", true, points, now);
        UserResponse r2 = new UserResponse(10L, 20L, 30L, 40L, 1L, "A", true, points, now);

        assertThat(r1).isEqualTo(r2);

        r2.setUserAnswer("Changed");
        assertThat(r1).isNotEqualTo(r2);
    }
}

