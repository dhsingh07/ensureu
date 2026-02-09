package com.book.ensureu.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for AI question generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionGenerateResponse {

    private List<GeneratedQuestion> questions;

    private String provider;

    private String model;
}
