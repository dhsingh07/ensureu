package com.book.ensureu.admin.api;

import com.book.ensureu.admin.dto.LlmConfigDto;
import com.book.ensureu.admin.service.LlmConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/llm-config")
@Tag(name = "LLM Configuration", description = "Manage AI/LLM provider settings (SUPERADMIN only)")
public class LlmConfigApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmConfigApi.class);

    @Autowired
    private LlmConfigService llmConfigService;

    @Operation(summary = "Get available LLM providers")
    @CrossOrigin
    @RequestMapping(value = "/providers", method = RequestMethod.GET)
    public List<LlmConfigDto.ProviderInfo> getProviders() {
        LOGGER.info("getProviders");
        try {
            return llmConfigService.getProviders();
        } catch (Exception ex) {
            LOGGER.error("getProviders error", ex);
        }
        return null;
    }

    @Operation(summary = "Get current LLM configuration")
    @CrossOrigin
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public LlmConfigDto.ConfigResponse getCurrentConfig() {
        LOGGER.info("getCurrentConfig");
        try {
            return llmConfigService.getCurrentConfig();
        } catch (Exception ex) {
            LOGGER.error("getCurrentConfig error", ex);
        }
        return null;
    }

    @Operation(summary = "Update LLM configuration (SUPERADMIN only)")
    @CrossOrigin
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public LlmConfigDto.ConfigResponse updateConfig(@RequestBody LlmConfigDto.UpdateRequest request) {
        LOGGER.info("updateConfig: provider={}, model={}", request.getProvider(), request.getModel());
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String updatedBy = auth != null ? auth.getName() : "unknown";

            return llmConfigService.updateConfig(request, updatedBy);
        } catch (Exception ex) {
            LOGGER.error("updateConfig error", ex);
        }
        return null;
    }

    @Operation(summary = "Test LLM provider connection (SUPERADMIN only)")
    @CrossOrigin
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public LlmConfigDto.TestResult testProvider(
            @RequestParam String provider,
            @RequestParam(required = false) String model) {
        LOGGER.info("testProvider: provider={}, model={}", provider, model);
        try {
            return llmConfigService.testProvider(provider, model);
        } catch (Exception ex) {
            LOGGER.error("testProvider error", ex);
        }
        return null;
    }
}
