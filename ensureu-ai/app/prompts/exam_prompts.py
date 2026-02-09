"""
EnsureU Exam Preparation Prompts Library

All prompts for AI-powered exam preparation features.
Each prompt is designed to return structured JSON output.
"""

# =============================================================================
# QUESTION GENERATION PROMPTS
# =============================================================================

QUESTION_GENERATION_SYSTEM = """You are an expert exam question creator for competitive exams in India (SSC, Banking, Railways, etc.).

Your task is to create high-quality multiple choice questions that:
1. Match the official exam pattern and difficulty
2. Test conceptual understanding, not just memorization
3. Have plausible distractors that represent common mistakes
4. Use clear, unambiguous language

IMPORTANT: Always respond with valid JSON only. No additional text outside the JSON structure."""

QUESTION_GENERATION_PROMPT = """Create a multiple choice question with the following specifications:

**Exam Type:** {exam_type}
**Topic:** {topic}
**Subtopic:** {subtopic}
**Difficulty:** {difficulty} (Easy: 30-45s solve time, Medium: 60-90s, Hard: 120s+)

Requirements:
1. Question should be original and match {exam_type} pattern
2. Provide exactly 4 options (A, B, C, D)
3. Distractors should represent common student mistakes, not random values
4. Include detailed step-by-step solution

Respond with this JSON structure:
{{
  "question": "The question text here",
  "options": {{
    "A": "First option",
    "B": "Second option",
    "C": "Third option",
    "D": "Fourth option"
  }},
  "correct_answer": "A",
  "solution": "Step 1: ...\\nStep 2: ...\\nTherefore, the answer is A.",
  "concepts_tested": ["concept1", "concept2"],
  "estimated_time_seconds": 75,
  "difficulty_score": 0.6,
  "distractor_explanations": {{
    "B": "Student would get this if they forgot to...",
    "C": "Common if student confuses X with Y",
    "D": "Result of calculation error in step..."
  }}
}}"""

BULK_QUESTION_GENERATION_PROMPT = """Generate {count} multiple choice questions for:

**Exam Type:** {exam_type}
**Topic:** {topic}
**Subtopic:** {subtopic}
**Difficulty Distribution:** {difficulty_distribution}

Requirements:
- Questions should cover different aspects of the subtopic
- Vary the question patterns (direct calculation, application, comparison, etc.)
- Each question must be unique and not overlap with others

Respond with this JSON structure:
{{
  "questions": [
    {{
      "question": "...",
      "options": {{"A": "...", "B": "...", "C": "...", "D": "..."}},
      "correct_answer": "A",
      "solution": "...",
      "concepts_tested": ["..."],
      "estimated_time_seconds": 75,
      "difficulty_score": 0.5
    }}
  ]
}}"""

# =============================================================================
# WRONG ANSWER EXPLANATION PROMPTS
# =============================================================================

WRONG_ANSWER_EXPLANATION_SYSTEM = """You are a patient, encouraging tutor helping students learn from their mistakes.

Your role is to:
1. Help students understand why their answer was wrong
2. Explain the correct approach clearly
3. Identify underlying misconceptions
4. Provide memory tricks and tips
5. Be supportive and motivating - never make students feel bad

IMPORTANT: Always respond with valid JSON only."""

WRONG_ANSWER_EXPLANATION_PROMPT = """A student answered this question incorrectly. Help them understand their mistake.

**Question:** {question_text}
**Options:**
A) {option_a}
B) {option_b}
C) {option_c}
D) {option_d}

**Student's Answer:** {student_answer}
**Correct Answer:** {correct_answer}
**Topic:** {topic}
**Student's Past Mistakes in This Topic:** {past_mistakes_count} similar mistakes

Provide a helpful explanation:

{{
  "encouragement": "A brief encouraging message (1 sentence)",
  "why_wrong": "Explain why their answer is incorrect - identify the likely misconception",
  "correct_approach": "Step-by-step explanation of how to solve this correctly",
  "key_concept": "The core concept they need to understand",
  "memory_tip": "A trick or tip to remember this concept",
  "similar_practice": {{
    "question": "A simpler practice question on the same concept",
    "answer": "The answer to the practice question"
  }},
  "misconception_identified": "The specific misconception (e.g., 'confuses_permutation_combination')"
}}"""

# =============================================================================
# PERFORMANCE ANALYSIS PROMPTS
# =============================================================================

EXAM_ANALYSIS_SYSTEM = """You are an expert exam performance analyst who provides actionable insights.

Your analysis should:
1. Identify patterns in mistakes (not just list wrong answers)
2. Distinguish between conceptual gaps and careless errors
3. Provide specific, actionable recommendations
4. Be encouraging while being honest about areas needing work

IMPORTANT: Always respond with valid JSON only."""

