"""
Claude/Anthropic LLM Client Implementation
"""
import json
import httpx
from typing import List, Dict, AsyncIterator, Tuple, Optional
from anthropic import AsyncAnthropic

from .base import BaseLLMClient
from ..config import settings


class ClaudeClient(BaseLLMClient):
    """Anthropic Claude API client"""

    def __init__(self):
        # Configure timeout from settings
        timeout = httpx.Timeout(
            float(settings.LLM_TIMEOUT),
            connect=float(settings.LLM_CONNECT_TIMEOUT)
        )
        self.client = AsyncAnthropic(
            api_key=settings.ANTHROPIC_API_KEY,
            timeout=timeout,
        )

    def _split_system_messages(
        self, messages: List[Dict[str, str]]
    ) -> Tuple[Optional[str], List[Dict[str, str]]]:
        """
        Extract system messages from message list.
        Claude requires system messages to be passed separately.
        """
        system_parts = []
        other_messages = []

        for msg in messages:
            if msg.get("role") == "system":
                system_parts.append(msg.get("content", ""))
            else:
                other_messages.append(msg)

        system = "\n\n".join(system_parts) if system_parts else None
        return system, other_messages

    async def chat(
        self,
        messages: List[Dict[str, str]],
        model: str,
        temperature: float = 0.2,
        max_tokens: int = 4096,
        json_mode: bool = False,
    ) -> str:
        system, chat_messages = self._split_system_messages(messages)

        # If JSON mode requested, add instruction to system prompt
        if json_mode:
            json_instruction = "\n\nIMPORTANT: You must respond with valid JSON only. No additional text or explanation outside the JSON."
            system = (system or "") + json_instruction

        response = await self.client.messages.create(
            model=model,
            max_tokens=max_tokens,
            temperature=temperature,
            system=system or "",
            messages=chat_messages,
        )

        # Extract text from response
        return response.content[0].text if response.content else ""

    async def chat_stream(
        self,
        messages: List[Dict[str, str]],
        model: str,
        temperature: float = 0.2,
        max_tokens: int = 4096,
    ) -> AsyncIterator[str]:
        system, chat_messages = self._split_system_messages(messages)

        async with self.client.messages.stream(
            model=model,
            max_tokens=max_tokens,
            temperature=temperature,
            system=system or "",
            messages=chat_messages,
        ) as stream:
            async for text in stream.text_stream:
                yield text

    async def embed(
        self,
        texts: List[str],
        model: str,
    ) -> List[List[float]]:
        """Claude does not support embeddings"""
        raise NotImplementedError(
            "Claude/Anthropic does not support embeddings. "
            "Use OpenAI or Ollama for embedding generation."
        )

    async def list_models(self) -> List[str]:
        """Return known Claude models"""
        return [
            "claude-sonnet-4-5",
            "claude-3-5-sonnet-20241022",
            "claude-3-5-haiku-20241022",
            "claude-3-opus-20240229",
            "claude-3-haiku-20240307",
        ]

    def supports_embeddings(self) -> bool:
        return False

    def supports_json_mode(self) -> bool:
        return False  # Claude doesn't have native JSON mode, we use prompt engineering
