// AI Service hooks - React Query integration for AI features

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post, put } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import { useUIStore } from '@/stores/ui-store';
import type {
  QuestionGenerateRequest,
  QuestionGenerateResponse,
  WrongAnswerRequest,
  WrongAnswerExplanation,
  HintRequest,
  HintResponse,
  QuestionValidationRequest,
  QuestionValidationResponse,
  ExamAnalysisRequest,
  ExamAnalysisResponse,
  StudyPlanRequest,
  StudyPlanResponse,
  AIHealthResponse,
  LLMProviderInfo,
  LLMConfig,
  LLMConfigUpdate,
  LLMProviderTestResult,
} from '@/types/ai';
import type { ApiResponse } from '@/types/api';

// Query keys factory
export const aiKeys = {
  all: ['ai'] as const,
  health: () => [...aiKeys.all, 'health'] as const,
  explanation: (questionId: string) => [...aiKeys.all, 'explanation', questionId] as const,
  hint: (questionId: string, level: number) => [...aiKeys.all, 'hint', questionId, level] as const,
  validation: (questionId: string) => [...aiKeys.all, 'validation', questionId] as const,
  examAnalysis: (examId: string) => [...aiKeys.all, 'exam-analysis', examId] as const,
  studyPlan: (userId: string, examName: string) => [...aiKeys.all, 'study-plan', userId, examName] as const,
  // Analysis History keys
  analysisHistory: (userId: string) => [...aiKeys.all, 'analysis-history', userId] as const,
  monthlySummary: (userId: string) => [...aiKeys.all, 'monthly-summary', userId] as const,
  weakAreas: (userId: string) => [...aiKeys.all, 'weak-areas', userId] as const,
  // LLM Config keys
  llmProviders: () => [...aiKeys.all, 'llm-providers'] as const,
  llmConfig: () => [...aiKeys.all, 'llm-config'] as const,
};

// Check AI service health
export function useAIHealth(enabled = true) {
  return useQuery({
    queryKey: aiKeys.health(),
    queryFn: async () => {
      const response = await get<ApiResponse<AIHealthResponse>>(API_URLS.AI.HEALTH);
      return response.body || {
        status: 'unavailable' as const,
        features: {
          questionGeneration: false,
          wrongAnswerExplanation: false,
          hints: false,
          examAnalysis: false,
          studyPlan: false,
        },
      };
    },
    enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: 1,
  });
}

// Generate questions using AI
export function useGenerateQuestions() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (request: QuestionGenerateRequest) => {
      const response = await post<ApiResponse<QuestionGenerateResponse>>(
        API_URLS.AI.GENERATE_QUESTIONS,
        request
      );
      if (response.status !== 200) {
        throw new Error(response.message || 'Failed to generate questions');
      }
      return response.body;
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to generate questions. Please try again.');
    },
  });
}

// Get wrong answer explanation
export function useWrongAnswerExplanation() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (request: WrongAnswerRequest) => {
      const response = await post<ApiResponse<WrongAnswerExplanation>>(
        API_URLS.AI.EXPLAIN_WRONG,
        request
      );
      if (response.status !== 200) {
        throw new Error(response.message || 'Failed to get explanation');
      }
      return response.body;
    },
    onSuccess: (data) => {
      if (data?.questionId) {
        queryClient.setQueryData(aiKeys.explanation(data.questionId), data);
      }
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to get explanation. Please try again.');
    },
  });
}

// Get cached wrong answer explanation
export function useCachedExplanation(questionId: string) {
  return useQuery({
    queryKey: aiKeys.explanation(questionId),
    queryFn: async () => null as WrongAnswerExplanation | null,
    enabled: false, // Only used for cache reads
    staleTime: Infinity,
  });
}

// Get question hint
export function useQuestionHint() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (request: HintRequest) => {
      const response = await post<ApiResponse<HintResponse>>(
        API_URLS.AI.GET_HINT,
        {
          question_id: request.questionId,
          question_text: request.questionText,
          topic: request.topic,
          correct_answer: request.correctAnswer,
          solution: request.solution,
          hint_level: request.hintLevel,
        }
      );
      if (response.status !== 200) {
        throw new Error(response.message || 'Failed to get hint');
      }
      return response.body;
    },
    onSuccess: (data) => {
      if (data?.questionId) {
        queryClient.setQueryData(
          aiKeys.hint(data.questionId, data.hintLevel),
          data
        );
      }
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to get hint. Please try again.');
    },
  });
}

// Get cached hint for a question
export function useCachedHint(questionId: string, level: number) {
  return useQuery({
    queryKey: aiKeys.hint(questionId, level),
    queryFn: async () => null as HintResponse | null,
    enabled: false,
    staleTime: Infinity,
  });
}