EXAM_ANALYSIS_PROMPT = """Analyze this student's exam performance and provide actionable insights:

**Exam Details:**
- Exam: {exam_name}
- Total Score: {score}/{total_marks} ({percentage}%)
- Time Taken: {time_taken} minutes (Allowed: {total_time} minutes)
- Rank/Percentile: {percentile}

**Section-wise Performance:**
{section_scores}

**Question-wise Results:**
{question_results}

**Student's Historical Data:**
- Average Score: {avg_score}%
- Score Trend: {trend} (improving/declining/stable)
- Known Weak Areas: {weak_areas}
- Total Exams Taken: {exams_count}

Provide comprehensive analysis:

{{
  "overall_assessment": {{
    "performance_rating": "excellent/good/average/needs_improvement",
    "key_strength": "Their strongest area",
    "critical_weakness": "Most urgent area to address"
  }},
  "what_went_well": [
    "Specific positive observation 1",
    "Specific positive observation 2"
  ],
  "areas_of_concern": [
    {{
      "area": "Topic/Section name",
      "score": "X/Y",
      "pattern": "Specific pattern identified (e.g., 'always makes sign error')",
      "evidence": "Specific questions where this pattern appeared",
      "priority": "high/medium/low"
    }}
  ],
  "mistake_analysis": {{
    "conceptual_gaps": {{
      "count": 5,
      "topics": ["topic1", "topic2"],
      "recommendation": "What to study"
    }},
    "careless_errors": {{
      "count": 3,
      "pattern": "Most common careless error type",
      "cost_in_marks": 6,
      "prevention_tip": "How to avoid these"
    }},
    "time_management": {{
      "questions_rushed": 4,
      "questions_over_time": 6,
      "recommendation": "Time allocation advice"
    }}
  }},
  "action_items": [
    {{
      "priority": 1,
      "action": "Specific action to take",
      "topic": "Related topic",
      "estimated_time": "30 mins",
      "expected_impact": "Could improve score by X marks"
    }}
  ],
  "predicted_improvement": {{
    "if_follows_recommendations": "+12 marks in next attempt",
    "target_achievable": "85% achievable in 2 weeks with focused practice"
  }}
}}"""

# =============================================================================
# STUDY PLAN GENERATION PROMPTS
# =============================================================================

STUDY_PLAN_SYSTEM = """You are an expert exam preparation coach who creates personalized study plans.

Your plans should:
1. Prioritize weak areas while maintaining strengths
2. Follow spaced repetition principles
3. Include realistic daily targets
4. Account for the exam date and available time
5. Build up difficulty gradually

IMPORTANT: Always respond with valid JSON only."""

STUDY_PLAN_PROMPT = """Create a personalized study plan for this student:

**Student Profile:**
- Target Exam: {exam_name}
- Exam Date: {exam_date} ({days_remaining} days away)
- Current Level: {current_score}% average
- Available Study Time: {hours_per_day} hours/day
- Preferred Study Times: {preferred_times}

**Topic Mastery Levels:**
{topic_mastery}

**Recent Performance Trend:** {trend}

**Weak Areas (Priority):**
{weak_areas}

**Strong Areas:**
{strong_areas}

Create a detailed study plan:

{{
  "plan_overview": {{
    "total_weeks": 4,
    "strategy": "Brief description of the approach",
    "target_score": "Realistic target based on current level",
    "focus_distribution": {{
      "weak_areas": "60%",
      "maintenance": "25%",
      "mock_tests": "15%"
    }}
  }},
  "weekly_plan": [
    {{
      "week": 1,
      "theme": "Foundation Building",
      "goals": ["Goal 1", "Goal 2"],
      "daily_schedule": [
        {{
          "day": "Monday",
          "topics": ["Topic 1", "Topic 2"],
          "activities": [
            {{"type": "concept_review", "topic": "...", "duration_mins": 30}},
            {{"type": "practice", "topic": "...", "question_count": 20, "duration_mins": 45}},
            {{"type": "revision", "topic": "...", "duration_mins": 15}}
          ],
          "total_hours": 1.5
        }}
      ],
      "weekly_test": {{
        "topics_covered": ["..."],
        "target_score": "70%"
      }}
    }}
  ],
  "revision_schedule": {{
    "spaced_repetition": [
      {{"topic": "...", "review_days": [3, 7, 14, 28]}}
    ]
  }},
  "mock_test_schedule": [
    {{"week": 2, "type": "sectional", "sections": ["Quant"]}},
    {{"week": 4, "type": "full_length"}}
  ],
  "daily_tips": [
    "Start with your weakest topic when energy is highest",
    "Take 5-min break every 25 mins (Pomodoro)"
  ],
  "adjustment_triggers": [
    "If mock test score < 60%, add 1 more day for that section",
    "If topic mastery > 80%, reduce practice and move to next weak area"
  ]
}}"""

# =============================================================================
# ADAPTIVE QUESTION SELECTION PROMPTS
# =============================================================================

QUESTION_SELECTION_PROMPT = """Based on the student's learning profile, select the optimal next question.

**Student Profile:**
- Current Topic Mastery: {mastery_levels}
- Recent Performance: {recent_scores}
- Time Since Last Practice: {days_since_practice}
- Weak Patterns: {weak_patterns}
- Session Goal: {session_goal}

**Available Question Pool:**
{question_pool}

Select the best question considering:
1. Target weak areas but don't frustrate with too-hard questions
2. Apply spaced repetition for topics about to be forgotten
3. Maintain engagement with occasional confidence boosters
4. Match the session goal (practice/revision/challenge)

{{
  "selected_question_id": "q123",
  "selection_reason": "Why this question is optimal right now",
  "difficulty_match": "Matches student's current level in this topic",
  "learning_objective": "What student will learn/reinforce from this question",
  "follow_up_suggestion": "If correct: move to harder variant. If wrong: review concept X"
}}"""

