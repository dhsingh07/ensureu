"""
Service for storing and retrieving exam analysis results from MongoDB
"""
from datetime import datetime
from typing import List, Optional, Dict, Any
from motor.motor_asyncio import AsyncIOMotorDatabase
import logging

from ..models.exam_analysis import StoredExamAnalysis, UserAnalysisSummary
from ..schemas import ExamAnalysisRequest, ExamAnalysisResponse

logger = logging.getLogger(__name__)

COLLECTION_NAME = "user_exam_analyses"


async def save_exam_analysis(
    db: AsyncIOMotorDatabase,
    request: ExamAnalysisRequest,
    response: ExamAnalysisResponse,
) -> str:
    """
    Save exam analysis result to MongoDB.
    Returns the inserted document ID.
    """
    percentage = (request.score / request.total_marks * 100) if request.total_marks > 0 else 0
    now = datetime.utcnow()
    analysis_month = now.strftime("%Y-%m")

    doc = {
        "user_id": request.user_id,
        "exam_id": request.exam_id,
        "exam_name": request.exam_name,
        "score": request.score,
        "total_marks": request.total_marks,
        "percentage": round(percentage, 2),
        "time_taken_minutes": request.time_taken_minutes,
        "total_time_minutes": request.total_time_minutes,
        "overall_assessment": response.overall_assessment,
        "what_went_well": response.what_went_well,
        "areas_of_concern": [ac.model_dump() for ac in response.areas_of_concern],
        "mistake_analysis": response.mistake_analysis.model_dump() if response.mistake_analysis else None,
        "action_items": [ai.model_dump() for ai in response.action_items],
        "predicted_improvement": response.predicted_improvement or {},
        "provider": response.provider,
        "model": response.model,
        "analyzed_at": now,
        "analysis_month": analysis_month,
    }

    result = await db[COLLECTION_NAME].insert_one(doc)
    logger.info(f"Saved exam analysis for user {request.user_id}, exam {request.exam_id}")
    return str(result.inserted_id)


async def get_user_analysis_history(
    db: AsyncIOMotorDatabase,
    user_id: str,
    limit: int = 20,
    skip: int = 0,
) -> List[Dict[str, Any]]:
    """
    Get user's exam analysis history, sorted by most recent first.
    """
    cursor = db[COLLECTION_NAME].find(
        {"user_id": user_id}
    ).sort("analyzed_at", -1).skip(skip).limit(limit)

    results = []
    async for doc in cursor:
        doc["_id"] = str(doc["_id"])
        results.append(doc)

    return results


async def get_user_monthly_summary(
    db: AsyncIOMotorDatabase,
    user_id: str,
    months: int = 6,
) -> Dict[str, Any]:
    """
    Get monthly summary of user's exam analyses.
    Shows progress over the last N months.
    """
    # Aggregate by month
    pipeline = [
        {"$match": {"user_id": user_id}},
        {"$group": {
            "_id": "$analysis_month",
            "total_exams": {"$sum": 1},
            "avg_percentage": {"$avg": "$percentage"},
            "total_score": {"$sum": "$score"},
            "total_max_marks": {"$sum": "$total_marks"},
            "exams": {"$push": {
                "exam_id": "$exam_id",
                "exam_name": "$exam_name",
                "percentage": "$percentage",
                "analyzed_at": "$analyzed_at",
                "performance_rating": "$overall_assessment.performance_rating",
            }},
        }},
        {"$sort": {"_id": -1}},
        {"$limit": months},
    ]

    results = []
    async for doc in db[COLLECTION_NAME].aggregate(pipeline):
        results.append({
            "month": doc["_id"],
            "total_exams": doc["total_exams"],
            "avg_percentage": round(doc["avg_percentage"], 2),
            "exams": doc["exams"],
        })

    # Calculate overall trend
    trend = "stable"
    if len(results) >= 2:
        recent_avg = results[0]["avg_percentage"]
        older_avg = results[-1]["avg_percentage"]
        diff = recent_avg - older_avg
        if diff > 5:
            trend = "improving"
        elif diff < -5:
            trend = "declining"

    # Get overall stats
    total_count = await db[COLLECTION_NAME].count_documents({"user_id": user_id})
    first_doc = await db[COLLECTION_NAME].find_one(
        {"user_id": user_id},
        sort=[("analyzed_at", 1)]
    )
    last_doc = await db[COLLECTION_NAME].find_one(
        {"user_id": user_id},
        sort=[("analyzed_at", -1)]
    )

    return {
        "user_id": user_id,
        "total_analyses": total_count,
        "first_analysis": first_doc["analyzed_at"].isoformat() if first_doc else None,
        "last_analysis": last_doc["analyzed_at"].isoformat() if last_doc else None,
        "score_trend": trend,
        "monthly_summaries": results,
    }


async def get_analysis_by_exam(
    db: AsyncIOMotorDatabase,
    user_id: str,
    exam_id: str,
) -> Optional[Dict[str, Any]]:
    """
    Get the most recent analysis for a specific exam.
    """
    doc = await db[COLLECTION_NAME].find_one(
        {"user_id": user_id, "exam_id": exam_id},
        sort=[("analyzed_at", -1)]
    )
    if doc:
        doc["_id"] = str(doc["_id"])
    return doc


async def get_user_weak_areas_trend(
    db: AsyncIOMotorDatabase,
    user_id: str,
) -> Dict[str, Any]:
    """
    Analyze user's weak areas across all their exam analyses.
    Helps identify persistent problem areas.
    """
    pipeline = [
        {"$match": {"user_id": user_id}},
        {"$unwind": "$areas_of_concern"},
        {"$group": {
            "_id": "$areas_of_concern.area",
            "frequency": {"$sum": 1},
            "avg_priority": {"$avg": {
                "$switch": {
                    "branches": [
                        {"case": {"$eq": ["$areas_of_concern.priority", "high"]}, "then": 3},
                        {"case": {"$eq": ["$areas_of_concern.priority", "medium"]}, "then": 2},
                        {"case": {"$eq": ["$areas_of_concern.priority", "low"]}, "then": 1},
                    ],
                    "default": 2
                }
            }},
            "patterns": {"$push": "$areas_of_concern.pattern"},
        }},
        {"$sort": {"frequency": -1}},
        {"$limit": 10},
    ]

    weak_areas = []
    async for doc in db[COLLECTION_NAME].aggregate(pipeline):
        weak_areas.append({
            "area": doc["_id"],
            "frequency": doc["frequency"],
            "priority_score": round(doc["avg_priority"], 2),
            "patterns": doc["patterns"][:3],  # Show top 3 patterns
        })

    return {
        "user_id": user_id,
        "persistent_weak_areas": weak_areas,
    }
