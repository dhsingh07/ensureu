"""
LLM Configuration API Routes - SUPERADMIN only
Allows configuration of LLM provider, model, and settings
"""
from typing import Optional, List
from datetime import datetime
from fastapi import APIRouter, HTTPException, Depends
from pydantic import BaseModel, Field
from motor.motor_asyncio import AsyncIOMotorDatabase

from ..dependencies import get_db, require_role
from ..llm import PROVIDER_INFO
from ..config import settings

router = APIRouter(prefix="/llm-config", tags=["LLM Configuration"])


# =============================================================================
# SCHEMAS
# =============================================================================

class LLMProviderInfo(BaseModel):
    """Information about an LLM provider"""
    id: str
    name: str
    models: List[str]
    supports_embeddings: bool
    supports_json_mode: bool
    configured: bool = False  # Has API key configured


class LLMConfigResponse(BaseModel):
    """Current LLM configuration"""
    provider: str
    model: str
    embed_model: Optional[str] = None
    temperature: float = 0.7
    max_tokens: int = 4096
    updated_at: Optional[datetime] = None
    updated_by: Optional[str] = None


class LLMConfigUpdate(BaseModel):
    """Request to update LLM configuration"""
    provider: str = Field(..., description="LLM provider: claude, openai, ollama")
    model: str = Field(..., description="Model name to use")
    embed_model: Optional[str] = Field(None, description="Model for embeddings")
    temperature: float = Field(0.7, ge=0.0, le=2.0, description="Temperature for generation")
    max_tokens: int = Field(4096, ge=100, le=128000, description="Max tokens for response")


class ProviderTestResult(BaseModel):
    """Result of testing a provider"""
    success: bool
    provider: str
    model: str
    message: str
    response_time_ms: Optional[int] = None


# =============================================================================
# CONFIG COLLECTION NAME
# =============================================================================

LLM_CONFIG_COLLECTION = "llm_config"
LLM_CONFIG_DOC_ID = "active_config"


# =============================================================================
# ROUTES
# =============================================================================

@router.get("/providers", response_model=List[LLMProviderInfo])
async def list_providers():
    """
    List all available LLM providers and their capabilities.
    Public endpoint for displaying options.
    """
    providers = []

    for provider_id, info in PROVIDER_INFO.items():
        # Check if provider has API key configured
        configured = False
        if provider_id == "claude":
            configured = bool(settings.ANTHROPIC_API_KEY)
        elif provider_id == "openai":
            configured = bool(settings.OPENAI_API_KEY)
        elif provider_id == "ollama":
            configured = True  # Ollama doesn't need API key

        providers.append(LLMProviderInfo(
            id=provider_id,
            name=info["name"],
            models=info["models"],
            supports_embeddings=info["supports_embeddings"],
            supports_json_mode=info["supports_json_mode"],
            configured=configured,
        ))

    return providers


@router.get("/current", response_model=LLMConfigResponse)
async def get_current_config(
    db: AsyncIOMotorDatabase = Depends(get_db),
):
    """
    Get the current LLM configuration.
    Falls back to environment defaults if no config stored or DB unavailable.
    """
    try:
        # Try to get stored config
        config = await db[LLM_CONFIG_COLLECTION].find_one({"_id": LLM_CONFIG_DOC_ID})

        if config:
            return LLMConfigResponse(
                provider=config.get("provider", settings.LLM_PROVIDER),
                model=config.get("model", _get_default_model(config.get("provider", settings.LLM_PROVIDER))),
                embed_model=config.get("embed_model"),
                temperature=config.get("temperature", 0.7),
                max_tokens=config.get("max_tokens", 4096),
                updated_at=config.get("updated_at"),
                updated_by=config.get("updated_by"),
            )
    except Exception as e:
        # Log error but continue with defaults
        import logging
        logging.warning(f"Failed to fetch LLM config from DB: {e}")

    # Return defaults from environment
    return LLMConfigResponse(
        provider=settings.LLM_PROVIDER,
        model=_get_default_model(settings.LLM_PROVIDER),
        embed_model=_get_default_embed_model(settings.LLM_PROVIDER),
        temperature=0.7,
        max_tokens=4096,
    )


