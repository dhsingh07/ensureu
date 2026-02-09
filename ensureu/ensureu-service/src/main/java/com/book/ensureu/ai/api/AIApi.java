package com.book.ensureu.ai.api;

import com.book.ensureu.ai.dto.*;
import com.book.ensureu.ai.service.AIIntegrationService;
import com.book.ensureu.response.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for AI-powered features.
 * Proxies requests to the EnsureU AI Service.
 */
@RestController
@RequestMapping("/ai")
@CrossOrigin
@Tag(name = "AI", description = "AI-powered learning features")
public class AIApi {

    private static final Logger logger = LoggerFactory.getLogger(AIApi.class);

    private final AIIntegrationService aiService;

    @Autowired
    public AIApi(AIIntegrationService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check AI service health")
    public Response<Map<String, Object>> checkHealth() {
        try {
            Map<String, Object> health = aiService.checkHealth();
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(health)
                    .setMessage("AI service status");
        } catch (Exception e) {
            logger.error("AI health check error: {}", e.getMessage());
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .setMessage(e.getMessage());
        }
    }

    @PostMapping("/questions/generate")
    @Operation(summary = "Generate questions using AI",
            description = "Generate exam-quality MCQs for a given topic and difficulty")
    public Response<QuestionGenerateResponse> generateQuestions(
            @RequestBody QuestionGenerateRequest request
    ) {
        logger.info("API: Generate questions - topic: {}, count: {}",
                request.getTopic(), request.getCount());

        try {
            QuestionGenerateResponse response = aiService.generateQuestions(request);
            return new Response<QuestionGenerateResponse>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(response)
                    .setMessage("Questions generated successfully");
        } catch (Exception e) {
            logger.error("Question generation failed: {}", e.getMessage());
            return new Response<QuestionGenerateResponse>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @PostMapping("/questions/explain-wrong")
    @Operation(summary = "Explain wrong answer",
            description = "Get AI explanation for why an answer was incorrect")
    public Response<WrongAnswerExplanation> explainWrongAnswer(
            @RequestBody WrongAnswerRequest request
    ) {
        logger.info("API: Explain wrong answer - question: {}", request.getQuestionId());

        try {
            WrongAnswerExplanation explanation = aiService.explainWrongAnswer(request);
            return new Response<WrongAnswerExplanation>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(explanation)
                    .setMessage("Explanation generated successfully");
        } catch (Exception e) {
            logger.error("Wrong answer explanation failed: {}", e.getMessage());
            return new Response<WrongAnswerExplanation>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @PostMapping("/questions/hint")
    @Operation(summary = "Get question hint",
            description = "Get progressive hints for a question")
    public Response<Map<String, Object>> getQuestionHint(
            @RequestBody Map<String, Object> request
    ) {
        String questionId = (String) request.get("question_id");
        logger.info("API: Get hint for question: {}", questionId);

        try {
            Map<String, Object> hint = aiService.getQuestionHint(
                    questionId,
                    (String) request.get("question_text"),
                    (String) request.get("topic"),
                    (String) request.get("correct_answer"),
                    (String) request.get("solution"),
                    request.get("hint_level") != null ? (Integer) request.get("hint_level") : 1
            );
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(hint)
                    .setMessage("Hint generated successfully");
        } catch (Exception e) {
            logger.error("Hint generation failed: {}", e.getMessage());
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @PostMapping("/questions/validate")
    @Operation(summary = "Validate question quality",
            description = "Check a question for accuracy, clarity, and exam-readiness")
    public Response<Map<String, Object>> validateQuestion(
            @RequestBody Map<String, Object> question
    ) {
        logger.info("API: Validate question");

        try {
            Map<String, Object> validation = aiService.validateQuestion(question);
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(validation)
                    .setMessage("Validation complete");
        } catch (Exception e) {
            logger.error("Question validation failed: {}", e.getMessage());
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @PostMapping("/analysis/exam")
    @Operation(summary = "Analyze exam performance",
            description = "Get comprehensive AI analysis of exam performance with action items")
    public Response<ExamAnalysisResponse> analyzeExamPerformance(
            @RequestBody ExamAnalysisRequest request
    ) {
        logger.info("API: Analyze exam - user: {}, exam: {}",
                request.getUserId(), request.getExamId());

        try {
            ExamAnalysisResponse analysis = aiService.analyzeExamPerformance(request);
            return new Response<ExamAnalysisResponse>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(analysis)
                    .setMessage("Analysis complete");
        } catch (Exception e) {
            logger.error("Exam analysis failed: {}", e.getMessage());
            return new Response<ExamAnalysisResponse>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @PostMapping("/analysis/study-plan")
    @Operation(summary = "Generate study plan",
            description = "Get personalized AI-generated study plan")
    public Response<StudyPlanResponse> generateStudyPlan(
            @RequestBody StudyPlanRequest request
    ) {
        logger.info("API: Generate study plan - user: {}, exam: {}",
                request.getUserId(), request.getExamName());

        try {
            StudyPlanResponse plan = aiService.generateStudyPlan(request);
            return new Response<StudyPlanResponse>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(plan)
                    .setMessage("Study plan generated successfully");
        } catch (Exception e) {
            logger.error("Study plan generation failed: {}", e.getMessage());
            return new Response<StudyPlanResponse>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    // =============================================================================
    // ANALYSIS HISTORY ENDPOINTS
    // =============================================================================

    @GetMapping("/analysis/history/{userId}")
    @Operation(summary = "Get analysis history",
            description = "Get user's exam analysis history for progress tracking")
    public Response<List<Map<String, Object>>> getAnalysisHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int skip
    ) {
        logger.info("API: Get analysis history - user: {}", userId);

        try {
            List<Map<String, Object>> history = aiService.getAnalysisHistory(userId, limit, skip);
            return new Response<List<Map<String, Object>>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(history)
                    .setMessage("Analysis history retrieved");
        } catch (Exception e) {
            logger.error("Failed to get analysis history: {}", e.getMessage());
            return new Response<List<Map<String, Object>>>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @GetMapping("/analysis/monthly-summary/{userId}")
    @Operation(summary = "Get monthly summary",
            description = "Get monthly summary of user's exam analyses with progress trends")
    public Response<Map<String, Object>> getMonthlySummary(
            @PathVariable String userId,
            @RequestParam(defaultValue = "6") int months
    ) {
        logger.info("API: Get monthly summary - user: {}", userId);

        try {
            Map<String, Object> summary = aiService.getMonthlySummary(userId, months);
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(summary)
                    .setMessage("Monthly summary retrieved");
        } catch (Exception e) {
            logger.error("Failed to get monthly summary: {}", e.getMessage());
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @GetMapping("/analysis/exam/{userId}/{examId}")
    @Operation(summary = "Get exam analysis",
            description = "Get the most recent analysis for a specific exam")
    public Response<Map<String, Object>> getExamAnalysis(
            @PathVariable String userId,
            @PathVariable String examId
    ) {
        logger.info("API: Get exam analysis - user: {}, exam: {}", userId, examId);

        try {
            Map<String, Object> analysis = aiService.getExamAnalysis(userId, examId);
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(analysis)
                    .setMessage("Exam analysis retrieved");
        } catch (Exception e) {
            logger.error("Failed to get exam analysis: {}", e.getMessage());
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }

    @GetMapping("/analysis/weak-areas/{userId}")
    @Operation(summary = "Get weak areas analysis",
            description = "Get analysis of user's persistent weak areas across all exams")
    public Response<Map<String, Object>> getWeakAreasAnalysis(
            @PathVariable String userId
    ) {
        logger.info("API: Get weak areas - user: {}", userId);

        try {
            Map<String, Object> weakAreas = aiService.getWeakAreasAnalysis(userId);
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.OK.value())
                    .setBody(weakAreas)
                    .setMessage("Weak areas analysis retrieved");
        } catch (Exception e) {
            logger.error("Failed to get weak areas analysis: {}", e.getMessage());
            return new Response<Map<String, Object>>()
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(e.getMessage());
        }
    }
}
