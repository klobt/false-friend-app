from fastapi import FastAPI, HTTPException, Query
from dao import CardDao, DeviceTokenDao, ExerciseDao, FriendDao, SessionDao, UserDao
from model import DeviceToken, ExerciseType, FriendAction, PublicUserData, PushMessage, Session
import notifications

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

@app.post("/friends/{user_id}/requests")
async def send_friend_request(user_id: int, to_user_id: int = Query(...)):
    try:
        status = FriendDao().request(user_id, to_user_id)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

    if status == 'pending':
        notifications.send_push_to_user(to_user_id, "Nowe zaproszenie", "Masz nowe zaproszenie do znajomych!")
    elif status == 'accepted':
        notifications.send_push_to_user(to_user_id, "Nowy znajomy", "Zostaliście znajomymi!")

    return {"status": status}

@app.get("/friends/{user_id}")
async def list_friends(user_id: int):
    return {"data": FriendDao().list_friends(user_id)}

@app.get("/friends/{user_id}/requests")
async def list_friend_requests(user_id: int):
    return {"data": FriendDao().list_pending(user_id)}

@app.put("/friends/{user_id}/requests/{other_id}")
async def respond_friend_request(user_id: int, other_id: int, action: FriendAction):
    if not FriendDao().respond(user_id, other_id, action.accept):
        raise HTTPException(status_code=404, detail="No pending request")

    if action.accept:
        notifications.send_push_to_user(other_id, "Nowy znajomy", "Twoje zaproszenie zostało zaakceptowane!")

    return {"success": True}

@app.delete("/friends/{user_id}/{other_id}")
async def remove_friend(user_id: int, other_id: int):
    FriendDao().remove(user_id, other_id)
    return {"success": True}

@app.post("/notifications/{user_id}/tokens")
async def register_token(user_id: int, body: DeviceToken):
    DeviceTokenDao().upsert(user_id, body.token, body.platform)
    return {"success": True}

@app.delete("/notifications/tokens/{token}")
async def unregister_token(token: str):
    DeviceTokenDao().remove(token)
    return {"success": True}

@app.post("/notifications/{user_id}/send")
async def send_notification(user_id: int, body: PushMessage):
    notifications.send_push_to_user(user_id, body.title, body.body, body.data)
    return {"success": True}
