package com.nt.course_service_lms.entityTest;

import com.nt.course_service_lms.entity.QuizQuestion;
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
    void testFieldAssignmentsUsingSetters() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion question = new QuizQuestion();
        question.setQuestionId(2L);
        question.setQuizId(20L);
        question.setQuestionText("What is the capital of France?");
        question.setQuestionType("MULTIPLE_CHOICE");
        question.setOptions("[\"Paris\",\"London\"]");
        question.setCorrectAnswer("Paris");
        question.setPoints(BigDecimal.valueOf(10.0));
        question.setExplanation("Paris is the capital of France.");
        question.setRequired(false);
        question.setPosition(2);
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

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

        QuizQuestion q1 = new QuizQuestion();
        q1.setQuestionId(3L);
        q1.setQuizId(30L);
        q1.setQuestionText("Q");
        q1.setQuestionType("TRUE_FALSE");
        q1.setCorrectAnswer("true");
        q1.setPoints(BigDecimal.TEN);
        q1.setRequired(true);
        q1.setPosition(1);
        q1.setCreatedAt(now);
        q1.setUpdatedAt(now);

        QuizQuestion q2 = new QuizQuestion();
        q2.setQuestionId(3L);
        q2.setQuizId(30L);
        q2.setQuestionText("Q");
        q2.setQuestionType("TRUE_FALSE");
        q2.setCorrectAnswer("true");
        q2.setPoints(BigDecimal.TEN);
        q2.setRequired(true);
        q2.setPosition(1);
        q2.setCreatedAt(now);
        q2.setUpdatedAt(now);

        assertThat(q1).isEqualTo(q2);
        assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q1 = new QuizQuestion();
        q1.setQuestionId(1L);
        q1.setQuizId(1L);
        q1.setQuestionText("A");
        q1.setQuestionType("TYPE");
        q1.setCorrectAnswer("1");
        q1.setPoints(BigDecimal.ONE);
        q1.setRequired(true);
        q1.setPosition(1);
        q1.setCreatedAt(now);
        q1.setUpdatedAt(now);

        QuizQuestion q2 = new QuizQuestion();
        q2.setQuestionId(2L);
        q2.setQuizId(2L);
        q2.setQuestionText("B");
        q2.setQuestionType("TYPE");
        q2.setCorrectAnswer("2");
        q2.setPoints(BigDecimal.TEN);
        q2.setRequired(false);
        q2.setPosition(2);
        q2.setCreatedAt(now);
        q2.setUpdatedAt(now);

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

        QuizQuestion q1 = new QuizQuestion();
        q1.setQuestionId(1L);
        q1.setQuizId(1L);
        q1.setQuestionText("Q");
        q1.setQuestionType("TYPE");
        q1.setCorrectAnswer("Ans");
        q1.setPoints(BigDecimal.ONE);
        q1.setRequired(true);
        q1.setPosition(1);
        q1.setCreatedAt(now);
        q1.setUpdatedAt(now);

        QuizQuestion q2 = new QuizQuestion();
        q2.setQuestionId(1L);
        q2.setQuizId(1L);
        q2.setQuestionText("Q");
        q2.setQuestionType("TYPE");
        q2.setCorrectAnswer("Ans");
        q2.setPoints(BigDecimal.ONE);
        q2.setRequired(true);
        q2.setPosition(1);
        q2.setCreatedAt(now);
        q2.setUpdatedAt(now);

        assertThat(q1).isEqualTo(q2);

        q2.setCorrectAnswer("Changed");

        assertThat(q1).isNotEqualTo(q2);
    }

    @Test
    void testToStringContainsFields() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q = new QuizQuestion();
        q.setQuestionId(99L);
        q.setQuizId(88L);
        q.setQuestionText("Q");
        q.setQuestionType("TYPE");
        q.setOptions("opts");
        q.setCorrectAnswer("A");
        q.setPoints(BigDecimal.TEN);
        q.setExplanation("exp");
        q.setRequired(true);
        q.setPosition(1);
        q.setCreatedAt(now);
        q.setUpdatedAt(now);

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

    @Test
    void testEquals_FailsOnDifferentQuestionText() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q1 = new QuizQuestion();
        q1.setQuestionId(1L);
        q1.setQuizId(1L);
        q1.setQuestionText("Question 1");
        q1.setQuestionType("TYPE");
        q1.setCorrectAnswer("A");
        q1.setPoints(BigDecimal.ONE);
        q1.setRequired(true);
        q1.setPosition(1);
        q1.setCreatedAt(now);
        q1.setUpdatedAt(now);

        QuizQuestion q2 = new QuizQuestion();
        q2.setQuestionId(1L);
        q2.setQuizId(1L);
        q2.setQuestionText("Question 2"); // different
        q2.setQuestionType("TYPE");
        q2.setCorrectAnswer("A");
        q2.setPoints(BigDecimal.ONE);
        q2.setRequired(true);
        q2.setPosition(1);
        q2.setCreatedAt(now);
        q2.setUpdatedAt(now);

        assertThat(q1).isNotEqualTo(q2);
    }

    @Test
    void testHashCodeChangesWhenFieldChanges() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q = new QuizQuestion();
        q.setQuestionId(1L);
        q.setQuizId(1L);
        q.setQuestionText("Q");
        q.setQuestionType("TYPE");
        q.setCorrectAnswer("A");
        q.setPoints(BigDecimal.ONE);
        q.setRequired(true);
        q.setPosition(1);
        q.setCreatedAt(now);
        q.setUpdatedAt(now);

        int originalHash = q.hashCode();
        q.setQuestionText("New Question Text");

        assertThat(q.hashCode()).isNotEqualTo(originalHash);
    }

    @Test
    void testBuilderPattern() {
        LocalDateTime now = LocalDateTime.now();

        QuizQuestion q = QuizQuestion.builder()
                .questionId(1L)
                .quizId(2L)
                .questionText("Sample Question")
                .questionType("MULTIPLE_CHOICE")
                .options("[\"A\",\"B\"]")
                .correctAnswer("A")
                .points(BigDecimal.valueOf(5.0))
                .explanation("Answer A is correct.")
                .required(true)
                .position(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(q.getQuestionText()).isEqualTo("Sample Question");
        assertThat(q.getCorrectAnswer()).isEqualTo("A");
        assertThat(q.getOptions()).contains("A", "B");
    }

}

