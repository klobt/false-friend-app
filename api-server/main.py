from fastapi import Depends, FastAPI, HTTPException, Query
import auth
from dao import CardDao, EmailCodeDao, ExerciseDao, IdentityDao, SessionDao, UserDao
from model import (
    ChangePasswordRequest, ExerciseType, LoginRequest, PublicUserData,
    RegisterRequest, Session, SocialLoginRequest, TokenResponse, VerifyEmailRequest,
)

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.get("/exercises")
async def get_exercises(ids: list[int] = Query(default=[])):
    return {
        "data": ExerciseDao().get(ids)
    }

@app.get("/reviews/today")
async def get_card_exercise_ids(type_filter = Query(default=None), limit: int = Query(default=10), offset: int = Query(default=0)):
    return {
        "exercise_ids": CardDao().get_review_exercise_ids(ExerciseType[type_filter] if type_filter else None, limit, offset)
    }

@app.get("/sessions")
async def get_sessions(limit: int = Query(default=10), offset: int = Query(default=0)):
    return {
        "data": SessionDao().get(limit, offset),
        "total": SessionDao().total()
    }

@app.post("/sessions")
async def post_session(session: Session):
    return {
        "success": SessionDao().create(session)
    }

@app.get("/users")
async def get_users(limit: int = Query(default=10), offset: int = Query(default=0)):
    return {
        "data": UserDao().get(limit, offset),
        "total": UserDao().total()
    }

@app.get("/users/{user_id}")
async def get_user(user_id: int):
    return {
        "id": user_id,
        "public_data": UserDao().get_public_data(user_id)
    }

@app.put("/users/{user_id}")
async def put_user(user_id: int, user: PublicUserData):
    UserDao().set_public_data(user.data, user_id)
    return {
        "success": True
    }

@app.post("/auth/register", response_model=TokenResponse)
async def register(body: RegisterRequest):
    users = UserDao()
    if users.get_by_email(body.email):
        raise HTTPException(status_code=409, detail="Email already registered")
    user_id = users.create_with_email(body.email, auth.hash_password(body.password))
    code = auth.generate_code()
    EmailCodeDao().set(user_id, code)
    auth.send_email(body.email, "Kod weryfikacyjny", f"Twój kod weryfikacyjny: {code}")
    return TokenResponse(access_token=auth.create_token(user_id))

@app.post("/auth/verify-email")
async def verify_email(body: VerifyEmailRequest):
    user = UserDao().get_by_email(body.email)
    if not user or not EmailCodeDao().verify(user['id'], body.code):
        raise HTTPException(status_code=400, detail="Invalid or expired code")
    UserDao().set_verified(user['id'])
    return {"success": True}

@app.post("/auth/login", response_model=TokenResponse)
async def login(body: LoginRequest):
    user = UserDao().get_by_email(body.email)
    if not user or not user['password_hash'] or not auth.verify_password(body.password, user['password_hash']):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    return TokenResponse(access_token=auth.create_token(user['id']))

@app.post("/auth/social/{provider}", response_model=TokenResponse)
async def social_login(provider: str, body: SocialLoginRequest):
    provider_user_id, email = auth.verify_social_token(provider, body.id_token)
    identities = IdentityDao()
    user_id = identities.get_user_id(provider, provider_user_id)
    if user_id is None:
        users = UserDao()
        existing = users.get_by_email(email) if email else None
        user_id = existing['id'] if existing else users.create_with_email(email, None)
        if email:
            users.set_verified(user_id)
        identities.link(provider, provider_user_id, user_id)
    return TokenResponse(access_token=auth.create_token(user_id))

@app.put("/auth/password")
async def change_password(body: ChangePasswordRequest, user_id: int = Depends(auth.get_current_user_id)):
    users = UserDao()
    user = users.get_by_id(user_id)
    if not user or not user['password_hash'] or not auth.verify_password(body.current_password, user['password_hash']):
        raise HTTPException(status_code=401, detail="Invalid current password")
    users.set_password(user_id, auth.hash_password(body.new_password))
    return {"success": True}
