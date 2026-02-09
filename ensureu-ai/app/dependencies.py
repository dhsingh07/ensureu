"""
FastAPI Dependencies for Authentication and Feature Flags
"""
import jwt
from typing import Optional
from fastapi import Header, HTTPException, Depends
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase

from .config import settings


# =============================================================================
# DATABASE
# =============================================================================

_mongo_client: Optional[AsyncIOMotorClient] = None


async def get_db() -> AsyncIOMotorDatabase:
    """Get MongoDB database connection"""
    global _mongo_client
    if _mongo_client is None:
        _mongo_client = AsyncIOMotorClient(settings.MONGODB_URI)
    return _mongo_client[settings.MONGODB_DB]


async def close_db():
    """Close MongoDB connection"""
    global _mongo_client
    if _mongo_client is not None:
        _mongo_client.close()
        _mongo_client = None


# =============================================================================
# AUTHENTICATION
# =============================================================================

async def get_current_user(
    authorization: str = Header(default=""),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> dict:
    """
    Extract and validate JWT token from Authorization header.

    Returns user payload from token.
    For development, allows requests without token.
    """
    import logging
    logger = logging.getLogger(__name__)

    # In DEBUG mode, skip JWT validation entirely
    if settings.DEBUG:
        # Extract user info from token if possible, otherwise use debug defaults
        if authorization:
            try:
                parts = authorization.split()
                if len(parts) == 2:
                    token = parts[1]
                    # Decode without verification in debug mode
                    payload = jwt.decode(token, options={"verify_signature": False})
                    roles = payload.get("roles", [])
                    return {
                        "user_id": payload.get("sub") or payload.get("userId") or "debug_user",
                        "username": payload.get("username") or payload.get("sub"),
                        "roles": roles if roles else ["USER", "ADMIN", "SUPERADMIN"],
                        "email": payload.get("email"),
                    }
            except Exception:
                pass
        return {"user_id": "debug_user", "roles": ["USER", "ADMIN", "SUPERADMIN"]}

    if not authorization:
        raise HTTPException(
            status_code=401,
            detail="Authorization header required",
        )

    # Extract token from "Bearer <token>" format
    parts = authorization.split()
    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise HTTPException(
            status_code=401,
            detail="Invalid authorization header format. Use: Bearer <token>",
        )

    token = parts[1]

    try:
        # Decode JWT token - accept both HS256 and HS512 for compatibility
        payload = jwt.decode(
            token,
            settings.JWT_SECRET,
            algorithms=["HS256", "HS512"],
        )

        # Extract roles - handle different formats from Java backend
        # Java may store as "roles", "authorities", or nested in "auth"
        roles = payload.get("roles", [])
        if not roles:
            roles = payload.get("authorities", [])
        if not roles and "auth" in payload:
            roles = payload["auth"].get("roles", [])

        # Handle case where roles are objects with "authority" field
        if roles and isinstance(roles[0], dict):
            roles = [r.get("authority", r.get("role", "")) for r in roles]

        logger.debug(f"JWT payload: {payload}")
        logger.debug(f"Extracted roles: {roles}")

        return {
            "user_id": payload.get("sub") or payload.get("userId"),
            "username": payload.get("username") or payload.get("sub"),
            "roles": roles,
            "email": payload.get("email"),
        }

    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=401,
            detail="Token has expired",
        )
    except jwt.InvalidTokenError as e:
        raise HTTPException(
            status_code=401,
            detail=f"Invalid token: {str(e)}",
        )


async def get_optional_user(
    authorization: str = Header(default=""),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> Optional[dict]:
    """
    Optionally extract user from token.
    Returns None if no valid token provided.
    """
    if not authorization:
        return None

    try:
        return await get_current_user(authorization, db)
    except HTTPException:
        return None


def require_role(*required_roles: str):
    """
    Dependency factory to require specific roles.

    Usage:
        @router.get("/admin-only", dependencies=[Depends(require_role("ADMIN", "SUPERADMIN"))])
    """
    async def _check_role(user: dict = Depends(get_current_user)):
        user_roles = user.get("roles", [])

        # Normalize role names (handle ROLE_ prefix)
        normalized_user_roles = set()
        for role in user_roles:
            normalized_user_roles.add(role)
            if role.startswith("ROLE_"):
                normalized_user_roles.add(role[5:])
            else:
                normalized_user_roles.add(f"ROLE_{role}")

        normalized_required = set()
        for role in required_roles:
            normalized_required.add(role)
            if role.startswith("ROLE_"):
                normalized_required.add(role[5:])
            else:
                normalized_required.add(f"ROLE_{role}")

        if not normalized_user_roles.intersection(normalized_required):
            raise HTTPException(
                status_code=403,
                detail=f"Requires one of roles: {', '.join(required_roles)}",
            )

        return user

    return _check_role


# =============================================================================
# FEATURE FLAGS
# =============================================================================

async def check_feature_enabled(
    feature_key: str,
    db: AsyncIOMotorDatabase,
    user_id: Optional[str] = None,
) -> bool:
    """
    Check if a feature is enabled.

    Checks:
    1. Global feature flag in app_config
    2. User-specific override if user_id provided
    """
    # Check global config
    config = await db.app_config.find_one({"type": "feature_flags"})
    if config:
        features = config.get("features", {})
        if not features.get(feature_key, {}).get("enabled", True):
            return False

    # Check user-specific override
    if user_id:
        user_config = await db.user_feature_flags.find_one({"user_id": user_id})
        if user_config:
            user_features = user_config.get("features", {})
            if feature_key in user_features:
                return user_features[feature_key]

    return True


def require_feature(feature_key: str):
    """
    Dependency factory to require a feature to be enabled.

    Usage:
        @router.get("/ai-chat", dependencies=[Depends(require_feature("ai_chat"))])
    """
    async def _check_feature(
        user: dict = Depends(get_current_user),
        db: AsyncIOMotorDatabase = Depends(get_db),
    ):
        enabled = await check_feature_enabled(
            feature_key,
            db,
            user.get("user_id"),
        )
        if not enabled:
            raise HTTPException(
                status_code=403,
                detail=f"Feature '{feature_key}' is not available",
            )
        return user

    return _check_feature
