"""
EnsureU AI Service - Main Application

FastAPI application for AI-powered exam preparation features:
- Question generation
- Wrong answer explanation
- Performance analysis
- Study plan generation
- Adaptive learning
"""
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import logging

from .config import settings
from .dependencies import close_db, get_db
from .llm import set_cached_config

# Import routers
from .routes import llm, questions, analysis, llm_config

# Configure logging
logging.basicConfig(
    level=logging.DEBUG if settings.DEBUG else logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan handler for startup/shutdown"""
    # Startup
    logger.info(f"Starting {settings.SERVICE_NAME} v{settings.VERSION}")
    logger.info(f"Default LLM Provider: {settings.LLM_PROVIDER}")

    # Load LLM config from database
    try:
        db = await get_db()
        config = await db["llm_config"].find_one({"_id": "active_config"})
        if config:
            set_cached_config(config)
            logger.info(f"Loaded LLM config from DB: provider={config.get('provider')}, model={config.get('model')}")
        else:
            logger.info("No stored LLM config, using environment defaults")
    except Exception as e:
        logger.warning(f"Failed to load LLM config from DB: {e}")

    yield

    # Shutdown
    logger.info("Shutting down...")
    await close_db()


# Create FastAPI application
app = FastAPI(
    title="EnsureU AI Service",
    description="""
AI-powered exam preparation service providing:

- **Question Generation**: Generate exam-quality MCQs with AI
- **Wrong Answer Explanation**: Understand mistakes and learn from them
- **Performance Analysis**: Deep insights into exam performance
- **Study Plans**: Personalized AI-generated study schedules
- **Adaptive Learning**: Smart question selection based on your level

Built for competitive exam preparation (SSC, Banking, Railways, etc.)
    """,
    version=settings.VERSION,
    lifespan=lifespan,
    docs_url="/docs" if settings.DEBUG else None,
    redoc_url="/redoc" if settings.DEBUG else None,
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:3000",  # Next.js dev
        "http://localhost:4200",  # Angular dev
        "http://localhost:8282",  # Java backend
        "*",  # Allow all in development (restrict in production)
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Global exception handler
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    logger.error(f"Unhandled exception: {exc}", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={
            "detail": "An internal error occurred",
            "error": str(exc) if settings.DEBUG else "Internal Server Error",
        },
    )


# Health check endpoint
@app.get("/health")
async def health_check():
    """Health check endpoint for load balancers"""
    return {
        "status": "healthy",
        "service": settings.SERVICE_NAME,
        "version": settings.VERSION,
        "llm_provider": settings.LLM_PROVIDER,
    }


# Root endpoint
@app.get("/")
async def root():
    """Service information"""
    return {
        "service": settings.SERVICE_NAME,
        "version": settings.VERSION,
        "description": "AI-powered exam preparation service",
        "docs": "/docs" if settings.DEBUG else "Disabled in production",
        "endpoints": {
            "llm": "/llm - Core LLM chat and embeddings",
            "questions": "/questions - Question generation and explanation",
            "analysis": "/analysis - Performance analysis and study plans",
            "llm-config": "/llm-config - LLM configuration (SUPERADMIN)",
        },
    }


# Include routers
app.include_router(llm.router)
app.include_router(questions.router)
app.include_router(analysis.router)
app.include_router(llm_config.router)


# Development server
if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8000,
        reload=settings.DEBUG,
        log_level="debug" if settings.DEBUG else "info",
    )
