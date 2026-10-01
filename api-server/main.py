from fastapi import Depends, FastAPI, HTTPException, Query
import auth
import clock
from datetime import timedelta
from dao import CardDao, DeviceTokenDao, EmailCodeDao, ExerciseDao, FriendDao, IdentityDao, SessionDao, StatsDao, UserDao
from model import (
    ChangePasswordRequest, DeviceToken, ExerciseType, FriendAction, LoginRequest, NicknameRequest, PublicUserData, PushMessage,
    RegisterRequest, Session, TimeAdvanceRequest, SocialLoginRequest, TokenResponse, UserStats, VerifyEmailRequest,
)
import notifications

app = FastAPI()

@app.get("/")
async def root(user_id: int = Depends(auth.get_current_user_id)):
    return {"message": "Hello World"}

@app.get("/exercises")
async def get_exercises(ids: list[int] = Query(default=[]), user_id: int = Depends(auth.get_current_user_id)):
    return {
        "data": ExerciseDao().get(ids)
    }

@app.get("/reviews/today")
async def get_card_exercise_ids(type_filter = Query(default=None), limit: int = Query(default=10), offset: int = Query(default=0), user_id: int = Depends(auth.get_current_user_id)):
    return {
        "exercise_ids": CardDao().get_review_exercise_ids(ExerciseType[type_filter] if type_filter else None, limit, offset, user_id)
    }

@app.get("/sessions")
async def get_sessions(limit: int = Query(default=10), offset: int = Query(default=0), user_id: int = Depends(auth.get_current_user_id)):
    return {
        "data": SessionDao().get(limit, offset, user_id),
        "total": SessionDao().total_for_user(user_id)
    }

@app.post("/sessions")
async def post_session(session: Session, user_id: int = Depends(auth.get_current_user_id)):
    session.user_id = user_id
    return {
        "success": SessionDao().create(session)
    }

@app.get("/users")
async def get_users(limit: int = Query(default=10), offset: int = Query(default=0), user_id: int = Depends(auth.get_current_user_id)):
    return {
        "data": UserDao().get(limit, offset),
        "total": UserDao().total()
    }

@app.get("/users/me")
async def get_user(user_id: int = Depends(auth.get_current_user_id)):
    user = UserDao().get_by_id(user_id)
    return {
        "id": user_id,
        "nickname": user['nickname'] if user else None,
        "public_data": UserDao().get_public_data(user_id)
    }

@app.put("/users/me/nickname")
async def put_nickname(body: NicknameRequest, user_id: int = Depends(auth.get_current_user_id)):
    nickname = body.nickname.strip()
    if not nickname:
        raise HTTPException(status_code=400, detail="Nickname cannot be empty")
    UserDao().set_nickname(user_id, nickname)
    return {"success": True, "nickname": nickname}

@app.put("/users/me")
async def put_user(user: PublicUserData, user_id: int = Depends(auth.get_current_user_id)):
    UserDao().set_public_data(user.data, user_id)
    return {
        "success": True
    }

@app.post("/friends/requests")
async def send_friend_request(to_user_id: int = Query(...), user_id: int = Depends(auth.get_current_user_id)):
    try:
        status = FriendDao().request(user_id, to_user_id)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

    if status == 'pending':
        notifications.send_push_to_user(to_user_id, "Nowe zaproszenie", "Masz nowe zaproszenie do znajomych!")
    elif status == 'accepted':
        notifications.send_push_to_user(to_user_id, "Nowy znajomy", "Zostaliście znajomymi!")

    return {"status": status}

@app.get("/friends")
async def list_friends(user_id: int = Depends(auth.get_current_user_id)):
    return {"data": FriendDao().list_friends(user_id)}

@app.get("/friends/requests")
async def list_friend_requests(user_id: int = Depends(auth.get_current_user_id)):
    return {"data": FriendDao().list_pending(user_id)}

