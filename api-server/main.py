from fastapi import FastAPI, Query
from dao import ExerciseDao, SessionDao, UserDao
from model import PublicUserData, Session

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.get("/exercises/")
async def get_exercises(ids: list[int] = Query(default=[])):
    return {
        "data": ExerciseDao().get(ids)
    }


@app.get("/sessions/")
async def get_sessions(limit: int = Query(default=10), offset: int = Query(default=0)):
    return {
        "data": SessionDao().get(limit, offset),
        "total": SessionDao().total()
    }

@app.post("/sessions/")
async def post_session(session: Session):
    SessionDao().create(session)
    return {
        "success": True
    }

@app.get("/users/")
async def get_users(limit: int = Query(default=10), offset: int = Query(default=0)):
    return {
        "data": UserDao().get(limit, offset),
        "total": UserDao().total()
    }

@app.get("/users/{user_id}/")
async def get_user(user_id: int):
    return {
        "id": user_id,
        "public_data": UserDao().get_public_data(user_id)
    }

@app.put("/users/{user_id}/")
async def put_user(user_id: int, user: PublicUserData):
    UserDao().set_public_data(user.data, user_id)
    return {
        "success": True
    }
