"""
AI Question Generation and Analysis Routes
"""
import json
from fastapi import APIRouter, HTTPException, Depends

from ..schemas import (
    QuestionGenerateRequest,
    QuestionGenerateResponse,
    GeneratedQuestion,
    WrongAnswerRequest,
    WrongAnswerResponse,
    WrongAnswerExplanation,
    HintRequest,
    HintResponse,
    HintLevel,
)
from ..llm import get_llm_client
from ..prompts import (
    QUESTION_GENERATION_SYSTEM,
    QUESTION_GENERATION_PROMPT,
    BULK_QUESTION_GENERATION_PROMPT,
    WRONG_ANSWER_EXPLANATION_SYSTEM,
    WRONG_ANSWER_EXPLANATION_PROMPT,
    HINT_GENERATION_PROMPT,
)
from ..dependencies import get_current_user
from ..services.json_parser import parse_llm_json

router = APIRouter(prefix="/questions", tags=["Questions"])


@router.post("/generate", response_model=QuestionGenerateResponse)
async def generate_questions(
    request: QuestionGenerateRequest,
    user: dict = Depends(get_current_user),
):
    """
    Generate exam questions using AI.

    - Generates questions matching exam pattern
    - Includes solution and distractor explanations
    - Supports bulk generation (up to 10 questions)
    """
    try:
        client, default_model, _ = get_llm_client()

        if request.count == 1:
            # Single question generation
            prompt = QUESTION_GENERATION_PROMPT.format(
                exam_type=request.exam_type.value,
                topic=request.topic,
                subtopic=request.subtopic,
                difficulty=request.difficulty.value,
            )
        else:
            # Bulk generation
            difficulty_dist = f"{request.difficulty.value} difficulty for all"
            prompt = BULK_QUESTION_GENERATION_PROMPT.format(
                count=request.count,
                exam_type=request.exam_type.value,
                topic=request.topic,
                subtopic=request.subtopic,
                difficulty_distribution=difficulty_dist,
            )

        messages = [
            {"role": "system", "content": QUESTION_GENERATION_SYSTEM},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.3,  # Slightly higher for variety
            json_mode=client.supports_json_mode(),
        )

        # Parse JSON response
        data = parse_llm_json(reply)

        # Normalize response format
        if request.count == 1:
            questions = [GeneratedQuestion(**data)]
        else:
            questions = [GeneratedQuestion(**q) for q in data.get("questions", [])]

        return QuestionGenerateResponse(
            questions=questions,
            provider="default",
            model=default_model,
        )

    except json.JSONDecodeError as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to parse AI response as JSON: {str(e)}",
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/explain-wrong", response_model=WrongAnswerResponse)
async def explain_wrong_answer(
    request: WrongAnswerRequest,
    user: dict = Depends(get_current_user),
):
    """
    Explain why a student's answer was wrong and provide guidance.

    - Identifies likely misconception
    - Provides step-by-step correct approach
    - Includes memory tip and practice question
    """
    try:
        client, default_model, _ = get_llm_client()

        # Get user's past mistakes count for context (would come from DB in production)
        past_mistakes_count = 0  # TODO: Fetch from user service

        prompt = WRONG_ANSWER_EXPLANATION_PROMPT.format(
            question_text=request.question_text,
            option_a=request.options.get("A", ""),
            option_b=request.options.get("B", ""),
            option_c=request.options.get("C", ""),
            option_d=request.options.get("D", ""),
            student_answer=request.student_answer,
            correct_answer=request.correct_answer,
            topic=request.topic,
            past_mistakes_count=past_mistakes_count,
        )

        messages = [
            {"role": "system", "content": WRONG_ANSWER_EXPLANATION_SYSTEM},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.2,
            json_mode=client.supports_json_mode(),
        )

        data = parse_llm_json(reply)
        explanation = WrongAnswerExplanation(**data)

        return WrongAnswerResponse(
            explanation=explanation,
            provider="default",
            model=default_model,
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/hints", response_model=HintResponse)
async def generate_hints(
    request: HintRequest,
    user: dict = Depends(get_current_user),
):
    """
    Generate progressive hints for a question.

    Returns 3 levels of hints from subtle to more direct.
    """
    try:
        client, default_model, _ = get_llm_client()

        prompt = HINT_GENERATION_PROMPT.format(
            question_text=request.question_text,
            topic=request.topic,
            correct_answer=request.correct_answer,
            solution=request.solution,
        )

        messages = [
            {"role": "system", "content": "You are a helpful tutor providing progressive hints. Respond with JSON only."},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.2,
            json_mode=client.supports_json_mode(),
        )

        data = parse_llm_json(reply)

        return HintResponse(
            hint_level_1=HintLevel(**data.get("hint_level_1", {})),
            hint_level_2=HintLevel(**data.get("hint_level_2", {})) if data.get("hint_level_2") else None,
            hint_level_3=HintLevel(**data.get("hint_level_3", {})) if data.get("hint_level_3") else None,
            provider="default",
            model=default_model,
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/validate")
async def validate_question(
    question: dict,
    user: dict = Depends(get_current_user),
):
    """
    Validate a question for quality, clarity, and accuracy.

    Checks:
    - Mathematical accuracy of solution
    - Clarity of question wording
    - Plausibility of distractors
    - Appropriate difficulty
    """
    try:
        client, default_model, _ = get_llm_client()

        prompt = f"""Validate this exam question for quality:

Question: {question.get('question_text', '')}
Options: {json.dumps(question.get('options', {}))}
Correct Answer: {question.get('correct_answer', '')}
Solution: {question.get('solution', '')}
Stated Difficulty: {question.get('difficulty', '')}

Check and respond with JSON:
{{
  "is_valid": true/false,
  "accuracy_check": {{
    "solution_correct": true/false,
    "answer_matches_solution": true/false,
    "errors_found": ["list of errors if any"]
  }},
  "clarity_check": {{
    "is_clear": true/false,
    "ambiguities": ["list of ambiguous parts"],
    "suggested_rewording": "improved wording if needed"
  }},
  "distractor_check": {{
    "are_plausible": true/false,
    "weak_distractors": ["options that are obviously wrong"],
    "suggestions": ["how to improve distractors"]
  }},
  "difficulty_assessment": {{
    "actual_difficulty": "easy/medium/hard",
    "matches_stated": true/false,
    "reasoning": "why this difficulty"
  }},
  "overall_quality_score": 8.5,
  "recommendations": ["list of improvements"]
}}"""

        messages = [
            {"role": "system", "content": "You are an expert exam question reviewer. Be thorough but fair."},
            {"role": "user", "content": prompt},
        ]

        reply = await client.chat(
            messages=messages,
            model=default_model,
            temperature=0.1,
            json_mode=client.supports_json_mode(),
        )

        return parse_llm_json(reply)

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
