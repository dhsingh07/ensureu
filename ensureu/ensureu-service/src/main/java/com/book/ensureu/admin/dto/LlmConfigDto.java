package com.book.ensureu.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;
import java.util.List;

public class LlmConfigDto {

    /**
     * Response for current LLM configuration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfigResponse {
        private String provider;
        private String model;
        private String embedModel;
        private Double temperature;
        private Integer maxTokens;
        private Date updatedAt;
        private String updatedBy;
        private Boolean isActive;
    }

    /**
     * Request to update LLM configuration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String provider;
        private String model;
        private String embedModel;
        private Double temperature;
        private Integer maxTokens;
    }

    /**
     * Model information with detailed specifications
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelDetail {
        private String id;
        private String name;
        private int contextWindow;
        private String tier;       // "fast", "balanced", "powerful"
        private String description;
    }

    /**
     * Provider information with detailed metadata
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProviderInfo {
        private String id;
        private String name;
        private String description;
        private String baseUrl;
        private String apiKeyEnvVar;
        private List<String> models;           // Simple model ID list (backward compat)
        private List<ModelDetail> modelDetails; // Detailed model info
        private boolean supportsEmbeddings;
        private boolean supportsJsonMode;
        private boolean configured;
        private boolean isLocal;               // True for Ollama
    }

    /**
     * Test result for provider
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TestResult {
        private boolean success;
        private String provider;
        private String model;
        private String message;
        private Long responseTimeMs;
    }
}