@app.put("/friends/requests/{other_id}")
async def respond_friend_request(other_id: int, action: FriendAction, user_id: int = Depends(auth.get_current_user_id)):
    if not FriendDao().respond(user_id, other_id, action.accept):
        raise HTTPException(status_code=404, detail="No pending request")

    if action.accept:
        notifications.send_push_to_user(other_id, "Nowy znajomy", "Twoje zaproszenie zostało zaakceptowane!")

    return {"success": True}

@app.delete("/friends/{other_id}")
async def remove_friend(other_id: int, user_id: int = Depends(auth.get_current_user_id)):
    FriendDao().remove(user_id, other_id)
    return {"success": True}

@app.post("/notifications/tokens")
async def register_token(body: DeviceToken, user_id: int = Depends(auth.get_current_user_id)):
    DeviceTokenDao().upsert(user_id, body.token, body.platform)
    return {"success": True}

@app.delete("/notifications/tokens/{token}")
async def unregister_token(token: str, user_id: int = Depends(auth.get_current_user_id)):
    if not DeviceTokenDao().remove_owned(token, user_id):
        raise HTTPException(status_code=404, detail="Token not found")
    return {"success": True}

@app.post("/notifications/send")
async def send_notification(body: PushMessage, user_id: int = Depends(auth.get_current_user_id)):
    notifications.send_push_to_user(user_id, body.title, body.body, body.data)
    return {"success": True}

@app.get("/users/me/stats", response_model=UserStats)
async def get_user_stats(user_id: int = Depends(auth.get_current_user_id)):
    return StatsDao().get(user_id)

@app.post("/auth/register")
async def register(body: RegisterRequest):
    users = UserDao()
    existing = users.get_by_email(body.email)
    if existing and existing['email_verified']:
        raise HTTPException(status_code=409, detail="Email already registered")
    password_hash = auth.hash_password(body.password)
    if existing:
        # never-verified account: let the person retry with a fresh code
        user_id = existing['id']
        users.set_password(user_id, password_hash)
    else:
        user_id = users.create_with_email(body.email, password_hash)
    if not auth.REQUIRE_EMAIL_VERIFICATION:
        users.set_verified(user_id)
        return {"success": True, "verification_required": False, "access_token": auth.create_token(user_id), "token_type": "bearer"}
    code = auth.generate_code()
    EmailCodeDao().set(user_id, code)
    auth.send_email(body.email, "Kod weryfikacyjny", f"Twój kod weryfikacyjny: {code}")
    return {"success": True, "verification_required": True}

@app.post("/auth/verify-email", response_model=TokenResponse)
async def verify_email(body: VerifyEmailRequest):
    user = UserDao().get_by_email(body.email)
    if not user or not EmailCodeDao().verify(user['id'], body.code):
        raise HTTPException(status_code=400, detail="Invalid or expired code")
    UserDao().set_verified(user['id'])
    return TokenResponse(access_token=auth.create_token(user['id']))

@app.post("/auth/login", response_model=TokenResponse)
async def login(body: LoginRequest):
    user = UserDao().get_by_email(body.email)
    if not user or not user['password_hash'] or not auth.verify_password(body.password, user['password_hash']):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    if not user['email_verified']:
        raise HTTPException(status_code=403, detail="Email not verified")
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

def _require_time_control():
    if not clock.ENABLED:
        raise HTTPException(status_code=404, detail="Not found")

def _time_state():
    return {"now": clock.utcnow().isoformat(), "offset_seconds": int(clock.offset().total_seconds())}

@app.get("/debug/time")
async def get_time(user_id: int = Depends(auth.get_current_user_id)):
    _require_time_control()
    return _time_state()

@app.post("/debug/time/advance")
async def advance_time(body: TimeAdvanceRequest, user_id: int = Depends(auth.get_current_user_id)):
    _require_time_control()
    clock.advance(timedelta(days=body.days, hours=body.hours, minutes=body.minutes))
    return _time_state()

@app.post("/debug/time/reset")
async def reset_time(user_id: int = Depends(auth.get_current_user_id)):
    _require_time_control()
    clock.reset()
    return _time_state()
