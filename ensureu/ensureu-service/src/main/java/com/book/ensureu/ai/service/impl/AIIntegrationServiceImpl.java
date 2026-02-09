package com.book.ensureu.ai.service.impl;

import com.book.ensureu.ai.dto.*;
import com.book.ensureu.ai.service.AIIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AI Integration Service.
 * Communicates with the EnsureU AI Service (Python FastAPI).
 */
@Service
public class AIIntegrationServiceImpl implements AIIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AIIntegrationServiceImpl.class);

    private final RestTemplate aiRestTemplate;

    @Autowired
    public AIIntegrationServiceImpl(@Qualifier("aiRestTemplate") RestTemplate aiRestTemplate) {
        this.aiRestTemplate = aiRestTemplate;
    }

    @Override
    public QuestionGenerateResponse generateQuestions(QuestionGenerateRequest request) {
        logger.info("Generating {} question(s) for topic: {}/{}",
                request.getCount(), request.getTopic(), request.getSubtopic());

        try {
            ResponseEntity<QuestionGenerateResponse> response = aiRestTemplate.postForEntity(
                    "/questions/generate",
                    createHttpEntity(request),
                    QuestionGenerateResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully generated {} questions",
                        response.getBody().getQuestions().size());
                return response.getBody();
            }

            throw new RuntimeException("Failed to generate questions: Empty response");

        } catch (RestClientException e) {
            logger.error("Error calling AI service for question generation: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    public WrongAnswerExplanation explainWrongAnswer(WrongAnswerRequest request) {
        logger.info("Getting explanation for wrong answer on question: {}", request.getQuestionId());

        try {
            // The AI service returns a wrapper with 'explanation' field
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = aiRestTemplate.postForEntity(
                    "/questions/explain-wrong",
                    createHttpEntity(request),
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                @SuppressWarnings("unchecked")
                Map<String, Object> explanationMap = (Map<String, Object>) body.get("explanation");

                return mapToWrongAnswerExplanation(explanationMap);
            }

            throw new RuntimeException("Failed to get explanation: Empty response");

        } catch (RestClientException e) {
            logger.error("Error calling AI service for wrong answer explanation: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    public ExamAnalysisResponse analyzeExamPerformance(ExamAnalysisRequest request) {
        logger.info("Analyzing exam performance for user: {}, exam: {}",
                request.getUserId(), request.getExamId());

        try {
            ResponseEntity<ExamAnalysisResponse> response = aiRestTemplate.postForEntity(
                    "/analysis/exam",
                    createHttpEntity(request),
                    ExamAnalysisResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully analyzed exam performance");
                return response.getBody();
            }

            throw new RuntimeException("Failed to analyze exam: Empty response");

        } catch (RestClientException e) {
            logger.error("Error calling AI service for exam analysis: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    public StudyPlanResponse generateStudyPlan(StudyPlanRequest request) {
        logger.info("Generating study plan for user: {}, exam: {}",
                request.getUserId(), request.getExamName());

        try {
            ResponseEntity<StudyPlanResponse> response = aiRestTemplate.postForEntity(
                    "/analysis/study-plan",
                    createHttpEntity(request),
                    StudyPlanResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully generated study plan");
                return response.getBody();
            }

            throw new RuntimeException("Failed to generate study plan: Empty response");

        } catch (RestClientException e) {
            logger.error("Error calling AI service for study plan: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getQuestionHint(
            String questionId,
            String questionText,
            String topic,
            String correctAnswer,
            String solution,
            int hintLevel
    ) {
        logger.info("Getting hint level {} for question: {}", hintLevel, questionId);

        Map<String, Object> request = new HashMap<>();
        request.put("question_id", questionId);
        request.put("question_text", questionText);
        request.put("topic", topic);
        request.put("correct_answer", correctAnswer);
        request.put("solution", solution);
        request.put("hint_level", hintLevel);

        try {
            ResponseEntity<Map> response = aiRestTemplate.postForEntity(
                    "/questions/hints",
                    createHttpEntity(request),
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new RuntimeException("Failed to get hint: Empty response");

        } catch (RestClientException e) {
            logger.error("Error calling AI service for hint: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> validateQuestion(Map<String, Object> question) {
        logger.info("Validating question for quality");

        try {
            ResponseEntity<Map> response = aiRestTemplate.postForEntity(
                    "/questions/validate",
                    createHttpEntity(question),
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new RuntimeException("Failed to validate question: Empty response");

        } catch (RestClientException e) {
            logger.error("Error calling AI service for validation: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkHealth() {
        try {
            ResponseEntity<Map> response = aiRestTemplate.getForEntity("/health", Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            Map<String, Object> unhealthy = new HashMap<>();
            unhealthy.put("status", "unhealthy");
            unhealthy.put("error", "Empty response from AI service");
            return unhealthy;

        } catch (RestClientException e) {
            logger.error("AI service health check failed: {}", e.getMessage());

            Map<String, Object> unhealthy = new HashMap<>();
            unhealthy.put("status", "unhealthy");
            unhealthy.put("error", e.getMessage());
            return unhealthy;
        }
    }

    // Helper methods

    private <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    @SuppressWarnings("unchecked")
    private WrongAnswerExplanation mapToWrongAnswerExplanation(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        WrongAnswerExplanation.PracticeQuestion practiceQuestion = null;
        Map<String, Object> similarPractice = (Map<String, Object>) map.get("similar_practice");
        if (similarPractice != null) {
            practiceQuestion = WrongAnswerExplanation.PracticeQuestion.builder()
                    .question((String) similarPractice.get("question"))
                    .answer((String) similarPractice.get("answer"))
                    .build();
        }

        return WrongAnswerExplanation.builder()
                .encouragement((String) map.get("encouragement"))
                .whyWrong((String) map.get("why_wrong"))
                .correctApproach((String) map.get("correct_approach"))
                .keyConcept((String) map.get("key_concept"))
                .memoryTip((String) map.get("memory_tip"))
                .similarPractice(practiceQuestion)
                .misconceptionIdentified((String) map.get("misconception_identified"))
                .build();
    }

    // =============================================================================
    // ANALYSIS HISTORY METHODS
    // =============================================================================

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAnalysisHistory(String userId, int limit, int skip) {
        logger.info("Getting analysis history for user: {}", userId);

        try {
            String url = String.format("/analysis/history/%s?limit=%d&skip=%d", userId, limit, skip);
            ResponseEntity<List> response = aiRestTemplate.getForEntity(url, List.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            return new ArrayList<>();

        } catch (RestClientException e) {
            logger.error("Error getting analysis history: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMonthlySummary(String userId, int months) {
        logger.info("Getting monthly summary for user: {}", userId);

        try {
            String url = String.format("/analysis/monthly-summary/%s?months=%d", userId, months);
            ResponseEntity<Map> response = aiRestTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            return new HashMap<>();

        } catch (RestClientException e) {
            logger.error("Error getting monthly summary: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getExamAnalysis(String userId, String examId) {
        logger.info("Getting exam analysis for user: {}, exam: {}", userId, examId);

        try {
            String url = String.format("/analysis/exam/%s/%s", userId, examId);
            ResponseEntity<Map> response = aiRestTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            return new HashMap<>();

        } catch (RestClientException e) {
            logger.error("Error getting exam analysis: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getWeakAreasAnalysis(String userId) {
        logger.info("Getting weak areas analysis for user: {}", userId);

        try {
            String url = String.format("/analysis/weak-areas/%s", userId);
            ResponseEntity<Map> response = aiRestTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            return new HashMap<>();

        } catch (RestClientException e) {
            logger.error("Error getting weak areas analysis: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }
}