// Validate question quality
export function useValidateQuestion() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (request: QuestionValidationRequest) => {
      const response = await post<ApiResponse<QuestionValidationResponse>>(
        API_URLS.AI.VALIDATE_QUESTION,
        request
      );
      if (response.status !== 200) {
        throw new Error(response.message || 'Failed to validate question');
      }
      return response.body;
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to validate question. Please try again.');
    },
  });
}

// Analyze exam performance
export function useExamAnalysis() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (request: ExamAnalysisRequest) => {
      // Convert camelCase to snake_case for Python backend
      // Also ensure arrays are never null (Python expects lists)
      const snakeCaseRequest = {
        user_id: request.userId,
        exam_id: request.examId,
        exam_name: request.examName || '',
        score: request.score,
        total_marks: request.totalMarks,
        time_taken_minutes: request.timeTakenMinutes,
        total_time_minutes: request.totalTimeMinutes,
        percentile: request.percentile,
        section_scores: (request.sectionScores || []).map(s => ({
          section_name: s.sectionName,
          score: s.score,
          max_score: s.maxScore,
          percentage: s.percentage,
          questions_attempted: s.questionsAttempted,
          questions_correct: s.questionsCorrect,
        })),
        question_results: (request.questionResults || []).map(q => ({
          question_id: q.questionId,
          topic: q.topic,
          subtopic: q.subtopic,
          difficulty: q.difficulty,
          student_answer: q.studentAnswer,
          correct_answer: q.correctAnswer,
          is_correct: q.isCorrect,
          time_taken_seconds: q.timeTakenSeconds,
          marks_obtained: q.marksObtained,
          max_marks: q.maxMarks,
        })),
        avg_score: request.avgScore,
        trend: request.trend,
        weak_areas: request.weakAreas || [],
        exams_count: request.examsCount,
      };

      const response = await post<ApiResponse<ExamAnalysisResponse>>(
        API_URLS.AI.ANALYZE_EXAM,
        snakeCaseRequest
      );
      if (response.status !== 200) {
        throw new Error(response.message || 'Failed to analyze exam');
      }
      return response.body;
    },
    onSuccess: (data, variables) => {
      if (data && variables.examId) {
        queryClient.setQueryData(aiKeys.examAnalysis(variables.examId), data);
      }
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to analyze exam. Please try again.');
    },
  });
}

// Get cached exam analysis
export function useCachedExamAnalysis(examId: string, enabled = true) {
  return useQuery({
    queryKey: aiKeys.examAnalysis(examId),
    queryFn: async () => null as ExamAnalysisResponse | null,
    enabled: enabled && !!examId,
    staleTime: 30 * 60 * 1000, // 30 minutes
  });
}

// =============================================================================
// ANALYSIS HISTORY HOOKS
// =============================================================================

export interface AnalysisHistoryItem {
  _id: string;
  user_id: string;
  exam_id: string;
  exam_name?: string;
  score: number;
  total_marks: number;
  percentage: number;
  overall_assessment: Record<string, unknown>;
  what_went_well: string[];
  areas_of_concern: Array<{
    area: string;
    score?: string;
    pattern: string;
    priority: string;
  }>;
  action_items: Array<{
    priority: number | string;
    action: string;
    topic: string;
  }>;
  analyzed_at: string;
  analysis_month: string;
}

export interface MonthlySummary {
  user_id: string;
  total_analyses: number;
  first_analysis?: string;
  last_analysis?: string;
  score_trend: 'improving' | 'declining' | 'stable';
  monthly_summaries: Array<{
    month: string;
    total_exams: number;
    avg_percentage: number;
    exams: Array<{
      exam_id: string;
      exam_name?: string;
      percentage: number;
      analyzed_at: string;
      performance_rating?: string;
    }>;
  }>;
}

export interface WeakAreasAnalysis {
  user_id: string;
  persistent_weak_areas: Array<{
    area: string;
    frequency: number;
    priority_score: number;
    patterns: string[];
  }>;
}