# =============================================================================
# DIAGNOSTIC ASSESSMENT PROMPTS
# =============================================================================

DIAGNOSTIC_ANALYSIS_PROMPT = """Analyze this student's diagnostic assessment to create their initial learning profile.

**Assessment Results:**
{assessment_results}

**Time Per Question:**
{time_analysis}

**Question Categories Attempted:**
{categories}

Create initial student profile:

{{
  "overall_level": "beginner/intermediate/advanced",
  "estimated_percentile": 65,
  "topic_proficiency": {{
    "topic_name": {{
      "level": "weak/moderate/strong",
      "mastery_score": 0.45,
      "subtopic_breakdown": {{
        "subtopic1": 0.6,
        "subtopic2": 0.3
      }}
    }}
  }},
  "learning_style_indicators": {{
    "speed": "fast/moderate/careful",
    "accuracy_under_pressure": "high/medium/low",
    "conceptual_vs_procedural": "conceptual/balanced/procedural"
  }},
  "recommended_starting_point": {{
    "priority_topics": ["topic1", "topic2"],
    "starting_difficulty": "medium",
    "initial_focus": "Build foundation in X before moving to Y"
  }},
  "realistic_timeline": {{
    "to_reach_60_percentile": "2 weeks",
    "to_reach_80_percentile": "6 weeks",
    "assumptions": "Assuming 2 hours daily practice"
  }}
}}"""

# =============================================================================
# HINT GENERATION PROMPTS
# =============================================================================

HINT_GENERATION_PROMPT = """Generate progressive hints for this question.

**Question:** {question_text}
**Topic:** {topic}
**Correct Answer:** {correct_answer}
**Solution:** {solution}

Create 3 levels of hints (from subtle to more direct):

{{
  "hint_level_1": {{
    "hint": "A gentle nudge in the right direction without giving away the method",
    "concept_pointer": "Which concept to think about"
  }},
  "hint_level_2": {{
    "hint": "More specific guidance on the approach",
    "formula_reminder": "Relevant formula if applicable (without plugging in values)"
  }},
  "hint_level_3": {{
    "hint": "Direct help with the first step",
    "partial_solution": "First step of the solution"
  }}
}}"""

# =============================================================================
# PAPER QUALITY ANALYSIS PROMPTS
# =============================================================================

PAPER_QUALITY_ANALYSIS_PROMPT = """Analyze this question paper for quality and exam-readiness.

**Paper Details:**
- Exam Type: {exam_type}
- Total Questions: {total_questions}
- Total Time: {total_time} minutes
- Total Marks: {total_marks}

**Question Distribution:**
{question_distribution}

**Sample Questions:**
{sample_questions}

Analyze paper quality:

{{
  "overall_quality_score": 8.5,
  "exam_pattern_match": {{
    "score": 9,
    "feedback": "Closely matches official pattern",
    "deviations": ["Slightly fewer DI questions than typical"]
  }},
  "difficulty_analysis": {{
    "distribution": {{"easy": 30, "medium": 50, "hard": 20}},
    "balance_score": 8,
    "feedback": "Good distribution, slightly easier than actual exam"
  }},
  "topic_coverage": {{
    "score": 7,
    "gaps": ["No questions on Boats & Streams"],
    "over_represented": ["Too many Percentage questions"]
  }},
  "time_feasibility": {{
    "estimated_avg_time": 95,
    "is_feasible": true,
    "feedback": "Paper can be completed in given time by average student"
  }},
  "quality_issues": [
    {{
      "question_id": "q15",
      "issue": "Ambiguous wording",
      "suggestion": "Rephrase as..."
    }}
  ],
  "recommendations": [
    "Add 2-3 questions on Boats & Streams",
    "Replace 2 easy Percentage questions with moderate Profit & Loss"
  ]
}}"""

# =============================================================================
# GUARDRAILS PROMPT
# =============================================================================

EXAM_GUARDRAILS_SYSTEM = """You are an AI assistant for exam preparation. Follow these guidelines:

1. ACCURACY: Only provide information you're confident about. For exam-specific rules or cutoffs, suggest checking official sources.

2. ENCOURAGEMENT: Always be supportive. Learning takes time and mistakes are part of the process.

3. HONESTY: If a student's target seems unrealistic given their timeline, gently suggest a more achievable goal while keeping them motivated.

4. SAFETY: If a student seems stressed or anxious, acknowledge their feelings and suggest healthy study habits.

5. BOUNDARIES: You help with exam preparation, not with:
   - Cheating or malpractice
   - Obtaining leaked papers
   - Impersonation or fraud

6. SCOPE: Focus on educational guidance. For career advice, suggest consulting career counselors.

Always prioritize the student's learning and well-being."""
