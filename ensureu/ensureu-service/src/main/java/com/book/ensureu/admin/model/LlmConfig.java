package com.book.ensureu.admin.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * LLM Configuration document - stores the active LLM provider settings.
 * Only ONE document should exist with id="active_config"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "llm_config")
public class LlmConfig {

    public static final String ACTIVE_CONFIG_ID = "active_config";

    @Id
    private String id;

    // Provider: claude, openai, ollama
    private String provider;

    // Model name (e.g., claude-sonnet-4-5, gpt-4o, llama3.1)
    private String model;

    // Embedding model (optional)
    private String embedModel;

    // Generation parameters
    private Double temperature;
    private Integer maxTokens;

    // Audit fields
    private Date updatedAt;
    private String updatedBy;

    // Provider status
    private Boolean isActive;

    /**
     * Model information with context windows and capabilities
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelInfo {
        private String id;
        private String name;
        private int contextWindow;
        private String tier; // "fast", "balanced", "powerful"
        private String description;
    }

    /**
     * Available LLM providers with their models and detailed information
     */
    public enum Provider {
        CLAUDE(
            "claude",
            "Anthropic Claude",
            "Leading AI assistant known for safety, helpfulness, and nuanced understanding. Excellent at analysis, writing, and coding tasks.",
            "ANTHROPIC_API_KEY",
            "https://api.anthropic.com",
            List.of(
                new ModelInfo("claude-sonnet-4-5", "Claude Sonnet 4.5", 200000, "balanced", "Latest balanced model - great for most tasks"),
                new ModelInfo("claude-3-5-haiku-20241022", "Claude 3.5 Haiku", 200000, "fast", "Fastest model - ideal for quick responses"),
                new ModelInfo("claude-3-opus-20240229", "Claude 3 Opus", 200000, "powerful", "Most capable - complex reasoning tasks")
            ),
            false,
            false
        ),

        OPENAI(
            "openai",
            "OpenAI GPT",
            "Industry-standard AI models with broad capabilities. Excellent tool use, function calling, and structured output support.",
            "OPENAI_API_KEY",
            "https://api.openai.com",
            List.of(
                new ModelInfo("gpt-4o", "GPT-4o", 128000, "balanced", "Flagship multimodal model - text, vision, audio"),
                new ModelInfo("gpt-4o-mini", "GPT-4o Mini", 128000, "fast", "Cost-effective for simpler tasks"),
                new ModelInfo("gpt-4-turbo", "GPT-4 Turbo", 128000, "powerful", "Enhanced reasoning with vision support")
            ),
            true,
            true
        ),

        OLLAMA(
            "ollama",
            "Ollama (Local)",
            "Run open-source LLMs locally. Free, private, and no API costs. Requires Ollama installed on your server.",
            null,
            "http://localhost:11434",
            List.of(
                new ModelInfo("llama3.1", "Llama 3.1", 128000, "balanced", "Meta's latest - excellent general purpose"),
                new ModelInfo("llama3.2", "Llama 3.2", 128000, "balanced", "Newest Llama with improved reasoning"),
                new ModelInfo("mistral", "Mistral 7B", 32000, "fast", "Fast and efficient for simpler tasks"),
                new ModelInfo("codellama", "Code Llama", 16000, "balanced", "Specialized for code generation")
            ),
            true,
            true
        );

        private final String id;
        private final String name;
        private final String description;
        private final String apiKeyEnvVar;
        private final String baseUrl;
        private final List<ModelInfo> modelInfoList;
        private final boolean supportsEmbeddings;
        private final boolean supportsJsonMode;

        Provider(String id, String name, String description, String apiKeyEnvVar, String baseUrl,
                 List<ModelInfo> modelInfoList, boolean supportsEmbeddings, boolean supportsJsonMode) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.apiKeyEnvVar = apiKeyEnvVar;
            this.baseUrl = baseUrl;
            this.modelInfoList = modelInfoList;
            this.supportsEmbeddings = supportsEmbeddings;
            this.supportsJsonMode = supportsJsonMode;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getApiKeyEnvVar() { return apiKeyEnvVar; }
        public String getBaseUrl() { return baseUrl; }
        public List<ModelInfo> getModelInfoList() { return modelInfoList; }
        public boolean isSupportsEmbeddings() { return supportsEmbeddings; }
        public boolean isSupportsJsonMode() { return supportsJsonMode; }

        // Legacy method for backward compatibility
        public List<String> getModels() {
            return modelInfoList.stream().map(ModelInfo::getId).toList();
        }

        public static Provider fromId(String id) {
            for (Provider p : values()) {
                if (p.id.equalsIgnoreCase(id)) return p;
            }
            return null;
        }

        /**
         * Check if provider is configured (API key present or local)
         */
        public boolean isConfigured() {
            if (this == OLLAMA) {
                return true; // Ollama is always "configured" (local)
            }
            if (apiKeyEnvVar == null) {
                return false;
            }
            String key = System.getenv(apiKeyEnvVar);
            return key != null && !key.isEmpty();
        }
    }
}
