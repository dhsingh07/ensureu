package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for AI study plan generation.
 * Uses @JsonProperty for output to Python (snake_case) and @JsonAlias for input from frontend (camelCase).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanRequest {

    @JsonProperty("user_id")
    @JsonAlias("userId")
    private String userId;

    @JsonProperty("exam_name")
    @JsonAlias("examName")
    private String examName;

    @JsonProperty("exam_date")
    @JsonAlias("examDate")
    private String examDate;

    @JsonProperty("current_score")
    @JsonAlias("currentScore")
    private Double currentScore;

    @JsonProperty("hours_per_day")
    @JsonAlias("hoursPerDay")
    @Builder.Default
    private Double hoursPerDay = 2.0;

    @JsonProperty("preferred_times")
    @JsonAlias("preferredTimes")
    private List<String> preferredTimes;

    @JsonProperty("topic_mastery")
    @JsonAlias("topicMastery")
    private List<TopicMastery> topicMastery;

    @JsonProperty("weak_areas")
    @JsonAlias({"weakAreas", "weakTopics", "weak_topics"})
    private List<String> weakAreas;

    @JsonProperty("strong_areas")
    @JsonAlias({"strongAreas", "strongTopics", "strong_topics"})
    private List<String> strongAreas;

    @JsonProperty("current_level")
    @JsonAlias("currentLevel")
    private String currentLevel;

    @JsonProperty("available_hours_per_day")
    @JsonAlias("availableHoursPerDay")
    private Double availableHoursPerDay;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicMastery {
        private String topic;

        @JsonProperty("mastery_percentage")
        @JsonAlias("masteryPercentage")
        private Double masteryPercentage;

        @JsonProperty("last_practiced")
        @JsonAlias("lastPracticed")
        private String lastPracticed;

        @JsonProperty("question_count")
        @JsonAlias("questionCount")
        @Builder.Default
        private Integer questionCount = 0;
    }
}
