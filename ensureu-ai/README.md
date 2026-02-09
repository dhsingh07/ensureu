# EnsureU AI Service

AI-powered exam preparation service built with FastAPI, providing intelligent features for competitive exam preparation.

## Features

### 1. Question Generation (`/questions/generate`)
- Generate exam-quality MCQs matching official patterns
- Support for SSC, Banking, Railway exams
- Includes solution and distractor explanations

### 2. Wrong Answer Explanation (`/questions/explain-wrong`)
- Understand why an answer was wrong
- Identify underlying misconceptions
- Get memory tips and practice questions

### 3. Performance Analysis (`/analysis/exam`)
- Deep analysis of exam performance
- Pattern recognition in mistakes
- Careless vs conceptual error distinction
- Prioritized action items

### 4. Study Plan Generation (`/analysis/study-plan`)
- Personalized week-by-week schedules
- Spaced repetition integration
- Mock test scheduling
- Adaptive adjustments

### 5. Diagnostic Assessment (`/analysis/diagnostic`)
- Initial level assessment
- Topic-wise proficiency mapping
- Recommended starting points

### 6. Paper Quality Analysis (`/analysis/paper-quality`)
- Exam pattern matching validation
- Difficulty distribution check
- Topic coverage analysis

## Architecture

```
ensureu-ai/
├── app/
│   ├── llm/                 # LLM client abstraction
│   │   ├── base.py          # Abstract base class
│   │   ├── claude_client.py # Anthropic implementation
│   │   ├── openai_client.py # OpenAI implementation
│   │   ├── ollama_client.py # Local LLM implementation
│   │   └── factory.py       # Provider factory
│   ├── prompts/             # Prompt library
│   │   └── exam_prompts.py  # All exam-specific prompts
│   ├── routes/              # API endpoints
│   │   ├── llm.py           # Core LLM endpoints
│   │   ├── questions.py     # Question generation
│   │   └── analysis.py      # Performance analysis
│   ├── services/            # Business logic
│   │   └── json_parser.py   # LLM response parsing
│   ├── config.py            # Settings
│   ├── schemas.py           # Pydantic models
│   ├── dependencies.py      # Auth & feature flags
│   └── main.py              # FastAPI app
├── requirements.txt
├── .env.example
└── README.md
```

## Quick Start

### 1. Setup Environment

```bash
cd ensureu-ai

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Configure environment
cp .env.example .env
# Edit .env with your API keys
```

### 2. Configure LLM Provider

Set `LLM_PROVIDER` in `.env`:
- `claude` - Use Anthropic Claude (recommended)
- `openai` - Use OpenAI GPT
- `ollama` - Use local Ollama

Add the corresponding API key.

### 3. Run the Service

```bash
# Development
uvicorn app.main:app --reload --port 8000

# Production
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 4
```

### 4. Access API

- API Docs: http://localhost:8000/docs (debug mode only)
- Health Check: http://localhost:8000/health

## API Examples

### Generate Question

```bash
curl -X POST http://localhost:8000/questions/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "exam_type": "SSC_CGL",
    "topic": "Quantitative Aptitude",
    "subtopic": "Profit and Loss",
    "difficulty": "medium",
    "count": 1
  }'
```

### Explain Wrong Answer

```bash
curl -X POST http://localhost:8000/questions/explain-wrong \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "question_id": "q123",
    "question_text": "A shopkeeper sells an item at 20% profit...",
    "options": {"A": "₹120", "B": "₹100", "C": "₹80", "D": "₹60"},
    "student_answer": "B",
    "correct_answer": "A",
    "topic": "Profit and Loss"
  }'
```

### Analyze Exam Performance

```bash
curl -X POST http://localhost:8000/analysis/exam \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "exam_id": "exam123",
    "exam_name": "SSC CGL Mock 5",
    "user_id": "user123",
    "score": 142,
    "total_marks": 200,
    "time_taken_minutes": 55,
    "total_time_minutes": 60,
    "section_scores": [...],
    "question_results": [...]
  }'
```

## Integration with EnsureU Backend

The AI service integrates with the main Java backend:

1. **Shared JWT**: Uses same JWT secret for authentication
2. **Shared MongoDB**: Accesses same database for user data
3. **API Gateway**: Can be proxied through the main backend

### Backend Integration Example (Java)

```java
@Service
public class AIService {
    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public QuestionExplanation explainWrongAnswer(WrongAnswerRequest req) {
        return webClient.post()
            .uri(aiServiceUrl + "/questions/explain-wrong")
            .bodyValue(req)
            .retrieve()
            .bodyToMono(QuestionExplanation.class)
            .block();
    }
}
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `LLM_PROVIDER` | LLM provider (claude/openai/ollama) | claude |
| `ANTHROPIC_API_KEY` | Anthropic API key | - |
| `OPENAI_API_KEY` | OpenAI API key | - |
| `MONGODB_URI` | MongoDB connection string | mongodb://localhost:27017 |
| `MONGODB_DB` | Database name | ensureu |
| `JWT_SECRET` | JWT secret (match backend) | mySecret |
| `DEBUG` | Enable debug mode | false |

## Adding New Features

1. Add prompts to `app/prompts/exam_prompts.py`
2. Add schemas to `app/schemas.py`
3. Create route in `app/routes/`
4. Register router in `app/main.py`

## License

Proprietary - EnsureU