// Get user's analysis history
export function useAnalysisHistory(userId: string, limit = 20, skip = 0, enabled = true) {
  return useQuery({
    queryKey: [...aiKeys.analysisHistory(userId), limit, skip],
    queryFn: async () => {
      const response = await get<ApiResponse<AnalysisHistoryItem[]>>(
        `${API_URLS.AI.ANALYSIS_HISTORY}/${userId}?limit=${limit}&skip=${skip}`
      );
      return response.body || [];
    },
    enabled: enabled && !!userId,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Get monthly summary of analyses
export function useMonthlySummary(userId: string, months = 6, enabled = true) {
  return useQuery({
    queryKey: [...aiKeys.monthlySummary(userId), months],
    queryFn: async () => {
      const response = await get<ApiResponse<MonthlySummary>>(
        `${API_URLS.AI.ANALYSIS_MONTHLY}/${userId}?months=${months}`
      );
      return response.body;
    },
    enabled: enabled && !!userId,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Get weak areas analysis
export function useWeakAreasAnalysis(userId: string, enabled = true) {
  return useQuery({
    queryKey: aiKeys.weakAreas(userId),
    queryFn: async () => {
      const response = await get<ApiResponse<WeakAreasAnalysis>>(
        `${API_URLS.AI.ANALYSIS_WEAK_AREAS}/${userId}`
      );
      return response.body;
    },
    enabled: enabled && !!userId,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Get specific exam analysis from history
export function useExamAnalysisFromHistory(userId: string, examId: string, enabled = true) {
  return useQuery({
    queryKey: ['ai', 'exam-analysis-history', userId, examId],
    queryFn: async () => {
      const response = await get<ApiResponse<AnalysisHistoryItem>>(
        `${API_URLS.AI.ANALYSIS_BY_EXAM}/${userId}/${examId}`
      );
      return response.body;
    },
    enabled: enabled && !!userId && !!examId,
    staleTime: 30 * 60 * 1000, // 30 minutes
  });
}

// Generate study plan
export function useStudyPlan() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (request: StudyPlanRequest) => {
      const response = await post<ApiResponse<StudyPlanResponse>>(
        API_URLS.AI.STUDY_PLAN,
        request
      );
      if (response.status !== 200) {
        throw new Error(response.message || 'Failed to generate study plan');
      }
      return response.body;
    },
    onSuccess: (data, variables) => {
      if (data) {
        queryClient.setQueryData(
          aiKeys.studyPlan(variables.userId, variables.examName),
          data
        );
      }
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to generate study plan. Please try again.');
    },
  });
}

// Get cached study plan
export function useCachedStudyPlan(userId: string, examName: string, enabled = true) {
  return useQuery({
    queryKey: aiKeys.studyPlan(userId, examName),
    queryFn: async () => null as StudyPlanResponse | null,
    enabled: enabled && !!userId && !!examName,
    staleTime: 60 * 60 * 1000, // 1 hour
  });
}

// =============================================================================
// LLM Configuration Hooks (SUPERADMIN only) - Uses Java backend
// =============================================================================

// Get available LLM providers
export function useLLMProviders(enabled = true) {
  return useQuery({
    queryKey: aiKeys.llmProviders(),
    queryFn: async () => {
      // Java backend returns array directly (not wrapped in ApiResponse)
      const response = await get<LLMProviderInfo[] | null>(API_URLS.AI.LLM_PROVIDERS);
      return response || [];
    },
    enabled,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Get current LLM configuration
export function useLLMConfig(enabled = true) {
  return useQuery({
    queryKey: aiKeys.llmConfig(),
    queryFn: async () => {
      // Java backend returns config directly (not wrapped in ApiResponse)
      const response = await get<LLMConfig | null>(API_URLS.AI.LLM_CONFIG_CURRENT);
      // Return default config if no config exists
      return response || {
        provider: 'claude',
        model: 'claude-sonnet-4-5',
        temperature: 0.7,
        maxTokens: 4096,
        isActive: true,
      };
    },
    enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Update LLM configuration
export function useUpdateLLMConfig() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (config: LLMConfigUpdate) => {
      // Java backend returns updated config directly
      const response = await put<LLMConfig | null>(API_URLS.AI.LLM_CONFIG_UPDATE, config);
      if (!response) {
        throw new Error('Failed to update configuration');
      }
      return response;
    },
    onSuccess: (data) => {
      if (data) {
        queryClient.setQueryData(aiKeys.llmConfig(), data);
        showAlert('success', `LLM configuration updated to ${data.provider} / ${data.model}`);
      }
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to update LLM configuration');
    },
  });
}

// Test LLM provider
export function useTestLLMProvider() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async ({ provider, model }: { provider: string; model?: string }) => {
      const params = new URLSearchParams({ provider });
      if (model) params.append('model', model);
      // Java backend returns test result directly
      const response = await post<LLMProviderTestResult | null>(
        `${API_URLS.AI.LLM_CONFIG_TEST}?${params.toString()}`
      );
      return response;
    },
    onSuccess: (data) => {
      if (data?.success) {
        showAlert('success', `${data.provider} test passed in ${data.responseTimeMs}ms`);
      } else if (data) {
        showAlert('error', `${data.provider} test failed: ${data.message}`);
      }
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to test provider');
    },
  });
}
