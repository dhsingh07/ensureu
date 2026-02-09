"""
Core LLM API Routes
"""
import json
from typing import Optional
from fastapi import APIRouter, HTTPException, Depends
from fastapi.responses import StreamingResponse

from ..schemas import ChatRequest, ChatResponse, EmbedRequest, EmbedResponse
from ..llm import get_llm_client, get_embedding_client, PROVIDER_INFO
from ..prompts import EXAM_GUARDRAILS_SYSTEM
from ..dependencies import get_current_user

router = APIRouter(prefix="/llm", tags=["LLM"])


@router.get("/providers")
async def list_providers():
    """List available LLM providers and their capabilities"""
    return {
        "providers": PROVIDER_INFO,
        "default": "claude",
    }


@router.get("/models")
async def list_models(provider: Optional[str] = None):
    """List available models for a provider"""
    try:
        client, default_model, default_embed_model = get_llm_client(provider)
        models = await client.list_models()

        return {
            "provider": provider or "default",
            "default_model": default_model,
            "default_embed_model": default_embed_model,
            "available_models": models,
            "supports_embeddings": client.supports_embeddings(),
            "supports_json_mode": client.supports_json_mode(),
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/chat", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    user: dict = Depends(get_current_user),
):
    """
    Send messages to LLM and get response.
    Optionally applies guardrails for safe educational content.
    """
    try:
        client, default_model, _ = get_llm_client(request.provider)
        model = request.model or default_model

        messages = [{"role": m.role, "content": m.content} for m in request.messages]

        # Apply guardrails if enabled
        if request.guardrails:
            messages.insert(0, {"role": "system", "content": EXAM_GUARDRAILS_SYSTEM})

        reply = await client.chat(
            messages=messages,
            model=model,
            temperature=request.temperature,
            max_tokens=request.max_tokens,
        )

        return ChatResponse(
            provider=request.provider or "default",
            model=model,
            reply=reply,
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/chat/stream")
async def chat_stream(
    request: ChatRequest,
    user: dict = Depends(get_current_user),
):
    """
    Stream chat response token by token (Server-Sent Events).
    """
    try:
        client, default_model, _ = get_llm_client(request.provider)
        model = request.model or default_model

        messages = [{"role": m.role, "content": m.content} for m in request.messages]

        if request.guardrails:
            messages.insert(0, {"role": "system", "content": EXAM_GUARDRAILS_SYSTEM})

        async def generate():
            # Send start event
            yield f"event: start\ndata: {json.dumps({'provider': request.provider or 'default', 'model': model})}\n\n"

            # Stream tokens
            async for token in client.chat_stream(
                messages=messages,
                model=model,
                temperature=request.temperature,
                max_tokens=request.max_tokens,
            ):
                yield f"event: token\ndata: {json.dumps({'token': token})}\n\n"

            # Send end event
            yield f"event: end\ndata: {{}}\n\n"

        return StreamingResponse(
            generate(),
            media_type="text/event-stream",
            headers={
                "Cache-Control": "no-cache",
                "Connection": "keep-alive",
            },
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/embeddings", response_model=EmbedResponse)
async def embeddings(
    request: EmbedRequest,
    user: dict = Depends(get_current_user),
):
    """
    Generate embeddings for texts.
    Automatically falls back to a provider that supports embeddings.
    """
    try:
        client, embed_model = get_embedding_client()

        if not client.supports_embeddings():
            raise HTTPException(
                status_code=400,
                detail="No embedding-capable provider available. Configure OpenAI or Ollama.",
            )

        model = request.model or embed_model
        embeddings = await client.embed(texts=request.texts, model=model)

        return EmbedResponse(
            provider=request.provider or "default",
            model=model,
            embeddings=embeddings,
        )
    except NotImplementedError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
