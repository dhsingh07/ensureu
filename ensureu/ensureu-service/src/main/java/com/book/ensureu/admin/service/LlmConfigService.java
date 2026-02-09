package com.book.ensureu.admin.service;

import com.book.ensureu.admin.dto.LlmConfigDto;
import com.book.ensureu.admin.model.LlmConfig;

import java.util.List;

public interface LlmConfigService {

    /**
     * Get all available LLM providers
     */
    List<LlmConfigDto.ProviderInfo> getProviders();

    /**
     * Get the current active LLM configuration
     */
    LlmConfigDto.ConfigResponse getCurrentConfig();

    /**
     * Update the LLM configuration (sets as active)
     */
    LlmConfigDto.ConfigResponse updateConfig(LlmConfigDto.UpdateRequest request, String updatedBy);

    /**
     * Test a provider connection
     */
    LlmConfigDto.TestResult testProvider(String provider, String model);
}
