package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for wrong answer explanation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerExplanation {

    private String encouragement;

    @JsonProperty("why_wrong")
    private String whyWrong;

    @JsonProperty("correct_approach")
    private String correctApproach;

    @JsonProperty("key_concept")
    private String keyConcept;

    @JsonProperty("memory_tip")
    private String memoryTip;

    @JsonProperty("similar_practice")
    private PracticeQuestion similarPractice;

    @JsonProperty("misconception_identified")
    private String misconceptionIdentified;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticeQuestion {
        private String question;
        private String answer;
    }
}
