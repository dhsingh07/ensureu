package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for AI question generation.
 * Uses @JsonProperty for output to Python (snake_case) and @JsonAlias for input from frontend (camelCase).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionGenerateRequest {

    @JsonProperty("exam_type")
    @JsonAlias("examType")
    private String examType;

    private String topic;

    private String subtopic;

    @Builder.Default
    private String difficulty = "medium";

    @Builder.Default
    private Integer count = 1;
}
