package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO representing an AI-generated question.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedQuestion {

    private String question;

    private Map<String, String> options;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    private String solution;

    @JsonProperty("concepts_tested")
    private List<String> conceptsTested;

    @JsonProperty("estimated_time_seconds")
    private Integer estimatedTimeSeconds;

    @JsonProperty("difficulty_score")
    private Double difficultyScore;

    @JsonProperty("distractor_explanations")
    private Map<String, String> distractorExplanations;
}