@router.put(
    "/update",
    response_model=LLMConfigResponse,
    dependencies=[Depends(require_role("SUPERADMIN"))],
)
async def update_config(
    config: LLMConfigUpdate,
    db: AsyncIOMotorDatabase = Depends(get_db),
    user: dict = Depends(require_role("SUPERADMIN")),
):
    """
    Update the LLM configuration.
    SUPERADMIN only.
    """
    # Validate provider
    if config.provider not in PROVIDER_INFO:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid provider: {config.provider}. Valid options: {list(PROVIDER_INFO.keys())}",
        )

    # Validate model
    valid_models = PROVIDER_INFO[config.provider]["models"]
    if config.model not in valid_models:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid model for {config.provider}. Valid options: {valid_models}",
        )

    # Store config
    now = datetime.utcnow()
    update_data = {
        "_id": LLM_CONFIG_DOC_ID,
        "provider": config.provider,
        "model": config.model,
        "embed_model": config.embed_model,
        "temperature": config.temperature,
        "max_tokens": config.max_tokens,
        "updated_at": now,
        "updated_by": user.get("username") or user.get("user_id"),
    }

    try:
        await db[LLM_CONFIG_COLLECTION].replace_one(
            {"_id": LLM_CONFIG_DOC_ID},
            update_data,
            upsert=True,
        )

        # Update cached config
        from ..llm import set_cached_config
        set_cached_config(update_data)

    except Exception as e:
        raise HTTPException(
            status_code=503,
            detail=f"Failed to save configuration to database: {str(e)}. Check MongoDB connection.",
        )

    return LLMConfigResponse(
        provider=config.provider,
        model=config.model,
        embed_model=config.embed_model,
        temperature=config.temperature,
        max_tokens=config.max_tokens,
        updated_at=now,
        updated_by=update_data["updated_by"],
    )


@router.post(
    "/test",
    response_model=ProviderTestResult,
    dependencies=[Depends(require_role("SUPERADMIN"))],
)
async def test_provider(
    provider: str,
    model: Optional[str] = None,
):
    """
    Test a provider by sending a simple request.
    SUPERADMIN only.
    """
    import time
    from ..llm import get_llm_client

    if provider not in PROVIDER_INFO:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid provider: {provider}",
        )

    try:
        client, default_model, _ = get_llm_client(provider)
        test_model = model or default_model

        start_time = time.time()

        # Send a simple test message
        response = await client.chat(
            messages=[{"role": "user", "content": "Say 'Hello' in one word."}],
            model=test_model,
            temperature=0.1,
            max_tokens=10,
        )

        elapsed_ms = int((time.time() - start_time) * 1000)

        return ProviderTestResult(
            success=True,
            provider=provider,
            model=test_model,
            message=f"Provider responded: {response[:50]}..." if len(response) > 50 else f"Provider responded: {response}",
            response_time_ms=elapsed_ms,
        )

    except Exception as e:
        return ProviderTestResult(
            success=False,
            provider=provider,
            model=model or _get_default_model(provider),
            message=f"Error: {str(e)}",
        )


@router.get("/history", dependencies=[Depends(require_role("SUPERADMIN"))])
async def get_config_history(
    limit: int = 10,
    db: AsyncIOMotorDatabase = Depends(get_db),
):
    """
    Get history of LLM configuration changes.
    SUPERADMIN only.
    """
    try:
        cursor = db[f"{LLM_CONFIG_COLLECTION}_history"].find().sort("changed_at", -1).limit(limit)
        history = await cursor.to_list(length=limit)

        return [
            {
                "provider": h.get("provider"),
                "model": h.get("model"),
                "changed_at": h.get("changed_at"),
                "changed_by": h.get("changed_by"),
            }
            for h in history
        ]
    except Exception as e:
        # Return empty history if DB is unavailable
        import logging
        logging.warning(f"Failed to fetch LLM config history: {e}")
        return []


# =============================================================================
# HELPER FUNCTIONS
# =============================================================================

def _get_default_model(provider: str) -> str:
    """Get default model for a provider"""
    if provider == "claude":
        return settings.CLAUDE_MODEL
    elif provider == "openai":
        return settings.OPENAI_MODEL
    elif provider == "ollama":
        return settings.OLLAMA_MODEL
    return ""


def _get_default_embed_model(provider: str) -> Optional[str]:
    """Get default embedding model for a provider"""
    if provider == "claude":
        return settings.CLAUDE_EMBED_MODEL
    elif provider == "openai":
        return settings.OPENAI_EMBED_MODEL
    elif provider == "ollama":
        return settings.OLLAMA_EMBED_MODEL
    return None
