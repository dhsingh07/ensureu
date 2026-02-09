"""
Pydantic schemas for request/response models
"""
from typing import List, Dict, Optional, Any, Union
from pydantic import BaseModel, Field, field_validator
from enum import Enum


# =============================================================================
# ENUMS
# =============================================================================

class DifficultyLevel(str, Enum):
    EASY = "easy"
    MEDIUM = "medium"
    HARD = "hard"


class ExamType(str, Enum):
    SSC_CGL = "SSC_CGL"
    SSC_CHSL = "SSC_CHSL"
    SSC_CPO = "SSC_CPO"
    BANK_PO = "BANK_PO"
    BANK_CLERK = "BANK_CLERK"
    RAILWAY = "RAILWAY"


class PerformanceRating(str, Enum):
    EXCELLENT = "excellent"
    GOOD = "good"
    AVERAGE = "average"
    NEEDS_IMPROVEMENT = "needs_improvement"


# =============================================================================
# LLM SCHEMAS
# =============================================================================

class ChatMessage(BaseModel):
    role: str = Field(..., description="Message role: system, user, or assistant")
    content: str = Field(..., description="Message content")


class ChatRequest(BaseModel):
    messages: List[ChatMessage]
    provider: Optional[str] = None
    model: Optional[str] = None
    temperature: float = Field(default=0.2, ge=0, le=2)
    max_tokens: int = Field(default=4096, ge=1, le=32000)
    guardrails: bool = True


class ChatResponse(BaseModel):
    provider: str
    model: str
    reply: str


class EmbedRequest(BaseModel):
    texts: List[str]
    provider: Optional[str] = None
    model: Optional[str] = None


class EmbedResponse(BaseModel):
    provider: str
    model: str
    embeddings: List[List[float]]


# =============================================================================
# QUESTION GENERATION SCHEMAS
# =============================================================================

class QuestionGenerateRequest(BaseModel):
    exam_type: ExamType
    topic: str
    subtopic: str
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    count: int = Field(default=1, ge=1, le=10)


class GeneratedOption(BaseModel):
    text: str
    is_correct: bool = False


class DistractorExplanation(BaseModel):
    option: str
    explanation: str


class GeneratedQuestion(BaseModel):
    question: str
    options: Dict[str, str]
    correct_answer: str
    solution: str
    concepts_tested: List[str]
    estimated_time_seconds: int
    difficulty_score: float
    distractor_explanations: Optional[Dict[str, str]] = None


class QuestionGenerateResponse(BaseModel):
    questions: List[GeneratedQuestion]
    provider: str
    model: str


# =============================================================================
# WRONG ANSWER EXPLANATION SCHEMAS
# =============================================================================

class WrongAnswerRequest(BaseModel):
    question_id: str
    question_text: str
    options: Dict[str, str]  # {"A": "...", "B": "..."}
    student_answer: str
    correct_answer: str
    topic: str
    subtopic: Optional[str] = None
    user_id: Optional[str] = None


class PracticeQuestion(BaseModel):
    question: str
    answer: str


class WrongAnswerExplanation(BaseModel):
    encouragement: str
    why_wrong: str
    correct_approach: str
    key_concept: str
    memory_tip: str
    similar_practice: Optional[PracticeQuestion] = None
    misconception_identified: Optional[str] = None


class WrongAnswerResponse(BaseModel):
    explanation: WrongAnswerExplanation
    provider: str
    model: str


# =============================================================================
# EXAM ANALYSIS SCHEMAS
# =============================================================================

class QuestionResult(BaseModel):
    question_id: str
    topic: str
    subtopic: Optional[str] = None
    difficulty: DifficultyLevel
    student_answer: Optional[str] = None
    correct_answer: str
    is_correct: bool
    time_taken_seconds: int
    marks_obtained: float
    max_marks: float


class SectionScore(BaseModel):
    section_name: str
    score: float
    max_score: float
    percentage: float
    questions_attempted: int
    questions_correct: int


class ExamAnalysisRequest(BaseModel):
    exam_id: str
    exam_name: str
    user_id: str
    score: float
    total_marks: float
    time_taken_minutes: int
    total_time_minutes: int
    percentile: Optional[float] = None
    section_scores: List[SectionScore]
    question_results: List[QuestionResult]
    # Historical data
    avg_score: Optional[float] = None
    trend: Optional[str] = None  # improving/declining/stable
    weak_areas: Optional[List[str]] = None
    exams_count: Optional[int] = None


class AreaOfConcern(BaseModel):
    area: str = ""
    score: Optional[str] = ""
    pattern: str = ""
    evidence: Optional[Union[str, List[str]]] = ""
    priority: str = "medium"  # high/medium/low

    @field_validator('evidence', mode='before')
    @classmethod
    def convert_evidence_to_string(cls, v):
        """Convert list of evidence to comma-separated string, handle None"""
        if v is None:
            return ""
        if isinstance(v, list):
            return "; ".join(str(item) for item in v if item)
        return str(v)

    @field_validator('score', mode='before')
    @classmethod
    def convert_score(cls, v):
        """Handle None score"""
        if v is None:
            return ""
        return str(v)


class MistakeAnalysis(BaseModel):
    conceptual_gaps: Dict[str, Any]
    careless_errors: Dict[str, Any]
    time_management: Dict[str, Any]


class ActionItem(BaseModel):
    priority: Union[int, str] = 1
    action: str = ""
    topic: str = ""
    estimated_time: str = ""
    expected_impact: str = ""

    @field_validator('priority', mode='before')
    @classmethod
    def convert_priority(cls, v):
        """Convert string priority to int"""
        if isinstance(v, str):
            try:
                return int(v)
            except ValueError:
                # Map text priorities to numbers
                priority_map = {'high': 1, 'medium': 2, 'low': 3}
                return priority_map.get(v.lower(), 2)
        return v


