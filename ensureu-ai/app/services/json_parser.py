"""
JSON Parser Service for LLM Responses
"""
import json
import re
from typing import Any, Dict


def parse_llm_json(text: str) -> Dict[str, Any]:
    """
    Parse JSON from LLM response text.

    Handles cases where:
    - Response is pure JSON
    - JSON is wrapped in markdown code blocks
    - JSON is embedded in explanatory text

    Args:
        text: Raw LLM response text

    Returns:
        Parsed JSON as dictionary

    Raises:
        ValueError: If no valid JSON found
    """
    text = text.strip()

    # Try direct JSON parse first
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        pass

    # Try extracting from markdown code block
    code_block_pattern = r"```(?:json)?\s*([\s\S]*?)```"
    matches = re.findall(code_block_pattern, text)
    for match in matches:
        try:
            return json.loads(match.strip())
        except json.JSONDecodeError:
            continue

    # Try finding JSON object in text
    # Look for outermost { } pair
    brace_start = text.find("{")
    brace_end = text.rfind("}")

    if brace_start != -1 and brace_end != -1 and brace_end > brace_start:
        potential_json = text[brace_start : brace_end + 1]
        try:
            return json.loads(potential_json)
        except json.JSONDecodeError:
            pass

    # Try finding JSON array
    bracket_start = text.find("[")
    bracket_end = text.rfind("]")

    if bracket_start != -1 and bracket_end != -1 and bracket_end > bracket_start:
        potential_json = text[bracket_start : bracket_end + 1]
        try:
            return json.loads(potential_json)
        except json.JSONDecodeError:
            pass

    # Last resort: try to fix common JSON issues
    fixed_text = _attempt_json_fix(text)
    if fixed_text:
        try:
            return json.loads(fixed_text)
        except json.JSONDecodeError:
            pass

    raise ValueError(f"Could not parse valid JSON from LLM response. Response started with: {text[:200]}")


def _attempt_json_fix(text: str) -> str | None:
    """
    Attempt to fix common JSON formatting issues from LLM output.
    """
    # Remove any leading/trailing non-JSON text
    text = text.strip()

    # Find the JSON portion
    start = text.find("{")
    if start == -1:
        start = text.find("[")
    if start == -1:
        return None

    text = text[start:]

    # Fix trailing comma before closing brace/bracket
    text = re.sub(r",\s*}", "}", text)
    text = re.sub(r",\s*]", "]", text)

    # Fix single quotes to double quotes (careful with content)
    # Only do this for keys, not values
    text = re.sub(r"'(\w+)':", r'"\1":', text)

    # Fix unquoted keys
    text = re.sub(r"(\{|\,)\s*(\w+)\s*:", r'\1"\2":', text)

    return text


def extract_json_safe(text: str, default: Dict[str, Any] | None = None) -> Dict[str, Any]:
    """
    Safely extract JSON with a default fallback.

    Args:
        text: LLM response text
        default: Default value if parsing fails

    Returns:
        Parsed JSON or default value
    """
    try:
        return parse_llm_json(text)
    except ValueError:
        return default or {}
