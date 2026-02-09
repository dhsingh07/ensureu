package com.book.ensureu.admin.service.Impl;

import com.book.ensureu.admin.dto.LlmConfigDto;
import com.book.ensureu.admin.model.LlmConfig;
import com.book.ensureu.admin.service.LlmConfigService;
import com.book.ensureu.repository.LlmConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LlmConfigServiceImpl implements LlmConfigService {

    @Autowired
    private LlmConfigRepository llmConfigRepository;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    // Default configuration values
    private static final String DEFAULT_PROVIDER = "claude";
    private static final String DEFAULT_MODEL = "claude-sonnet-4-5";
    private static final Double DEFAULT_TEMPERATURE = 0.7;
    private static final Integer DEFAULT_MAX_TOKENS = 4096;

    @Override
    public List<LlmConfigDto.ProviderInfo> getProviders() {
        return Arrays.stream(LlmConfig.Provider.values())
            .map(p -> {
                // Convert ModelInfo to ModelDetail
                List<LlmConfigDto.ModelDetail> modelDetails = p.getModelInfoList().stream()
                    .map(m -> LlmConfigDto.ModelDetail.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .contextWindow(m.getContextWindow())
                        .tier(m.getTier())
                        .description(m.getDescription())
                        .build())
                    .collect(Collectors.toList());

                return LlmConfigDto.ProviderInfo.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .description(p.getDescription())
                    .baseUrl(p.getBaseUrl())
                    .apiKeyEnvVar(p.getApiKeyEnvVar())
                    .models(p.getModels())
                    .modelDetails(modelDetails)
                    .supportsEmbeddings(p.isSupportsEmbeddings())
                    .supportsJsonMode(p.isSupportsJsonMode())
                    .configured(p.isConfigured())
                    .isLocal(p == LlmConfig.Provider.OLLAMA)
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    public LlmConfigDto.ConfigResponse getCurrentConfig() {
        Optional<LlmConfig> activeConfig = llmConfigRepository.findByIsActiveTrue();

        if (activeConfig.isPresent()) {
            LlmConfig config = activeConfig.get();
            return LlmConfigDto.ConfigResponse.builder()
                .provider(config.getProvider())
                .model(config.getModel())
                .embedModel(config.getEmbedModel())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .updatedAt(config.getUpdatedAt())
                .updatedBy(config.getUpdatedBy())
                .isActive(config.getIsActive())
                .build();
        }

        // Return defaults if no config exists
        return LlmConfigDto.ConfigResponse.builder()
            .provider(DEFAULT_PROVIDER)
            .model(DEFAULT_MODEL)
            .temperature(DEFAULT_TEMPERATURE)
            .maxTokens(DEFAULT_MAX_TOKENS)
            .isActive(true)
            .build();
    }

    @Override
    public LlmConfigDto.ConfigResponse updateConfig(LlmConfigDto.UpdateRequest request, String updatedBy) {
        // Validate provider
        LlmConfig.Provider provider = LlmConfig.Provider.fromId(request.getProvider());
        if (provider == null) {
            throw new IllegalArgumentException("Invalid provider: " + request.getProvider() +
                ". Valid options: " + Arrays.stream(LlmConfig.Provider.values())
                    .map(LlmConfig.Provider::getId)
                    .collect(Collectors.joining(", ")));
        }

        // Validate model
        if (!provider.getModels().contains(request.getModel())) {
            throw new IllegalArgumentException("Invalid model for " + provider.getName() +
                ". Valid options: " + String.join(", ", provider.getModels()));
        }

        // Deactivate any existing active config
        llmConfigRepository.findByIsActiveTrue().ifPresent(existing -> {
            existing.setIsActive(false);
            llmConfigRepository.save(existing);
        });

        // Create or update the active config
        Date now = new Date();
        LlmConfig config = LlmConfig.builder()
            .id(LlmConfig.ACTIVE_CONFIG_ID)
            .provider(request.getProvider())
            .model(request.getModel())
            .embedModel(request.getEmbedModel())
            .temperature(request.getTemperature() != null ? request.getTemperature() : DEFAULT_TEMPERATURE)
            .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : DEFAULT_MAX_TOKENS)
            .updatedAt(now)
            .updatedBy(updatedBy)
            .isActive(true)
            .build();

        llmConfigRepository.save(config);

        log.info("LLM configuration updated: provider={}, model={}, by={}",
            config.getProvider(), config.getModel(), updatedBy);

        return LlmConfigDto.ConfigResponse.builder()
            .provider(config.getProvider())
            .model(config.getModel())
            .embedModel(config.getEmbedModel())
            .temperature(config.getTemperature())
            .maxTokens(config.getMaxTokens())
            .updatedAt(config.getUpdatedAt())
            .updatedBy(config.getUpdatedBy())
            .isActive(config.getIsActive())
            .build();
    }

    @Override
    public LlmConfigDto.TestResult testProvider(String providerId, String model) {
        LlmConfig.Provider provider = LlmConfig.Provider.fromId(providerId);
        if (provider == null) {
            return LlmConfigDto.TestResult.builder()
                .success(false)
                .provider(providerId)
                .model(model)
                .message("Invalid provider: " + providerId)
                .build();
        }

        String testModel = model != null ? model : provider.getModels().get(0);

        try {
            // Call Python AI service to test the provider
            RestTemplate restTemplate = new RestTemplate();
            String testUrl = aiServiceUrl + "/llm-config/test?provider=" + providerId;
            if (model != null) {
                testUrl += "&model=" + model;
            }

            long startTime = System.currentTimeMillis();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(testUrl, entity, Map.class);

            long elapsed = System.currentTimeMillis() - startTime;

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                return LlmConfigDto.TestResult.builder()
                    .success(true)
                    .provider(providerId)
                    .model(testModel)
                    .message((String) response.get("message"))
                    .responseTimeMs(elapsed)
                    .build();
            } else {
                return LlmConfigDto.TestResult.builder()
                    .success(false)
                    .provider(providerId)
                    .model(testModel)
                    .message(response != null ? (String) response.get("message") : "No response")
                    .build();
            }

        } catch (Exception e) {
            log.error("Failed to test provider {}: {}", providerId, e.getMessage());
            return LlmConfigDto.TestResult.builder()
                .success(false)
                .provider(providerId)
                .model(testModel)
                .message("Test failed: " + e.getMessage())
                .build();
        }
    }
}
