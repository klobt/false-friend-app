import json, os, random, smtplib
from datetime import datetime, timedelta, timezone
from email.message import EmailMessage

import bcrypt
import dotenv
import httpx
import jwt
from fastapi import Header, HTTPException

dotenv.load_dotenv()

JWT_SECRET = os.getenv('JWT_SECRET', 'dev-secret')
JWT_TTL_MIN = 60 * 24 * 30

# (issuer, jwks_uri, expected audience) for verifying provider-issued OIDC id_tokens
OIDC_PROVIDERS = {
    'google': ('https://accounts.google.com', 'https://www.googleapis.com/oauth2/v3/certs', os.getenv('GOOGLE_CLIENT_ID')),
    'apple': ('https://appleid.apple.com', 'https://appleid.apple.com/auth/keys', os.getenv('APPLE_CLIENT_ID')),
    'facebook': ('https://www.facebook.com', 'https://www.facebook.com/.well-known/oauth/openid/jwks/', os.getenv('FACEBOOK_APP_ID')),
}

def hash_password(password: str) -> str:
    return bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()

def verify_password(password: str, password_hash: str) -> bool:
    return bcrypt.checkpw(password.encode(), password_hash.encode())

def create_token(user_id: int) -> str:
    payload = {'sub': str(user_id), 'exp': datetime.now(timezone.utc) + timedelta(minutes=JWT_TTL_MIN)}
    return jwt.encode(payload, JWT_SECRET, algorithm='HS256')

def get_current_user_id(authorization: str = Header(...)) -> int:
    try:
        token = authorization.removeprefix('Bearer ').strip()
        payload = jwt.decode(token, JWT_SECRET, algorithms=['HS256'])
        return int(payload['sub'])
    except Exception:
        raise HTTPException(status_code=401, detail='Invalid or expired token')

def generate_code() -> str:
    return f'{random.randint(0, 999999):06d}'

def send_email(to: str, subject: str, body: str) -> None:
    host = os.getenv('SMTP_HOST')
    if not host:
        print(f'[email to {to}] {subject}: {body}')  # dev fallback, no SMTP configured
        return
    msg = EmailMessage()
    msg['From'] = os.getenv('SMTP_FROM', 'noreply@falsefriend.app')
    msg['To'], msg['Subject'] = to, subject
    msg.set_content(body)
    with smtplib.SMTP(host, int(os.getenv('SMTP_PORT', 587))) as s:
        s.starttls()
        s.login(os.getenv('SMTP_USER'), os.getenv('SMTP_PASSWORD'))
        s.send_message(msg)

def verify_social_token(provider: str, id_token: str) -> tuple[str, str | None]:
    """Verifies a provider-issued OIDC id_token (as obtained by the mobile app's
    native Google/Facebook-Limited-Login/Apple sign-in SDK) and returns
    (provider_user_id, email)."""
    if provider not in OIDC_PROVIDERS:
        raise HTTPException(status_code=400, detail='Unknown provider')
    issuer, jwks_uri, audience = OIDC_PROVIDERS[provider]
    jwks = httpx.get(jwks_uri, timeout=5).json()
    kid = jwt.get_unverified_header(id_token)['kid']
    key = next(k for k in jwks['keys'] if k['kid'] == kid)
    public_key = jwt.algorithms.RSAAlgorithm.from_jwk(json.dumps(key))
    claims = jwt.decode(id_token, public_key, algorithms=['RS256'], audience=audience, issuer=issuer)
    return claims['sub'], claims.get('email')
