package com.book.ensureu.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for wrong answer explanation.
 * Uses @JsonProperty for output to Python (snake_case) and @JsonAlias for input from frontend (camelCase).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerRequest {

    @JsonProperty("question_id")
    @JsonAlias("questionId")
    private String questionId;

    @JsonProperty("question_text")
    @JsonAlias("questionText")
    private String questionText;

    private Map<String, String> options;

    @JsonProperty("student_answer")
    @JsonAlias("studentAnswer")
    private String studentAnswer;

    @JsonProperty("correct_answer")
    @JsonAlias("correctAnswer")
    private String correctAnswer;

    private String topic;

    private String subtopic;

    @JsonProperty("user_id")
    @JsonAlias("userId")
    private String userId;
}
