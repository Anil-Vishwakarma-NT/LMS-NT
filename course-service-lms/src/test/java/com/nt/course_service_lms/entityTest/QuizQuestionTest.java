package com.example.course_service_lms.entityTest;

import com.example.course_service_lms.entity.QuizQuestion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class QuizQuestionTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion question = new QuizQuestion();
        question.setQuestionId(1L);
        question.setQuizId(10L);
        question.setQuestionText("What is Java?");
        question.setQuestionType("MULTIPLE_CHOICE");
        question.setOptions("[\"A language\",\"A drink\"]");
        question.setCorrectAnswer("A language");
        question.setPoints(BigDecimal.valueOf(5.0));
        question.setExplanation("Java is a programming language.");
        question.setRequired(true);
        question.setPosition(1);
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

        assertThat(question.getQuestionId()).isEqualTo(1L);
        assertThat(question.getQuizId()).isEqualTo(10L);
        assertThat(question.getQuestionText()).isEqualTo("What is Java?");
        assertThat(question.getQuestionType()).isEqualTo("MULTIPLE_CHOICE");
        assertThat(question.getOptions()).isEqualTo("[\"A language\",\"A drink\"]");
        assertThat(question.getCorrectAnswer()).isEqualTo("A language");
        assertThat(question.getPoints()).isEqualTo(BigDecimal.valueOf(5.0));
        assertThat(question.getExplanation()).isEqualTo("Java is a programming language.");
        assertThat(question.getRequired()).isTrue();
        assertThat(question.getPosition()).isEqualTo(1);
        assertThat(question.getCreatedAt()).isEqualTo(now);
        assertThat(question.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        QuizQuestion question = new QuizQuestion(
                2L,
                20L,
                "What is the capital of France?",
                "MULTIPLE_CHOICE",
                "[\"Paris\",\"London\"]",
                "Paris",
                BigDecimal.valueOf(10.0),
                "Paris is the capital of France.",
                false,
                2,
                now,
                now
        );

        assertThat(question.getQuestionId()).isEqualTo(2L);
        assertThat(question.getQuizId()).isEqualTo(20L);
        assertThat(question.getQuestionText()).isEqualTo("What is the capital of France?");
        assertThat(question.getQuestionType()).isEqualTo("MULTIPLE_CHOICE");
        assertThat(question.getOptions()).isEqualTo("[\"Paris\",\"London\"]");
        assertThat(question.getCorrectAnswer()).isEqualTo("Paris");
        assertThat(question.getPoints()).isEqualTo(BigDecimal.valueOf(10.0));
        assertThat(question.getExplanation()).isEqualTo("Paris is the capital of France.");
        assertThat(question.getRequired()).isFalse();
        assertThat(question.getPosition()).isEqualTo(2);
        assertThat(question.getCreatedAt()).isEqualTo(now);
        assertThat(question.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q1 = new QuizQuestion(
                3L, 30L, "Q", "TRUE_FALSE", null, "true",
                BigDecimal.TEN, null, true, 1, now, now
        );

        QuizQuestion q2 = new QuizQuestion(
                3L, 30L, "Q", "TRUE_FALSE", null, "true",
                BigDecimal.TEN, null, true, 1, now, now
        );

        assertThat(q1).isEqualTo(q2);
        assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q1 = new QuizQuestion(1L, 1L, "A", "TYPE", null, "1",
                BigDecimal.ONE, null, true, 1, now, now);

        QuizQuestion q2 = new QuizQuestion(2L, 2L, "B", "TYPE", null, "2",
                BigDecimal.TEN, null, false, 2, now, now);

        assertThat(q1).isNotEqualTo(q2);
        assertThat(q1.hashCode()).isNotEqualTo(q2.hashCode());
    }

    @Test
    void testEquals_SameReference() {
        QuizQuestion q = new QuizQuestion();
        assertThat(q).isEqualTo(q);
    }

    @Test
    void testEquals_NullAndOtherType() {
        QuizQuestion q = new QuizQuestion();
        assertThat(q).isNotEqualTo(null);
        assertThat(q).isNotEqualTo("Not a QuizQuestion");
    }

    @Test
    void testEqualsAfterFieldChange() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q1 = new QuizQuestion(1L, 1L, "Q", "TYPE", null, "Ans", BigDecimal.ONE, null, true, 1, now, now);
        QuizQuestion q2 = new QuizQuestion(1L, 1L, "Q", "TYPE", null, "Ans", BigDecimal.ONE, null, true, 1, now, now);

        assertThat(q1).isEqualTo(q2);

        q2.setCorrectAnswer("Changed");

        assertThat(q1).isNotEqualTo(q2);
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q = new QuizQuestion(99L, 88L, "Q", "TYPE", "opts", "A", BigDecimal.TEN, "exp", true, 1, now, now);

        String s = q.toString();

        assertThat(s).contains("questionId=99");
        assertThat(s).contains("quizId=88");
        assertThat(s).contains("questionText=Q");
        assertThat(s).contains("questionType=TYPE");
        assertThat(s).contains("correctAnswer=A");
        assertThat(s).contains("explanation=exp");
        assertThat(s).contains("required=true");
    }

    @Test
    void testNullFields() {
        QuizQuestion q = new QuizQuestion();
        q.setOptions(null);
        q.setExplanation(null);
        q.setCorrectAnswer(null);

        assertThat(q.getOptions()).isNull();
        assertThat(q.getExplanation()).isNull();
        assertThat(q.getCorrectAnswer()).isNull();
    }
}

