package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for AI study plan generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanResponse {

    @JsonProperty("plan_overview")
    private Map<String, Object> planOverview;

    @JsonProperty("weekly_plan")
    private List<WeekPlan> weeklyPlan;

    @JsonProperty("revision_schedule")
    private Map<String, Object> revisionSchedule;

    @JsonProperty("mock_test_schedule")
    private List<Map<String, Object>> mockTestSchedule;

    @JsonProperty("daily_tips")
    private List<String> dailyTips;

    @JsonProperty("adjustment_triggers")
    private List<String> adjustmentTriggers;

    private String provider;

    private String model;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeekPlan {
        private Integer week;
        private String theme;
        private List<String> goals;

        @JsonProperty("daily_schedule")
        private List<DaySchedule> dailySchedule;

        @JsonProperty("weekly_test")
        private Map<String, Object> weeklyTest;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DaySchedule {
        private String day;
        private List<String> topics;
        private List<DailyActivity> activities;

        @JsonProperty("total_hours")
        private Double totalHours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyActivity {
        private String type;
        private String topic;

        @JsonProperty("duration_mins")
        private Integer durationMins;

        @JsonProperty("question_count")
        private Integer questionCount;
    }
}
