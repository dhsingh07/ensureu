package com.book.ensureu.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for AI Service Integration.
 * Provides RestTemplate configured for communication with the EnsureU AI Service.
 */
@Configuration
public class AIServiceConfig {

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Value("${ai.service.timeout:120000}")
    private int timeout;

    @Value("${ai.service.api-key:}")
    private String apiKey;

    @Bean(name = "aiRestTemplate")
    public RestTemplate aiRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .rootUri(aiServiceUrl)
                .setConnectTimeout(Duration.ofMillis(30000)) // 30 seconds to connect
                .setReadTimeout(Duration.ofMillis(timeout))  // 120 seconds for LLM response
                .build();

        // Add API key interceptor if configured
        if (apiKey != null && !apiKey.isEmpty()) {
            restTemplate.getInterceptors().add(apiKeyInterceptor());
        }

        return restTemplate;
    }

    private ClientHttpRequestInterceptor apiKeyInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().add("X-API-Key", apiKey);
            return execution.execute(request, body);
        };
    }

    public String getAiServiceUrl() {
        return aiServiceUrl;
    }
}
