"""
AI-Powered Performance Analysis and Study Plan Routes
"""
import json
from typing import List, Dict, Any, Optional
from fastapi import APIRouter, HTTPException, Depends, Query
from motor.motor_asyncio import AsyncIOMotorDatabase

from ..schemas import (
    ExamAnalysisRequest,
    ExamAnalysisResponse,
    AreaOfConcern,
    MistakeAnalysis,
    ActionItem,
    StudyPlanRequest,
    StudyPlanResponse,
    WeekPlan,
    DiagnosticRequest,
    DiagnosticResponse,
    TopicProficiency,
    PaperQualityRequest,
    PaperQualityResponse,
    QualityIssue,
)
from ..llm import get_llm_client
from ..prompts import (
    EXAM_ANALYSIS_SYSTEM,
    EXAM_ANALYSIS_PROMPT,
    STUDY_PLAN_SYSTEM,
    STUDY_PLAN_PROMPT,
    DIAGNOSTIC_ANALYSIS_PROMPT,
    PAPER_QUALITY_ANALYSIS_PROMPT,
)
from ..dependencies import get_current_user, get_db
from ..services.json_parser import parse_llm_json
from ..services.analysis_storage import (
    save_exam_analysis,
    get_user_analysis_history,
    get_user_monthly_summary,
    get_analysis_by_exam,
    get_user_weak_areas_trend,
)

router = APIRouter(prefix="/analysis", tags=["Analysis"])


def _format_section_scores(section_scores) -> str:
    """Format section scores for prompt"""
    lines = []
    for s in section_scores:
        lines.append(f"- {s.section_name}: {s.score}/{s.max_score} ({s.percentage}%) - {s.questions_correct}/{s.questions_attempted} correct")
    return "\n".join(lines)


def _format_question_results(question_results) -> str:
    """Format question results for prompt (summarized)"""
    # Group by topic
    by_topic = {}
    for q in question_results:
        if q.topic not in by_topic:
            by_topic[q.topic] = {"correct": 0, "wrong": 0, "total_time": 0}
        if q.is_correct:
            by_topic[q.topic]["correct"] += 1
        else:
            by_topic[q.topic]["wrong"] += 1
        by_topic[q.topic]["total_time"] += q.time_taken_seconds

    lines = []
    for topic, data in by_topic.items():
        total = data["correct"] + data["wrong"]
        avg_time = data["total_time"] / total if total > 0 else 0
        lines.append(f"- {topic}: {data['correct']}/{total} correct, avg {avg_time:.0f}s/question")

    return "\n".join(lines)


@router.post("/exam", response_model=ExamAnalysisResponse)
async def analyze_exam(
    request: ExamAnalysisRequest,
    user: dict = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
):
    """
    Comprehensive AI analysis of exam performance.

    Provides:
    - Pattern analysis of mistakes
    - Distinction between conceptual gaps and careless errors
    - Prioritized action items
    - Score improvement predictions

    Results are automatically saved to database for progress tracking.
    """
    try:
        client, default_model, _ = get_llm_client()

        percentage = (request.score / request.total_marks * 100) if request.total_marks > 0 else 0

        prompt = EXAM_ANALYSIS_PROMPT.format(
            exam_name=request.exam_name,
            score=request.score,
            total_marks=request.total_marks,
            percentage=f"{percentage:.1f}",
            time_taken=request.time_taken_minutes,
            total_time=request.total_time_minutes,
            percentile=request.percentile or "N/A",
            section_scores=_format_section_scores(request.section_scores),
            question_results=_format_question_results(request.question_results),
            avg_score=request.avg_score or "N/A",
            trend=request.trend or "unknown",
            weak_areas=", ".join(request.weak_areas) if request.weak_areas else "None identified",
            exams_count=request.exams_count or 1,
        )

        messages = [
            {"role": "system", "content": EXAM_ANALYSIS_SYSTEM},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.2,
            max_tokens=4096,
            json_mode=client.supports_json_mode(),
        )

        data = parse_llm_json(reply)

        response = ExamAnalysisResponse(
            overall_assessment=data.get("overall_assessment", {}),
            what_went_well=data.get("what_went_well", []),
            areas_of_concern=[AreaOfConcern(**a) for a in data.get("areas_of_concern", [])],
            mistake_analysis=MistakeAnalysis(**data.get("mistake_analysis", {
                "conceptual_gaps": {},
                "careless_errors": {},
                "time_management": {},
            })),
            action_items=[ActionItem(**a) for a in data.get("action_items", [])],
            predicted_improvement=data.get("predicted_improvement", {}),
            provider="default",
            model=default_model,
        )

        # Save analysis to database for history tracking
        await save_exam_analysis(db, request, response)

        return response

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# =============================================================================
# ANALYSIS HISTORY ENDPOINTS
# =============================================================================

