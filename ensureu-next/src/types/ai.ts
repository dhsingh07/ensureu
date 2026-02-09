// AI Service Types - matching Java DTOs and Python schemas

// Question Generation
export interface QuestionGenerateRequest {
  topic: string;
  difficulty?: 'easy' | 'medium' | 'hard';
  count?: number;
  examType?: string;
  questionType?: 'MCQ' | 'MSQ' | 'TRUE_FALSE';
  language?: 'en' | 'hi';
}

export interface GeneratedQuestion {
  question: string;
  options: string[];
  correctAnswer: number | number[];
  explanation: string;
  topic?: string;
  difficulty?: string;
  questionHindi?: string;
  optionsHindi?: string[];
  explanationHindi?: string;
}

export interface QuestionGenerateResponse {
  questions: GeneratedQuestion[];
  topic: string;
  examType?: string;
  generatedAt: string;
}

// Wrong Answer Explanation
export interface WrongAnswerRequest {
  questionId: string;
  questionText: string;
  options: string[];
  userAnswer: number | number[];
  correctAnswer: number | number[];
  topic?: string;
  existingSolution?: string;
  language?: 'en' | 'hi';
}

export interface WrongAnswerExplanation {
  questionId: string;
  whyWrong: string;
  whyCorrect: string;
  conceptExplanation: string;
  relatedTopics: string[];
  studyTip: string;
  difficulty?: string;
  commonMistake?: string;
}

// Question Hint
export interface HintRequest {
  questionId: string;
  questionText: string;
  topic?: string;
  correctAnswer?: string;
  solution?: string;
  hintLevel: number;
}

export interface HintResponse {
  questionId: string;
  hint: string;
  hintLevel: number;
  maxLevel: number;
  hasMoreHints: boolean;
}

// Question Validation
export interface QuestionValidationRequest {
  question: string;
  options: string[];
  correctAnswer: number | number[];
  explanation?: string;
  topic?: string;
  examType?: string;
}

export interface QuestionValidationResponse {
  isValid: boolean;
  overallScore: number;
  issues: {
    type: 'error' | 'warning' | 'suggestion';
    field: string;
    message: string;
  }[];
  suggestions: string[];
  clarityScore: number;
  accuracyScore: number;
  examReadinessScore: number;
}

// Exam Analysis - matches Java ExamAnalysisRequest DTO
export interface ExamAnalysisRequest {
  userId: string;
  examId: string;
  examName?: string;
  score: number;           // obtained score
  totalMarks: number;      // maximum possible score
  timeTakenMinutes: number;
  totalTimeMinutes: number;
  percentile?: number;
  sectionScores: {
    sectionName: string;
    score: number;
    maxScore: number;
    percentage?: number;
    questionsAttempted?: number;
    questionsCorrect?: number;
  }[];
  questionResults?: {
    questionId: string;
    topic?: string;
    subtopic?: string;
    difficulty?: string;
    studentAnswer?: string;
    correctAnswer?: string;
    isCorrect: boolean;
    timeTakenSeconds?: number;
    marksObtained?: number;
    maxMarks?: number;
  }[];
  avgScore?: number;
  trend?: string;
  weakAreas?: string[];
  examsCount?: number;
}

// Response from Python AI service - matches Python schemas.py ExamAnalysisResponse
export interface ExamAnalysisResponse {
  // Overall assessment from LLM
  overall_assessment: {
    performance_rating?: string;  // excellent, good, average, needs_improvement
    key_strength?: string | null;
    critical_weakness?: string;
    summary?: string;
    [key: string]: string | null | undefined;  // Allow flexible LLM outputs
  };
  // What went well - list of positive observations
  what_went_well: string[];
  // Areas needing improvement
  areas_of_concern: {
    area: string;
    score?: string;
    pattern: string;
    evidence?: string;
    priority: string;  // high, medium, low
  }[];
  // Detailed mistake analysis
  mistake_analysis?: {
    conceptual_gaps?: Record<string, unknown>;
    careless_errors?: Record<string, unknown>;
    time_management?: Record<string, unknown>;
  } | null;
  // Recommended actions
  action_items: {
    priority: number | string;  // 1, 2, 3 or "high", "medium", "low"
    action: string;
    topic: string;
    estimated_time?: string;
    expected_impact?: string;
  }[];
  // Future improvement predictions
  predicted_improvement?: Record<string, unknown>;
  // LLM provider info
  provider: string;
  model: string;
}

// Study Plan
export interface StudyPlanRequest {
  userId: string;
  examName: string;
  examDate?: string;
  currentLevel?: 'beginner' | 'intermediate' | 'advanced';
  availableHoursPerDay?: number;
  weakTopics?: string[];
  strongTopics?: string[];
  previousScores?: {
    examName: string;
    score: number;
    date: string;
  }[];
}

export interface StudyPlanResponse {
  userId: string;
  examName: string;
  planDuration: string;
  weeklySchedule: {
    week: number;
    focus: string;
    dailyTasks: {
      day: string;
      topics: string[];
      duration: string;
      activities: string[];
    }[];
    weeklyGoal: string;
    assessment?: string;
  }[];
  priorityTopics: {
    topic: string;
    importance: 'critical' | 'important' | 'good-to-know';
    suggestedTime: string;
    resources?: string[];
  }[];
  milestones: {
    week: number;
    milestone: string;
    targetScore?: number;
  }[];
  tips: string[];
  generatedAt: string;
}

// AI Service Health
export interface AIHealthResponse {
  status: 'healthy' | 'degraded' | 'unavailable';
  provider?: string;
  model?: string;
  features: {
    questionGeneration: boolean;
    wrongAnswerExplanation: boolean;
    hints: boolean;
    examAnalysis: boolean;
    studyPlan: boolean;
  };
}

// =============================================================================
// LLM Configuration Types
// =============================================================================

export interface LLMModelDetail {
  id: string;
  name: string;
  contextWindow: number;
  tier: 'fast' | 'balanced' | 'powerful';
  description: string;
}

export interface LLMProviderInfo {
  id: string;
  name: string;
  description: string;
  baseUrl: string;
  apiKeyEnvVar: string | null;
  models: string[];
  modelDetails: LLMModelDetail[];
  supportsEmbeddings: boolean;
  supportsJsonMode: boolean;
  configured: boolean;
  isLocal: boolean;
}

export interface LLMConfig {
  provider: string;
  model: string;
  embedModel?: string;
  temperature: number;
  maxTokens: number;
  updatedAt?: string;
  updatedBy?: string;
  isActive?: boolean;
}

export interface LLMConfigUpdate {
  provider: string;
  model: string;
  embedModel?: string;
  temperature?: number;
  maxTokens?: number;
}

export interface LLMProviderTestResult {
  success: boolean;
  provider: string;
  model: string;
  message: string;
  responseTimeMs?: number;
}
