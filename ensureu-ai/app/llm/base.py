"""
Abstract base class for LLM clients
"""
from abc import ABC, abstractmethod
from typing import List, Dict, AsyncIterator, Optional


class BaseLLMClient(ABC):
    """Abstract base class for all LLM provider clients"""

    @abstractmethod
    async def chat(
        self,
        messages: List[Dict[str, str]],
        model: str,
        temperature: float = 0.2,
        max_tokens: int = 4096,
        json_mode: bool = False,
    ) -> str:
        """
        Send messages to LLM and get response.

        Args:
            messages: List of message dicts with 'role' and 'content'
            model: Model identifier
            temperature: Sampling temperature (0-2)
            max_tokens: Maximum tokens in response
            json_mode: If True, request JSON output format

        Returns:
            Model's response text
        """
        pass

    @abstractmethod
    async def chat_stream(
        self,
        messages: List[Dict[str, str]],
        model: str,
        temperature: float = 0.2,
        max_tokens: int = 4096,
    ) -> AsyncIterator[str]:
        """
        Stream chat response token by token.

        Yields:
            Individual tokens from the response
        """
        pass

    @abstractmethod
    async def embed(
        self,
        texts: List[str],
        model: str,
    ) -> List[List[float]]:
        """
        Generate embeddings for texts.

        Args:
            texts: List of strings to embed
            model: Embedding model identifier

        Returns:
            List of embedding vectors
        """
        pass

    @abstractmethod
    async def list_models(self) -> List[str]:
        """List available models for this provider"""
        pass

    def supports_embeddings(self) -> bool:
        """Check if this provider supports embeddings"""
        return True

    def supports_json_mode(self) -> bool:
        """Check if this provider supports JSON output mode"""
        return False
