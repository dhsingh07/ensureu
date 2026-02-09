"""
EnsureU AI Service Configuration
"""
from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    """Application settings loaded from environment variables"""

    # Service Info
    SERVICE_NAME: str = "ensureu-ai"
    VERSION: str = "1.0.0"
    DEBUG: bool = False

    # LLM Provider Configuration
    LLM_PROVIDER: str = "claude"  # claude | openai | ollama

    # OpenAI
    OPENAI_API_KEY: Optional[str] = None
    OPENAI_MODEL: str = "gpt-4o-mini"
    OPENAI_EMBED_MODEL: str = "text-embedding-3-small"

    # Claude/Anthropic
    ANTHROPIC_API_KEY: Optional[str] = None
    CLAUDE_MODEL: str = "claude-sonnet-4-5"
    CLAUDE_EMBED_MODEL: Optional[str] = None  # Claude doesn't support embeddings

    # Ollama (Local)
    OLLAMA_BASE_URL: str = "http://localhost:11434"
    OLLAMA_MODEL: str = "llama3.1"
    OLLAMA_EMBED_MODEL: str = "nomic-embed-text"

    # LLM Timeout (seconds) - AI operations can take a while
    LLM_TIMEOUT: int = 120
    LLM_CONNECT_TIMEOUT: int = 30

    # MongoDB
    MONGODB_URI: str = "mongodb://localhost:27017"
    MONGODB_DB: str = "ensureu"

    # EnsureU Backend Integration
    ENSUREU_BACKEND_URL: str = "http://localhost:8282/api"
    ENSUREU_API_KEY: Optional[str] = None

    # JWT Configuration (same as main backend)
    JWT_SECRET: str = "mySecret"
    JWT_ALGORITHM: str = "HS512"  # Must match Java backend (HS512)

    # RAG Configuration
    RAG_DB_PATH: str = "data/rag_embeddings.sqlite"
    RAG_CHUNK_SIZE: int = 500
    RAG_CHUNK_OVERLAP: int = 50

    # Rate Limiting
    RATE_LIMIT_ENABLED: bool = True
    DEFAULT_RATE_LIMIT: str = "30/minute"

    # Caching
    REDIS_URL: Optional[str] = None
    CACHE_TTL_SECONDS: int = 3600

    # Background Workers
    WORKER_POLL_INTERVAL: int = 10  # seconds

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


settings = Settings()
