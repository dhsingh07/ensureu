package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for exam performance analysis.
 * Uses @JsonProperty for output to Python (snake_case) and @JsonAlias for input from frontend (camelCase).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnalysisRequest {

    @JsonProperty("exam_id")
    @JsonAlias("examId")
    private String examId;

    @JsonProperty("exam_name")
    @JsonAlias("examName")
    private String examName;

    @JsonProperty("user_id")
    @JsonAlias("userId")
    private String userId;

    private Double score;

    @JsonProperty("total_marks")
    @JsonAlias("totalMarks")
    private Double totalMarks;

    @JsonProperty("time_taken_minutes")
    @JsonAlias("timeTakenMinutes")
    private Integer timeTakenMinutes;

    @JsonProperty("total_time_minutes")
    @JsonAlias("totalTimeMinutes")
    private Integer totalTimeMinutes;

    private Double percentile;

    @JsonProperty("section_scores")
    @JsonAlias("sectionScores")
    private List<SectionScore> sectionScores;

    @JsonProperty("question_results")
    @JsonAlias("questionResults")
    private List<QuestionResult> questionResults;

    @JsonProperty("avg_score")
    @JsonAlias("avgScore")
    private Double avgScore;

    private String trend;

    @JsonProperty("weak_areas")
    @JsonAlias("weakAreas")
    private List<String> weakAreas;

    @JsonProperty("exams_count")
    @JsonAlias("examsCount")
    private Integer examsCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionScore {
        @JsonProperty("section_name")
        @JsonAlias("sectionName")
        private String sectionName;

        private Double score;

        @JsonProperty("max_score")
        @JsonAlias("maxScore")
        private Double maxScore;

        private Double percentage;

        @JsonProperty("questions_attempted")
        @JsonAlias("questionsAttempted")
        private Integer questionsAttempted;

        @JsonProperty("questions_correct")
        @JsonAlias("questionsCorrect")
        private Integer questionsCorrect;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        @JsonProperty("question_id")
        @JsonAlias("questionId")
        private String questionId;

        private String topic;

        private String subtopic;

        private String difficulty;

        @JsonProperty("student_answer")
        @JsonAlias("studentAnswer")
        private String studentAnswer;

        @JsonProperty("correct_answer")
        @JsonAlias("correctAnswer")
        private String correctAnswer;

        @JsonProperty("is_correct")
        @JsonAlias("isCorrect")
        private Boolean isCorrect;

        @JsonProperty("time_taken_seconds")
        @JsonAlias("timeTakenSeconds")
        private Integer timeTakenSeconds;

        @JsonProperty("marks_obtained")
        @JsonAlias("marksObtained")
        private Double marksObtained;

        @JsonProperty("max_marks")
        @JsonAlias("maxMarks")
        private Double maxMarks;
    }
}
