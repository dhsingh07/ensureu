"""
LLM Client Factory - Instantiates the appropriate LLM client based on provider
"""
from typing import Tuple, Optional, Dict, Any

from .base import BaseLLMClient
from .claude_client import ClaudeClient
from .openai_client import OpenAIClient
from .ollama_client import OllamaClient
from ..config import settings


# Cached config from database (refreshed on demand)
_cached_config: Optional[Dict[str, Any]] = None


def set_cached_config(config: Optional[Dict[str, Any]]):
    """Set cached config from database lookup"""
    global _cached_config
    _cached_config = config


def get_cached_config() -> Optional[Dict[str, Any]]:
    """Get cached config"""
    return _cached_config


def get_llm_client(
    provider: Optional[str] = None,
    model: Optional[str] = None,
) -> Tuple[BaseLLMClient, str, Optional[str]]:
    """
    Get LLM client for the specified provider.

    Args:
        provider: Provider name (claude, openai, ollama). Uses cached config or env default if None.
        model: Model name. Uses cached config or provider default if None.

    Returns:
        Tuple of (client instance, default model, default embed model)
    """
    # Use cached config if available
    cached = get_cached_config()

    if cached and not provider:
        chosen = cached.get("provider", settings.LLM_PROVIDER)
    else:
        chosen = provider or settings.LLM_PROVIDER

    if chosen == "openai":
        model_name = model
        if not model_name and cached and cached.get("provider") == "openai":
            model_name = cached.get("model")
        return (
            OpenAIClient(),
            model_name or settings.OPENAI_MODEL,
            settings.OPENAI_EMBED_MODEL,
        )

    if chosen == "claude":
        model_name = model
        if not model_name and cached and cached.get("provider") == "claude":
            model_name = cached.get("model")
        return (
            ClaudeClient(),
            model_name or settings.CLAUDE_MODEL,
            settings.CLAUDE_EMBED_MODEL,
        )

    if chosen == "ollama":
        model_name = model
        if not model_name and cached and cached.get("provider") == "ollama":
            model_name = cached.get("model")
        return (
            OllamaClient(),
            model_name or settings.OLLAMA_MODEL,
            settings.OLLAMA_EMBED_MODEL,
        )

    raise ValueError(f"Unknown LLM provider: {chosen}")


def get_embedding_client() -> Tuple[BaseLLMClient, str]:
    """
    Get a client that supports embeddings.
    Falls back to OpenAI if primary provider doesn't support embeddings.

    Returns:
        Tuple of (client instance, embed model)
    """
    client, _, embed_model = get_llm_client()

    if client.supports_embeddings() and embed_model:
        return client, embed_model

    # Fallback to OpenAI for embeddings
    if settings.OPENAI_API_KEY:
        return OpenAIClient(), settings.OPENAI_EMBED_MODEL

    # Fallback to Ollama for embeddings
    return OllamaClient(), settings.OLLAMA_EMBED_MODEL


# Provider info for API responses
PROVIDER_INFO = {
    "openai": {
        "name": "OpenAI",
        "models": ["gpt-4o", "gpt-4o-mini", "gpt-4-turbo"],
        "supports_embeddings": True,
        "supports_json_mode": True,
    },
    "claude": {
        "name": "Anthropic Claude",
        "models": ["claude-sonnet-4-5", "claude-3-5-haiku-20241022", "claude-3-opus-20240229"],
        "supports_embeddings": False,
        "supports_json_mode": False,
    },
    "ollama": {
        "name": "Ollama (Local)",
        "models": ["llama3.1", "llama3.2", "mistral", "codellama"],
        "supports_embeddings": True,
        "supports_json_mode": True,
    },
}