class ExamAnalysisResponse(BaseModel):
    overall_assessment: Dict[str, Any]  # Allow Any for flexible LLM outputs
    what_went_well: List[str] = []
    areas_of_concern: List[AreaOfConcern] = []
    mistake_analysis: Optional[MistakeAnalysis] = None
    action_items: List[ActionItem] = []
    predicted_improvement: Dict[str, Any] = {}  # Allow Any for flexible LLM outputs
    provider: str
    model: str


# =============================================================================
# STUDY PLAN SCHEMAS
# =============================================================================

class TopicMastery(BaseModel):
    topic: str
    mastery_percentage: float
    last_practiced: Optional[str] = None
    question_count: int = 0


class StudyPlanRequest(BaseModel):
    user_id: str
    exam_name: str
    exam_date: Optional[str] = None  # ISO date string, optional
    current_score: Optional[float] = None  # Can be derived from level
    current_level: Optional[str] = "intermediate"  # beginner/intermediate/advanced
    hours_per_day: float = Field(default=2, ge=0.5, le=12)
    preferred_times: Optional[List[str]] = None
    topic_mastery: Optional[List[TopicMastery]] = []  # Made optional with default
    weak_areas: Optional[List[str]] = []  # Made optional with default
    strong_areas: Optional[List[str]] = []  # Made optional with default
    # Aliases for frontend compatibility
    weak_topics: Optional[List[str]] = None
    strong_topics: Optional[List[str]] = None
    available_hours_per_day: Optional[float] = None

    def get_weak_areas(self) -> List[str]:
        """Get weak areas from either field"""
        return self.weak_areas or self.weak_topics or []

    def get_strong_areas(self) -> List[str]:
        """Get strong areas from either field"""
        return self.strong_areas or self.strong_topics or []

    def get_hours_per_day(self) -> float:
        """Get hours per day from either field"""
        return self.available_hours_per_day or self.hours_per_day or 2.0

    def get_current_score(self) -> float:
        """Get current score, derive from level if not provided"""
        if self.current_score is not None:
            return self.current_score
        # Derive from level
        level_scores = {"beginner": 30.0, "intermediate": 50.0, "advanced": 70.0}
        return level_scores.get(self.current_level or "intermediate", 50.0)


class DailyActivity(BaseModel):
    type: str  # concept_review, practice, revision, mock_test
    topic: str
    duration_mins: int
    question_count: Optional[int] = None


class DaySchedule(BaseModel):
    day: str
    topics: List[str]
    activities: List[DailyActivity]
    total_hours: float


class WeekPlan(BaseModel):
    week: int
    theme: str
    goals: List[str]
    daily_schedule: List[DaySchedule]
    weekly_test: Optional[Dict[str, Any]] = None


class StudyPlanResponse(BaseModel):
    plan_overview: Dict[str, Any]
    weekly_plan: List[WeekPlan]
    revision_schedule: Dict[str, Any]
    mock_test_schedule: List[Dict[str, Any]]
    daily_tips: List[str]
    adjustment_triggers: List[str]
    provider: str
    model: str


# =============================================================================
# HINT GENERATION SCHEMAS
# =============================================================================

class HintRequest(BaseModel):
    question_id: str
    question_text: str
    topic: str
    correct_answer: str
    solution: str
    hint_level: int = Field(default=1, ge=1, le=3)


class HintLevel(BaseModel):
    hint: str
    concept_pointer: Optional[str] = None
    formula_reminder: Optional[str] = None
    partial_solution: Optional[str] = None


class HintResponse(BaseModel):
    hint_level_1: HintLevel
    hint_level_2: Optional[HintLevel] = None
    hint_level_3: Optional[HintLevel] = None
    provider: str
    model: str


# =============================================================================
# PAPER QUALITY SCHEMAS
# =============================================================================

class PaperQualityRequest(BaseModel):
    paper_id: str
    exam_type: ExamType
    total_questions: int
    total_time_minutes: int
    total_marks: float
    question_distribution: Dict[str, int]  # topic -> count
    sample_questions: List[Dict[str, Any]]  # List of question objects


class QualityIssue(BaseModel):
    question_id: str
    issue: str
    suggestion: str


class PaperQualityResponse(BaseModel):
    overall_quality_score: float
    exam_pattern_match: Dict[str, Any]
    difficulty_analysis: Dict[str, Any]
    topic_coverage: Dict[str, Any]
    time_feasibility: Dict[str, Any]
    quality_issues: List[QualityIssue]
    recommendations: List[str]
    provider: str
    model: str


# =============================================================================
# DIAGNOSTIC ASSESSMENT SCHEMAS
# =============================================================================

class DiagnosticRequest(BaseModel):
    user_id: str
    assessment_results: List[QuestionResult]
    time_analysis: Dict[str, float]  # topic -> avg_time
    categories_attempted: List[str]


class TopicProficiency(BaseModel):
    level: str  # weak/moderate/strong
    mastery_score: float
    subtopic_breakdown: Dict[str, float]


class DiagnosticResponse(BaseModel):
    overall_level: str  # beginner/intermediate/advanced
    estimated_percentile: float
    topic_proficiency: Dict[str, TopicProficiency]
    learning_style_indicators: Dict[str, str]
    recommended_starting_point: Dict[str, Any]
    realistic_timeline: Dict[str, str]
    provider: str
    model: str