@router.get("/history/{user_id}")
async def get_analysis_history(
    user_id: str,
    limit: int = Query(default=20, ge=1, le=100),
    skip: int = Query(default=0, ge=0),
    user: dict = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> List[Dict[str, Any]]:
    """
    Get user's exam analysis history.
    Returns list of all analyses, sorted by most recent first.
    """
    # Users can only view their own history (unless admin)
    user_roles = user.get("roles", [])
    is_admin = any(r in ["ADMIN", "SUPERADMIN", "ROLE_ADMIN", "ROLE_SUPERADMIN"] for r in user_roles)
    if user.get("user_id") != user_id and not is_admin:
        raise HTTPException(status_code=403, detail="Can only view your own analysis history")

    return await get_user_analysis_history(db, user_id, limit, skip)


@router.get("/monthly-summary/{user_id}")
async def get_monthly_summary(
    user_id: str,
    months: int = Query(default=6, ge=1, le=24),
    user: dict = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> Dict[str, Any]:
    """
    Get monthly summary of user's exam analyses.
    Shows progress over time with score trends.
    """
    user_roles = user.get("roles", [])
    is_admin = any(r in ["ADMIN", "SUPERADMIN", "ROLE_ADMIN", "ROLE_SUPERADMIN"] for r in user_roles)
    if user.get("user_id") != user_id and not is_admin:
        raise HTTPException(status_code=403, detail="Can only view your own analysis summary")

    return await get_user_monthly_summary(db, user_id, months)


@router.get("/exam/{user_id}/{exam_id}")
async def get_exam_analysis(
    user_id: str,
    exam_id: str,
    user: dict = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> Optional[Dict[str, Any]]:
    """
    Get the most recent analysis for a specific exam.
    """
    user_roles = user.get("roles", [])
    is_admin = any(r in ["ADMIN", "SUPERADMIN", "ROLE_ADMIN", "ROLE_SUPERADMIN"] for r in user_roles)
    if user.get("user_id") != user_id and not is_admin:
        raise HTTPException(status_code=403, detail="Can only view your own analysis")

    result = await get_analysis_by_exam(db, user_id, exam_id)
    if not result:
        raise HTTPException(status_code=404, detail="Analysis not found for this exam")
    return result


@router.get("/weak-areas/{user_id}")
async def get_weak_areas_analysis(
    user_id: str,
    user: dict = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> Dict[str, Any]:
    """
    Get analysis of user's persistent weak areas across all exams.
    Helps identify areas that need focused attention.
    """
    user_roles = user.get("roles", [])
    is_admin = any(r in ["ADMIN", "SUPERADMIN", "ROLE_ADMIN", "ROLE_SUPERADMIN"] for r in user_roles)
    if user.get("user_id") != user_id and not is_admin:
        raise HTTPException(status_code=403, detail="Can only view your own weak areas")

    return await get_user_weak_areas_trend(db, user_id)


@router.post("/study-plan", response_model=StudyPlanResponse)
async def generate_study_plan(
    request: StudyPlanRequest,
    user: dict = Depends(get_current_user),
):
    """
    Generate personalized AI study plan.

    Creates:
    - Week-by-week study schedule
    - Daily activity breakdown
    - Spaced repetition schedule
    - Mock test schedule
    """
    try:
        client, default_model, _ = get_llm_client()

        # Calculate days remaining (handle optional exam_date)
        from datetime import datetime, timedelta
        days_remaining = 30  # Default to 30 days
        exam_date_str = request.exam_date or ""

        if exam_date_str:
            try:
                exam_date = datetime.fromisoformat(exam_date_str.replace("Z", "+00:00"))
                days_remaining = max(1, (exam_date - datetime.now(exam_date.tzinfo)).days)
            except (ValueError, AttributeError):
                # If date parsing fails, use default
                exam_date_str = (datetime.now() + timedelta(days=30)).strftime("%Y-%m-%d")
        else:
            exam_date_str = (datetime.now() + timedelta(days=30)).strftime("%Y-%m-%d")

        # Get values using helper methods (handles field aliases)
        weak_areas = request.get_weak_areas()
        strong_areas = request.get_strong_areas()
        hours_per_day = request.get_hours_per_day()
        current_score = request.get_current_score()

        # Format topic mastery (handle optional/empty)
        topic_mastery_list = request.topic_mastery or []
        if topic_mastery_list:
            topic_mastery_str = "\n".join([
                f"- {t.topic}: {t.mastery_percentage}% mastery, {t.question_count} questions practiced"
                for t in topic_mastery_list
            ])
        else:
            topic_mastery_str = "No specific topic mastery data provided"

        prompt = STUDY_PLAN_PROMPT.format(
            exam_name=request.exam_name,
            exam_date=exam_date_str,
            days_remaining=days_remaining,
            current_score=current_score,
            hours_per_day=hours_per_day,
            preferred_times=", ".join(request.preferred_times) if request.preferred_times else "flexible",
            topic_mastery=topic_mastery_str,
            trend="stable",  # Would come from historical data
            weak_areas=", ".join(weak_areas) if weak_areas else "None specified",
            strong_areas=", ".join(strong_areas) if strong_areas else "None specified",
        )

        messages = [
            {"role": "system", "content": STUDY_PLAN_SYSTEM},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.3,
            max_tokens=8000,
            json_mode=client.supports_json_mode(),
        )

        data = parse_llm_json(reply)

        return StudyPlanResponse(
            plan_overview=data.get("plan_overview", {}),
            weekly_plan=[WeekPlan(**w) for w in data.get("weekly_plan", [])],
            revision_schedule=data.get("revision_schedule", {}),
            mock_test_schedule=data.get("mock_test_schedule", []),
            daily_tips=data.get("daily_tips", []),
            adjustment_triggers=data.get("adjustment_triggers", []),
            provider="default",
            model=default_model,
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/diagnostic", response_model=DiagnosticResponse)
async def analyze_diagnostic(
    request: DiagnosticRequest,
    user: dict = Depends(get_current_user),
):
    """
    Analyze diagnostic assessment to create initial student profile.

    Determines:
    - Overall level (beginner/intermediate/advanced)
    - Topic-wise proficiency
    - Learning style indicators
    - Recommended starting point
    """
    try:
        client, default_model, _ = get_llm_client()

        # Format assessment results
        results_str = "\n".join([
            f"- {r.topic}/{r.subtopic or 'general'}: {'✓' if r.is_correct else '✗'} ({r.time_taken_seconds}s)"
            for r in request.assessment_results
        ])

        # Format time analysis
        time_str = "\n".join([
            f"- {topic}: avg {time:.0f}s"
            for topic, time in request.time_analysis.items()
        ])

        prompt = DIAGNOSTIC_ANALYSIS_PROMPT.format(
            assessment_results=results_str,
            time_analysis=time_str,
            categories=", ".join(request.categories_attempted),
        )

        messages = [
            {"role": "system", "content": "You are an expert at assessing student abilities. Respond with JSON only."},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.2,
            json_mode=client.supports_json_mode(),
        )

        data = parse_llm_json(reply)

        # Parse topic proficiency
        topic_proficiency = {}
        for topic, prof_data in data.get("topic_proficiency", {}).items():
            topic_proficiency[topic] = TopicProficiency(**prof_data)

        return DiagnosticResponse(
            overall_level=data.get("overall_level", "intermediate"),
            estimated_percentile=data.get("estimated_percentile", 50),
            topic_proficiency=topic_proficiency,
            learning_style_indicators=data.get("learning_style_indicators", {}),
            recommended_starting_point=data.get("recommended_starting_point", {}),
            realistic_timeline=data.get("realistic_timeline", {}),
            provider="default",
            model=default_model,
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/paper-quality", response_model=PaperQualityResponse)
async def analyze_paper_quality(
    request: PaperQualityRequest,
    user: dict = Depends(get_current_user),
):
    """
    Analyze question paper for quality and exam-readiness.

    Checks:
    - Exam pattern match
    - Difficulty distribution
    - Topic coverage
    - Time feasibility
    """
    try:
        client, default_model, _ = get_llm_client()

        prompt = PAPER_QUALITY_ANALYSIS_PROMPT.format(
            exam_type=request.exam_type.value,
            total_questions=request.total_questions,
            total_time=request.total_time_minutes,
            total_marks=request.total_marks,
            question_distribution=json.dumps(request.question_distribution, indent=2),
            sample_questions=json.dumps(request.sample_questions[:5], indent=2),  # Limit to 5 samples
        )

        messages = [
            {"role": "system", "content": "You are an expert exam paper reviewer. Analyze thoroughly and respond with JSON."},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.2,
            json_mode=client.supports_json_mode(),
        )

        data = parse_llm_json(reply)

        return PaperQualityResponse(
            overall_quality_score=data.get("overall_quality_score", 7.0),
            exam_pattern_match=data.get("exam_pattern_match", {}),
            difficulty_analysis=data.get("difficulty_analysis", {}),
            topic_coverage=data.get("topic_coverage", {}),
            time_feasibility=data.get("time_feasibility", {}),
            quality_issues=[QualityIssue(**q) for q in data.get("quality_issues", [])],
            recommendations=data.get("recommendations", []),
            provider="default",
            model=default_model,
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
