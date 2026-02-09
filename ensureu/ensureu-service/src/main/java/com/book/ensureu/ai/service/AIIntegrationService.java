package com.book.ensureu.ai.service;

import com.book.ensureu.ai.dto.*;

import java.util.List;
import java.util.Map;

/**
 * Service interface for AI service integration.
 */
public interface AIIntegrationService {

    /**
     * Generate questions using AI.
     *
     * @param request Question generation parameters
     * @return Generated questions with solutions
     */
    QuestionGenerateResponse generateQuestions(QuestionGenerateRequest request);

    /**
     * Get AI explanation for why an answer was wrong.
     *
     * @param request Wrong answer details
     * @return Explanation with learning recommendations
     */
    WrongAnswerExplanation explainWrongAnswer(WrongAnswerRequest request);

    /**
     * Analyze exam performance with AI.
     *
     * @param request Exam results and historical data
     * @return Comprehensive analysis with action items
     */
    ExamAnalysisResponse analyzeExamPerformance(ExamAnalysisRequest request);

    /**
     * Generate personalized study plan.
     *
     * @param request User profile and learning goals
     * @return Week-by-week study plan
     */
    StudyPlanResponse generateStudyPlan(StudyPlanRequest request);

    /**
     * Get hint for a question.
     *
     * @param questionId Question ID
     * @param questionText Question text
     * @param topic Topic
     * @param correctAnswer Correct answer
     * @param solution Solution
     * @param hintLevel Hint level (1-3)
     * @return Hint response
     */
    Map<String, Object> getQuestionHint(
            String questionId,
            String questionText,
            String topic,
            String correctAnswer,
            String solution,
            int hintLevel
    );

    /**
     * Validate a question for quality.
     *
     * @param question Question details
     * @return Validation results
     */
    Map<String, Object> validateQuestion(Map<String, Object> question);

    /**
     * Check AI service health.
     *
     * @return Health status
     */
    Map<String, Object> checkHealth();

    // =============================================================================
    // ANALYSIS HISTORY METHODS
    // =============================================================================

    /**
     * Get user's exam analysis history.
     *
     * @param userId User ID
     * @param limit Maximum number of results
     * @param skip Number of results to skip (pagination)
     * @return List of analysis records
     */
    List<Map<String, Object>> getAnalysisHistory(String userId, int limit, int skip);

    /**
     * Get monthly summary of user's analyses.
     *
     * @param userId User ID
     * @param months Number of months to include
     * @return Monthly summary with trends
     */
    Map<String, Object> getMonthlySummary(String userId, int months);

    /**
     * Get analysis for a specific exam.
     *
     * @param userId User ID
     * @param examId Exam ID
     * @return Analysis record
     */
    Map<String, Object> getExamAnalysis(String userId, String examId);

    /**
     * Get user's weak areas analysis.
     *
     * @param userId User ID
     * @return Weak areas trend analysis
     */
    Map<String, Object> getWeakAreasAnalysis(String userId);
}
