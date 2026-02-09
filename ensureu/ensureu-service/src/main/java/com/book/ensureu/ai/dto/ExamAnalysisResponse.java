package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for exam performance analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnalysisResponse {

    @JsonProperty("overall_assessment")
    private Map<String, String> overallAssessment;

    @JsonProperty("what_went_well")
    private List<String> whatWentWell;

    @JsonProperty("areas_of_concern")
    private List<AreaOfConcern> areasOfConcern;

    @JsonProperty("mistake_analysis")
    private MistakeAnalysis mistakeAnalysis;

    @JsonProperty("action_items")
    private List<ActionItem> actionItems;

    @JsonProperty("predicted_improvement")
    private Map<String, String> predictedImprovement;

    private String provider;

    private String model;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaOfConcern {
        private String area;
        private String score;
        private String pattern;
        private String evidence;
        private String priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MistakeAnalysis {
        @JsonProperty("conceptual_gaps")
        private Map<String, Object> conceptualGaps;

        @JsonProperty("careless_errors")
        private Map<String, Object> carelessErrors;

        @JsonProperty("time_management")
        private Map<String, Object> timeManagement;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionItem {
        private Integer priority;
        private String action;
        private String topic;

        @JsonProperty("estimated_time")
        private String estimatedTime;

        @JsonProperty("expected_impact")
        private String expectedImpact;
    }
}
