"""
MongoDB model for storing exam analysis results
"""
from datetime import datetime
from typing import Optional, List, Dict, Any
from pydantic import BaseModel, Field


class StoredExamAnalysis(BaseModel):
    """Model for storing exam analysis in MongoDB"""

    # Identifiers
    user_id: str
    exam_id: str
    exam_name: Optional[str] = None

    # Original exam data
    score: float
    total_marks: float
    percentage: float
    time_taken_minutes: int
    total_time_minutes: int

    # AI Analysis results
    overall_assessment: Dict[str, Any] = {}
    what_went_well: List[str] = []
    areas_of_concern: List[Dict[str, Any]] = []
    mistake_analysis: Optional[Dict[str, Any]] = None
    action_items: List[Dict[str, Any]] = []
    predicted_improvement: Dict[str, Any] = {}

    # LLM info
    provider: str = "default"
    model: str = ""

    # Timestamps
    analyzed_at: datetime = Field(default_factory=datetime.utcnow)

    # For monthly grouping
    analysis_month: str = ""  # Format: "YYYY-MM"

    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat()
        }


class UserAnalysisSummary(BaseModel):
    """Summary of user's analysis history"""
    user_id: str
    total_analyses: int
    first_analysis: Optional[datetime] = None
    last_analysis: Optional[datetime] = None

    # Progress metrics
    avg_score_percentage: float = 0.0
    score_trend: str = "stable"  # improving, declining, stable

    # Monthly breakdown
    monthly_analyses: List[Dict[str, Any]] = []
