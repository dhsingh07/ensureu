"""
OpenAI LLM Client Implementation
"""
import httpx
from typing import List, Dict, AsyncIterator
from openai import AsyncOpenAI

from .base import BaseLLMClient
from ..config import settings


class OpenAIClient(BaseLLMClient):
    """OpenAI API client"""

    def __init__(self):
        # Configure timeout from settings
        timeout = httpx.Timeout(
            float(settings.LLM_TIMEOUT),
            connect=float(settings.LLM_CONNECT_TIMEOUT)
        )
        self.client = AsyncOpenAI(
            api_key=settings.OPENAI_API_KEY,
            timeout=timeout,
        )

    async def chat(
        self,
        messages: List[Dict[str, str]],
        model: str,
        temperature: float = 0.2,
        max_tokens: int = 4096,
        json_mode: bool = False,
    ) -> str:
        kwargs = {
            "model": model,
            "messages": messages,
            "temperature": temperature,
            "max_tokens": max_tokens,
        }

        # Enable JSON mode if requested
        if json_mode:
            kwargs["response_format"] = {"type": "json_object"}

        response = await self.client.chat.completions.create(**kwargs)
        return response.choices[0].message.content or ""

    async def chat_stream(
        self,
        messages: List[Dict[str, str]],
        model: str,
        temperature: float = 0.2,
        max_tokens: int = 4096,
    ) -> AsyncIterator[str]:
        stream = await self.client.chat.completions.create(
            model=model,
            messages=messages,
            temperature=temperature,
            max_tokens=max_tokens,
            stream=True,
        )

        async for chunk in stream:
            if chunk.choices and chunk.choices[0].delta.content:
                yield chunk.choices[0].delta.content

    async def embed(
        self,
        texts: List[str],
        model: str,
    ) -> List[List[float]]:
        response = await self.client.embeddings.create(
            model=model,
            input=texts,
        )
        return [item.embedding for item in response.data]

    async def list_models(self) -> List[str]:
        """Return commonly used OpenAI models"""
        return [
            "gpt-4o",
            "gpt-4o-mini",
            "gpt-4-turbo",
            "gpt-4",
            "gpt-3.5-turbo",
        ]

    def supports_embeddings(self) -> bool:
        return True

    def supports_json_mode(self) -> bool:
        return True
